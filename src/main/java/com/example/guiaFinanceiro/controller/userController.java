package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.repository.UserRepository;
import com.example.guiaFinanceiro.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Users user = userService.login(email);

        return ResponseEntity.ok(user);
    }
}
