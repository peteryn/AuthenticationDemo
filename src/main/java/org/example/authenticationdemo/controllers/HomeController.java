package org.example.authenticationdemo.controllers;

import org.example.authenticationdemo.models.User;
import org.example.authenticationdemo.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @PostMapping("/")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        boolean success = userService.registerUser(new User(email, password));
        String message = "Status: ";
        message += success ? "Success!" : "Failure";
        model.addAttribute("message", message);
        return "home";
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/login")
    public ModelAndView login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        boolean loginSuccess = userService.loginUser(new User(email, password));
        if (loginSuccess) {
            return new ModelAndView("profile", HttpStatus.OK);
        } else {
            return new ModelAndView("unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }
}
