package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.UsersDto;
import com.example.guiaFinanceiro.entites.Users;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void createUser (UsersDto usersDto){
        Users users = new Users();
        users.setEmail(usersDto.getEmail());
        users.setName(users.getName());
    }
}
