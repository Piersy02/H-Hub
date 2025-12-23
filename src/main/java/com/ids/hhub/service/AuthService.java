package com.ids.hhub.service;

import com.ids.hhub.dto.LoginRequestDto;
import com.ids.hhub.dto.RegisterRequestDto;
import com.ids.hhub.model.User;
import com.ids.hhub.model.enums.PlatformRole;
import com.ids.hhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    // Registrazione
    public User register(RegisterRequestDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email già registrata!");
        }

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setSurname(dto.getSurname());
        newUser.setEmail(dto.getEmail());

        // Cifriamo la password prima di salvarla
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Di default diventa un utente registrato
        newUser.setPlatformRole(PlatformRole.USER);

        return userRepo.save(newUser);
    }

    // Login
    public User login(LoginRequestDto dto) {
        // Cerca l'utente
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Confronta la password inviata con quella cifrata nel DB
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password errata");
        }

        return user;
    }

}