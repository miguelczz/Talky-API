package com.talky.backend.service;

import com.talky.backend.dto.UserSyncRequest;
import com.talky.backend.model.User;
import com.talky.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sincroniza el usuario en la base de datos a partir de la información recibida.
     * - Si el usuario no existe, lo crea.
     * - Si existe, actualiza los campos disponibles.
     */
    @Transactional
    public User syncUser(UserSyncRequest req) {
        Optional<User> existingUserOpt = userRepository.findByCognitoSub(req.getSub());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (req.getEmail() != null) existingUser.setEmail(req.getEmail());
            if (req.getName() != null) existingUser.setName(req.getName());
            if (req.getPhoneNumber() != null) existingUser.setPhoneNumber(req.getPhoneNumber());
            if (req.getBirthdate() != null) existingUser.setBirthdate(req.getBirthdate());
            if (req.getGender() != null) existingUser.setGender(req.getGender());
            return userRepository.save(existingUser);
        } else {
            User newUser = new User();
            newUser.setCognitoSub(req.getSub());
            newUser.setEmail(req.getEmail());
            newUser.setName(req.getName());
            newUser.setPhoneNumber(req.getPhoneNumber());
            newUser.setBirthdate(req.getBirthdate());
            newUser.setGender(req.getGender());
            newUser.setRole(req.getRole() != null ? req.getRole() : "student"); // valor por defecto
            return userRepository.save(newUser);
        }
    }

    public Optional<User> getByCognitoSub(String sub) {
        return userRepository.findByCognitoSub(sub);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
