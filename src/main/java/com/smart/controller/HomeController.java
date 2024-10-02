package com.smart.controller;

import com.smart.helper.Message;
import com.smart.model.User;
import com.smart.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
//@ResponseBody
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Home");
        return "home";
    }
    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title", "About");
        return "about";
    }
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", "Register");
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/login")
    public String login(){
        return "/login";
    }

    @PostMapping(path = "/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement){
        try{
            if(!agreement){
                System.out.println("You are not clicking the terms and conditions!!");
                throw new Exception("You are not clicking the terms and conditions!!");
            }

            if(result.hasErrors()){
                System.out.println(result.toString());
                model.addAttribute("user", user);
                return"signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered !! ","alert-success"));
            return "redirect:signup";

        }
        catch (Exception e){
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(),"alert-danger"));
            return "signup";
        }
    }
}
