package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.dto.UsersDto;
import com.example.guiaFinanceiro.entites.Users;
import com.example.guiaFinanceiro.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Users createUser(UsersDto usersDto) {
        Users users = new Users();
        users.setEmail(usersDto.getEmail());
        users.setName(usersDto.getName());
        users.setSenha(usersDto.getSenha());
        userRepository.save(users);
        return users;
    }

    @Modifying
    @Transactional
    public void delete(String email) {
        try {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            userRepository.delete(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Users login(String email) {
        // Busca o usuário. Se não achar, lança uma exceção.
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail: " + email));
    }

    @Transactional
    public Users findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional()
    public List<Users> findAll() {
        return userRepository.findAll();
    }
}