package com.pavel.store.service;

import com.pavel.store.entity.User;
import com.pavel.store.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;


    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllUsers(pageable);
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

    }

    public List<User> getUserByUsername(String username) {
        return userRepository.findAllByUsername(username);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

}
