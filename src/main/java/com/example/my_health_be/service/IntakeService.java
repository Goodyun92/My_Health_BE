package com.example.my_health_be.service;

import com.example.my_health_be.domain.enums.ErrorCode;
import com.example.my_health_be.domain.enums.Gender;
import com.example.my_health_be.domain.intake.DailyIntake;
import com.example.my_health_be.domain.intake.TaskIntake;
import com.example.my_health_be.domain.user.User;
import com.example.my_health_be.domain.user.UserProfile;
import com.example.my_health_be.dto.intake.DailyIntakeRequestDto;
import com.example.my_health_be.dto.intake.DailyIntakeReturnDto;
import com.example.my_health_be.dto.intake.TaskIntakeRequestDto;
import com.example.my_health_be.dto.intake.TaskIntakeReturnDto;
import com.example.my_health_be.exception.AppException;
import com.example.my_health_be.repository.intake.DailyIntakeRepository;
import com.example.my_health_be.repository.intake.TaskIntakeRepository;
import com.example.my_health_be.repository.user.UserProfileRepository;
import com.example.my_health_be.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.my_health_be.util.CalorieUtil.calcIntakeTargetCalorie;

@Service
@RequiredArgsConstructor
public class IntakeService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final DailyIntakeRepository dailyIntakeRepository;
    private final TaskIntakeRepository taskIntakeRepository;

    @Transactional
    public DailyIntakeReturnDto getDailyIntake (DailyIntakeRequestDto dto, String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        // 사용자 ID와 날짜로 검색
        Optional<DailyIntake> dailyIntakeOpt = dailyIntakeRepository.findByUserAndDate(user,dto.getDate());

        // 존재하면 반환, 없으면 새로 생성
        DailyIntake dailyIntake = dailyIntakeOpt.orElseGet(() -> {
            Optional<UserProfile> userProfile = userProfileRepository.findByUser(user);

            //성별, 체중, 키, 나이
            Gender gender = userProfile.map(UserProfile::getGender).orElse(Gender.FEMALE);
            Double weight = userProfile.map(UserProfile::getWeight).orElse(55.0);
            Double height = userProfile.map(UserProfile::getHeight).orElse(160.5);
            int age;

            Optional<LocalDate> birthOpt = userProfile.map(UserProfile::getBirth);
            if (birthOpt.isPresent()) {
                LocalDate birth = birthOpt.get();
                LocalDate today = LocalDate.now();
                age = Period.between(birth, today).getYears();
            } else {
                age = 25; // 기본 나이값
            }

            Double targetCalorie = calcIntakeTargetCalorie(gender,weight,height,age);

            DailyIntake newDailyIntake = DailyIntake.builder()
                    .user(user)
                    .date(dto.getDate())
                    .targetCalorie(targetCalorie)
                    .currentCalorie(0.0)
                    .build();

            return dailyIntakeRepository.save(newDailyIntake);
        });

        List<TaskIntakeReturnDto> taskIntakeList = taskIntakeRepository.findByDailyIntake(dailyIntake)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_INTAKE_NOT_FOUND, "섭취 리스트를 찾는 과정에 오류가 발생했습니다."))
                .stream()
                .map(taskIntake -> new TaskIntakeReturnDto(taskIntake.getId(), taskIntake.getContent(), taskIntake.getCalorie()))
                .collect(Collectors.toList());

        return DailyIntakeReturnDto.builder()
                .userName(userName)
                .date(dailyIntake.getDate())
                .targetCalorie(dailyIntake.getTargetCalorie())
                .currentCalorie(dailyIntake.getCurrentCalorie())
                .intakes(taskIntakeList)
                .build();
    }

    @Transactional
    public void postIntakeTask(TaskIntakeRequestDto dto, String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        DailyIntake dailyIntake = dailyIntakeRepository.findByUserAndDate(user,dto.getDate())
                .orElseThrow(() -> new AppException(ErrorCode.DAILY_INTAKE_NOT_FOUND, dto.getDate() + "의 정보가 없습니다. 먼저 해당 날짜의 섭취를 조회해주세요."));

        TaskIntake taskIntake = TaskIntake.builder()
                .dailyIntake(dailyIntake)
                .content(dto.getContent())
                .calorie(dto.getCalorie())
                .build();

        taskIntakeRepository.save(taskIntake);

        //daily의 current calorie에 더하기
        dailyIntake.setCurrentCalorie(dailyIntake.getCurrentCalorie()+dto.getCalorie());
        dailyIntakeRepository.save(dailyIntake);
    }

    @Transactional
    public void deleteIntakeTask(Long id, String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        TaskIntake taskIntake = taskIntakeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_INTAKE_NOT_FOUND, "Intake task"+ id + "가 존재하지않습니다."));

        if(!userName.equals(taskIntake.getDailyIntake().getUser().getUserName())){
            throw new AppException(ErrorCode.TASK_FORBIDDEN, "권한이 없습니다.");
        }

        DailyIntake dailyIntake = dailyIntakeRepository.findByUserAndDate(user,taskIntake.getDailyIntake().getDate())
                .orElseThrow(() -> new AppException(ErrorCode.DAILY_INTAKE_NOT_FOUND, taskIntake.getDailyIntake().getDate() + "의 정보가 없습니다. 먼저 해당 날짜의 섭취를 조회해주세요."));

        //current calorie에 반영
        dailyIntake.setCurrentCalorie(dailyIntake.getCurrentCalorie()- taskIntake.getCalorie());
        dailyIntakeRepository.save(dailyIntake);

        //삭제
        taskIntakeRepository.deleteById(id);
    }
}
