package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.models.User;
import it.polimi.codekatabattle.repositories.UserRepository;
import it.polimi.codekatabattle.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return this.userRepository.findById(userId);
    }

    @Override
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public Optional<User> deleteUser(Long userId) {
        return this.userRepository.findById(userId).map(user -> {
            this.userRepository.delete(user);
            return user;
        });
    }

}
