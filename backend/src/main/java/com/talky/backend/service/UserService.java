package com.talky.backend.service;

import com.talky.backend.dto.UserSyncRequest;
import com.talky.backend.model.User;
import com.talky.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // buscar un usuario por su cognito_sub
    public Optional<User> getByCognitoSub(String cognitoSub) {
        return userRepository.findByCognitoSub(cognitoSub);
    }

    // buscar un usuario por su email
    public Optional<User> getByEmail(String email) { // <-- nuevo
        return userRepository.getByEmail(email);
    }

    // guardar un usuario nuevo o actualizado
    public User save(User user) {
        return userRepository.save(user);
    }

    // Metodo para reflejar el usuario en la base de datos
    @Transactional
    public User syncUser(String cognitoSub, UserSyncRequest req) {
        return userRepository.findByCognitoSub(cognitoSub)
                .map(existing -> {
                    existing.setEmail(req.getEmail());
                    existing.setName(req.getName());
                    existing.setRole(req.getRole());
                    existing.setPhoneNumber(req.getPhoneNumber());
                    existing.setBirthdate(req.getBirthdate());
                    existing.setGender(req.getGender());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setCognitoSub(cognitoSub);
                    u.setEmail(req.getEmail());
                    u.setName(req.getName());
                    u.setRole(req.getRole());
                    u.setPhoneNumber(req.getPhoneNumber());
                    u.setBirthdate(req.getBirthdate());
                    u.setGender(req.getGender());
                    return userRepository.save(u);
                });
    }
}
