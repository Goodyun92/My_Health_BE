package com.example.my_health_be.service;

import com.example.my_health_be.domain.exercise.DailyExercise;
import com.example.my_health_be.domain.intake.DailyIntake;
import com.example.my_health_be.domain.user.Inbody;
import com.example.my_health_be.domain.user.User;
import com.example.my_health_be.domain.user.UserProfile;
import com.example.my_health_be.dto.user.UserInbodyRequest;
import com.example.my_health_be.dto.user.UserJoinRequest;
import com.example.my_health_be.dto.user.UserProfileRequest;
import com.example.my_health_be.exception.AppException;
import com.example.my_health_be.domain.enums.ErrorCode;
import com.example.my_health_be.repository.exercise.DailyExerciseRepository;
import com.example.my_health_be.repository.intake.DailyIntakeRepository;
import com.example.my_health_be.repository.user.InbodyRepository;
import com.example.my_health_be.repository.user.UserProfileRepository;
import com.example.my_health_be.repository.user.UserRepository;
import com.example.my_health_be.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.example.my_health_be.domain.enums.Role.ROLE_USER;
import static com.example.my_health_be.util.CalorieUtil.calcExerciseTargetCalorie;
import static com.example.my_health_be.util.CalorieUtil.calcIntakeTargetCalorie;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InbodyRepository inbodyRepository;
    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder encoder;
    private final DailyExerciseRepository dailyExerciseRepository;
    private final DailyIntakeRepository dailyIntakeRepository;

    @Value("${jwt.token.secret}")
    private String acessKey; //secret key

    @Value("${jwt.token.refresh}")
    private String refreshKey;

    @Transactional
    public void join(UserJoinRequest dto){

        String userName = dto.getUserName();
        String password = dto.getPassword();
        String nickName = dto.getNickName();

        // userName 중복 check
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는 이미 존재합니다.");
                        }
                );

        // nickName 중복 check
        userRepository.findByNickName(nickName)
                .ifPresent(user -> {
                            throw new AppException(ErrorCode.NICKNAME_DUPLICATED, nickName + "는 이미 존재합니다.");
                        }
                );

        // 유저 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .nickName(nickName)
                .role(ROLE_USER)
                .build();
        userRepository.save(user);

        // 인바디 생성
        Inbody inbody = Inbody.builder()
                .user(user)
                .build();
        inbodyRepository.save(inbody);

        // 프로필 생성
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();
        userProfileRepository.save(userProfile);

    }

    public String login(String userName, String password){

        //userName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        //password 틀림
        if(!encoder.matches(password, selectedUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }

        //access token 발행
        return getAccessToken(selectedUser.getUserName());
    }

    public User updateUserName(String userName, String nickName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        // nickName 중복 check
        userRepository.findByNickName(nickName)
                .ifPresent(thatuser -> {
                            throw new AppException(ErrorCode.NICKNAME_DUPLICATED, nickName + "는 이미 존재합니다.");
                        }
                );


        user.setNickName(nickName);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public UserProfile updateProfile(String userName, UserProfileRequest dto){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND, "사용자"+ userName + "의 프로필을 찾을 수 없습니다."));

        userProfile.setBirth(dto.getBirth());
        userProfile.setGender(dto.getGender());
        userProfile.setWeight(dto.getWeight());
        userProfile.setHeight(dto.getHeight());

        userProfileRepository.save(userProfile);



        /////////// 업데이트된 유저 프로필로 일일 목표 수치 조정
        ///////////

        //  변경된 target calorie 계산
        LocalDate today = LocalDate.now();
        Integer age = Period.between(userProfile.getBirth(), today).getYears();
        Double newTargetIntake = calcIntakeTargetCalorie(userProfile.getGender(),userProfile.getWeight(),userProfile.getHeight(),age);
        Double newTargetExercise = calcExerciseTargetCalorie(userProfile.getWeight());

        //오늘과 그 이후의 dailyExercise 레코드에 적용
        List<DailyExercise> exercises = dailyExerciseRepository.findByUserAndDateGreaterThanEqual(user, today);
        for (DailyExercise exercise : exercises) {
            exercise.setTargetCalorie(newTargetExercise);
        }
        dailyExerciseRepository.saveAll(exercises);

        //오늘과 그 이후의 dailyIntake 레코드에 적용
        List<DailyIntake> intakes = dailyIntakeRepository.findByUserAndDateGreaterThanEqual(user, today);
        for (DailyIntake intake : intakes) {
            intake.setTargetCalorie(newTargetIntake);
        }
        dailyIntakeRepository.saveAll(intakes);

        ///////////
        ///////////


        return userProfile;
    }

    public Inbody updateInbody(String userName, UserInbodyRequest dto){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+ userName + "이 없습니다."));

        Inbody inbody = inbodyRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INBODY_NOT_FOUND, "사용자"+ userName + "의 인바디를 찾을 수 없습니다."));

        inbody.setBmi(dto.getBmi());
        inbody.setSkeletalMuscle(dto.getSkeletalMuscle());
        inbody.setFatPer(dto.getFatPer());

        inbodyRepository.save(inbody);
        return inbody;
    }

    public String getAccessToken(String userName){
//        Long accessExpireTimeMs = 1000 * 60 * 60L;
        Long accessExpireTimeMs = 1000 *60 * 60 *24 *30L;  //한달로 설정 임시
//        Long accessExpireTimeMs = 1000 * 60 * 1L;   //test

        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+userName + "이 없습니다."));

        return JwtTokenUtil.createToken(selectedUser.getUserName(), selectedUser.getRole(),acessKey, accessExpireTimeMs);
    }

    public String getRefreshToken(String userName){
        Long refreshExpireTimeMs = 1000 *60 * 60 *24 *30L;
//        Long refreshExpireTimeMs = 1000 *60 * 2L;   //test

        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        return JwtTokenUtil.createToken(selectedUser.getUserName(), selectedUser.getRole() ,refreshKey, refreshExpireTimeMs);
    }

    public boolean validateRefreshToken(String refreshToken){
        return JwtTokenUtil.isValidate(refreshToken,refreshKey);
    }

    public String getUserNameByRefreshToken(String refreshToken){
        return JwtTokenUtil.getUserName(refreshToken,refreshKey);
    }

}
