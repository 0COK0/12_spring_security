package com.ohgiraffers.sessionsecurity.user.Controller;


import com.ohgiraffers.sessionsecurity.user.model.dto.SignupDTO;
import com.ohgiraffers.sessionsecurity.user.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public void signup() {}

    @PostMapping("/signup")
    public ModelAndView signup(ModelAndView mv, @ModelAttribute SignupDTO signupDTO) {

        // 유효성이 적합한지 확인해야함 여기는 그냥 간당하게 넘김
        int result = userService.regist(signupDTO);

        String message = "";

        //
        if(result > 0) {
            message = "회원가입이 정상적으로 완료되었습니다.";
        } else {
            message = "회원가입이 실패하였습니다.";
        }

        mv.addObject("message", message);

        return mv;
    }


}
