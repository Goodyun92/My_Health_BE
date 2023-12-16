package com.example.my_health_be.service;

import com.example.my_health_be.domain.enums.ErrorCode;
import com.example.my_health_be.domain.exercise.DailyExercise;
import com.example.my_health_be.domain.exercise.TaskExercise;
import com.example.my_health_be.domain.user.User;
import com.example.my_health_be.domain.user.UserProfile;
import com.example.my_health_be.dto.exercise.DailyExerciseReturnDto;
import com.example.my_health_be.dto.exercise.TaskExerciseRequestDto;
import com.example.my_health_be.dto.exercise.TaskExerciseReturnDto;
import com.example.my_health_be.exception.AppException;
import com.example.my_health_be.repository.exercise.DailyExerciseRepository;
import com.example.my_health_be.repository.exercise.TaskExerciseRepository;
import com.example.my_health_be.repository.user.UserProfileRepository;
import com.example.my_health_be.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.my_health_be.util.CalorieUtil.calcExerciseTargetCalorie;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final DailyExerciseRepository dailyExerciseRepository;
    private final TaskExerciseRepository taskExerciseRepository;

    @Transactional
    public DailyExerciseReturnDto getDailyExercise(LocalDate date, String userName){

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        // 사용자 ID와 날짜로 검색
        Optional<DailyExercise> dailyExerciseOpt = dailyExerciseRepository.findByUserAndDate(user, date);

        // 존재하면 반환, 없으면 새로 생성
        DailyExercise dailyExercise = dailyExerciseOpt.orElseGet(() -> {
            Optional<UserProfile> userProfile = userProfileRepository.findByUser(user);
            Double weight = userProfile.map(UserProfile::getWeight).orElse(55.0);

            Double targetCalorie = calcExerciseTargetCalorie(weight);

            DailyExercise newDailyExercise = DailyExercise.builder()
                    .user(user)
                    .date(date)
                    .targetCalorie(targetCalorie)
                    .currentCalorie(0.0)
                    .build();

            return dailyExerciseRepository.save(newDailyExercise);
        });

        List<TaskExerciseReturnDto> taskExerciseList = taskExerciseRepository.findByDailyExercise(dailyExercise)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_EXERCISE_NOT_FOUND, "운동 리스트를 찾는 과정에 오류가 발생했습니다."))
                .stream()
                .map(taskExercise -> new TaskExerciseReturnDto(taskExercise.getId(),taskExercise.getContent(), taskExercise.getCalorie()))
                .collect(Collectors.toList());

        return DailyExerciseReturnDto.builder()
                .userName(userName)
                .date(dailyExercise.getDate())
                .targetCalorie(dailyExercise.getTargetCalorie())
                .currentCalorie(dailyExercise.getCurrentCalorie())
                .exercises(taskExerciseList)
                .build();
    }

    @Transactional
    public void postExerciseTask(TaskExerciseRequestDto dto, String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        DailyExercise dailyExercise = dailyExerciseRepository.findByUserAndDate(user,dto.getDate())
                .orElseThrow(() -> new AppException(ErrorCode.DAILY_EXERCISE_NOT_FOUND, dto.getDate() + "의 정보가 없습니다. 먼저 해당 날짜의 운동을 조회해주세요."));

        TaskExercise taskExercise = TaskExercise.builder()
                .dailyExercise(dailyExercise)
                .content(dto.getContent())
                .calorie(dto.getCalorie())
                .build();

        taskExerciseRepository.save(taskExercise);

        //daily의 current calorie에 더하기
        dailyExercise.setCurrentCalorie(dailyExercise.getCurrentCalorie()+dto.getCalorie());
        dailyExerciseRepository.save(dailyExercise);
    }

    @Transactional
    public void deleteExerciseTask(Long id, String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        TaskExercise taskExercise = taskExerciseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_EXERCISE_NOT_FOUND, "Exercise task"+ id + "가 존재하지않습니다."));

        if(!userName.equals(taskExercise.getDailyExercise().getUser().getUserName())){
            throw new AppException(ErrorCode.TASK_FORBIDDEN, "권한이 없습니다.");
        }

        DailyExercise dailyExercise = dailyExerciseRepository.findByUserAndDate(user,taskExercise.getDailyExercise().getDate())
                .orElseThrow(() -> new AppException(ErrorCode.DAILY_EXERCISE_NOT_FOUND, taskExercise.getDailyExercise().getDate() + "의 정보가 없습니다. 먼저 해당 날짜의 운동을 조회해주세요."));

        //current calorie에 반영
        dailyExercise.setCurrentCalorie(dailyExercise.getCurrentCalorie()- taskExercise.getCalorie());
        dailyExerciseRepository.save(dailyExercise);

        //삭제
        taskExerciseRepository.deleteById(id);
    }
}
