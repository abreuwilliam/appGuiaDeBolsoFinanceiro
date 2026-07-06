package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.UsersDto;
import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.repository.UserRepository;
import com.example.guiaFinanceiro.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<Users> criar(@RequestBody UsersDto users){
            Users user = userService.createUser(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Users user = userService.login(email);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/deleter")
    public void delete(@RequestBody String email){
        userService.delete(email);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable UUID id) {

        Users user = userService.findById(id);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/users")
    public ResponseEntity <List<Users>> getUserFindall(){
        List<Users> users = userService.findAll();
        return  ResponseEntity.ok(users);
    }
}
