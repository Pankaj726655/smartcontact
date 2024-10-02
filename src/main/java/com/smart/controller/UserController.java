package com.smart.controller;

import com.smart.model.User;
import com.smart.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "normal/user_dashboard";
    }


}
