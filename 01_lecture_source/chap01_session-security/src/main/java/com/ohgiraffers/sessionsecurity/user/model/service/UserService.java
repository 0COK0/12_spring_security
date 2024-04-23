package com.ohgiraffers.sessionsecurity.user.model.service;

import com.ohgiraffers.sessionsecurity.user.model.dao.UserMapper;
import com.ohgiraffers.sessionsecurity.user.model.dto.LoginUserDTO;
import com.ohgiraffers.sessionsecurity.user.model.dto.SignupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    // 여기서는 비밀번호를 인코딩 시켜줘야함

    @Autowired
    // 주입을 받았구
    private PasswordEncoder passwordEncoder;

    public int regist(SignupDTO signupDTO) {

        // 가지고와서 패스워드를 인코딩 시켜줌(설정)
        signupDTO.setUserPass(passwordEncoder.encode(signupDTO.getUserPass()));

        int result = 0; // result를 0으로 초기화

        try {
            result = userMapper.regist(signupDTO); // regist에 담아줌
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public LoginUserDTO findByUsername(String username) {

        LoginUserDTO login = userMapper.findByUsername(username);

        if (!Objects.isNull(login)) {

            return login;
        } else {
            return null;
        }
    }
}
