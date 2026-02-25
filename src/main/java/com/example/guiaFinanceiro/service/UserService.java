package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.UsersDto;
import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createUser (UsersDto usersDto){
        Users users = new Users();
        users.setEmail(usersDto.getEmail());
        users.setName(users.getName());
    }
    public Users login(String email) {
        // Busca o usuário. Se não achar, lança uma exceção.
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail: " + email));
    }
}
