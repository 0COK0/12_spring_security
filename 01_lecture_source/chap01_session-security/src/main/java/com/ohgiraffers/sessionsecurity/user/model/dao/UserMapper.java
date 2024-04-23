package com.ohgiraffers.sessionsecurity.user.model.dao;


import com.ohgiraffers.sessionsecurity.user.model.dto.LoginUserDTO;
import com.ohgiraffers.sessionsecurity.user.model.dto.SignupDTO;
import org.apache.ibatis.annotations.Mapper;

//맴퍼를 달아줘야 마이바티스에서 달아놓은걸 찾아줌
@Mapper
public interface UserMapper {

    int regist(SignupDTO signupDTO);

    LoginUserDTO findByUsername(String username);
}
