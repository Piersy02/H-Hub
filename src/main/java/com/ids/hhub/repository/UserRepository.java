package com.ids.hhub.repository;

import com.ids.hhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;



public interface UserRepository extends JpaRepository<User, Long> {

    // Trova un utente completo tramite email
    Optional<User> findByEmail(String email);

    // Controlla se esiste l'email
    boolean existsByEmail(String email);
}