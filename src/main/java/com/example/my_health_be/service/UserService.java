package com.example.my_health_be.service;

import com.example.my_health_be.domain.user.User;
import com.example.my_health_be.dto.user.UserJoinRequest;
import com.example.my_health_be.exception.AppException;
import com.example.my_health_be.exception.ErrorCode;
import com.example.my_health_be.repository.UserRepository;
import com.example.my_health_be.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import static com.example.my_health_be.domain.enums.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String acessKey; //secret key

    @Value("${jwt.token.refresh}")
    private String refreshKey;

    public String join(UserJoinRequest dto){

        String userName = dto.getUserName();
        String password = dto.getPassword();
        String nickName = dto.getNickName();
        String email = dto.getEmail();

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

        // email 중복 check
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                            throw new AppException(ErrorCode.EMAIL_DUPLICATED, email + "는 이미 존재합니다.");
                        }
                );

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .nickName(nickName)
                .email(email)
                .role(ROLE_USER)
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(String userName, String password){

        //userName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        //password 틀림
        if(!encoder.matches(password, selectedUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }

        //access token
        String accessToken = getAccessToken(selectedUser.getUserName());


        // Exception 없으면 토큰 발행함
        return accessToken;
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

    public String getAccessToken(String userName){
//        Long accessExpireTimeMs = 1000 * 60 * 60L;
        Long accessExpireTimeMs = 1000 *60 * 60 *24 *30L;  //한달로 설정 시연 위해서
//        Long accessExpireTimeMs = 1000 * 60 * 1L;   //test

        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, "사용자"+userName + "이 없습니다."));

        String accessToken = JwtTokenUtil.createToken(selectedUser.getUserName(), selectedUser.getRole(),acessKey, accessExpireTimeMs);

        return accessToken;
    }

    public String getRefreshToken(String userName){
        Long refreshExpireTimeMs = 1000 *60 * 60 *24 *30L;
//        Long refreshExpireTimeMs = 1000 *60 * 2L;   //test

        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        String refreshToken = JwtTokenUtil.createToken(selectedUser.getUserName(), selectedUser.getRole() ,refreshKey, refreshExpireTimeMs);

        return refreshToken;
    }

    public boolean validateRefreshToken(String refreshToken){
        return JwtTokenUtil.isValidate(refreshToken,refreshKey);
    }

    public String getUserNameByRefreshToken(String refreshToken){
        return JwtTokenUtil.getUserName(refreshToken,refreshKey);
    }

}
