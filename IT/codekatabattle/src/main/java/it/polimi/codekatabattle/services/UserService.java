package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long userId);

    User saveUser(User user);

    User updateUser(User user);

    Optional<User> deleteUser(Long userId);

}
