package com.web.billim.controller;

import com.web.billim.dto.request.MemberSignupRequest;
import com.web.billim.service.MemberService;
import com.web.billim.validation.CheckIdValidator;
import com.web.billim.validation.CheckNickNameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private final MemberService memberService;

    private final CheckIdValidator checkIdValidator;
    private final CheckNickNameValidator checkNickNameValidator;

    @InitBinder
    public void validatorBinder(WebDataBinder binder){
        binder.addValidators(checkIdValidator);
        binder.addValidators(checkNickNameValidator);
    }

    @GetMapping("/member/signup")
    public String memberSignUp(){
        return "pages/member/signup";
    }

    @PostMapping("/member/signup")
    public String memberSingUpProc(@Valid MemberSignupRequest memberSignupRequest,
                               BindingResult bindingResult,
                               Model model
    ){
        if(bindingResult.hasErrors()){
            model.addAttribute("memberDto",memberSignupRequest);

            Map<String,String> validatorResult = memberService.validateHandling(bindingResult);
            for(String key : validatorResult.keySet()){
                model.addAttribute(key,validatorResult.get(key));
            }
            return "pages/member/signup";
        }
        memberService.singUp(memberSignupRequest);

        return "pages/home";}

}
