package org.example.authenticationdemo.controllers;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.message.Message;
import org.example.authenticationdemo.models.User;
import org.example.authenticationdemo.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

@Controller
public class HomeController {

    private final UserService userService;
    private final SecureRandom secureRandom;

    public HomeController(UserService userService) {
        this.userService = userService;
        this.secureRandom = new SecureRandom();
    }

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        byte[] salt = new byte[64];
        secureRandom.nextBytes(salt);
        byte[] passwordBytes = password.getBytes();
        byte[] saltPassword = new byte[salt.length + passwordBytes.length];
        System.arraycopy(passwordBytes, 0, saltPassword, 0, passwordBytes.length);
        System.arraycopy(salt, 0, saltPassword, passwordBytes.length, salt.length);

        Base64.Encoder encoder = Base64.getEncoder();

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            model.addAttribute("message", "Failure");
            return "register";
        }

        byte[] hashedAndSaltedBytes = md.digest(saltPassword);
        String encodedPassword = encoder.encodeToString(hashedAndSaltedBytes);
        String encodedSalt = encoder.encodeToString(salt);

        System.out.println("Email: " + email);
        System.out.println("encodedPassword: " + encodedPassword);
        System.out.println("encodedSalt: " + encodedSalt);

        boolean success = userService.registerUser(new User(email, encodedPassword, encodedSalt));
        String message = "Status: ";
        message += success ? "Success!" : "Failure";
        model.addAttribute("message", message);
        model.addAttribute("message", "success");
        return "register";
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/login")
    public ModelAndView login(
            @RequestHeader("Authorization") String authString
    ) {
        String encoded = authString.split(" ")[1];
        String decoded = new String(Base64.getDecoder().decode(encoded.getBytes()));
        String email = decoded.split(":")[0];
        String password = decoded.split(":")[1];
        boolean loginSuccess = userService.loginUser(email, password);
//        boolean loginSuccess = true;

        if (loginSuccess) {
            return new ModelAndView("profile", HttpStatus.OK);
        } else {
            return new ModelAndView("unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }
}
