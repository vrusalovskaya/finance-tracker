package com.example.finance_tracker.services;

import com.example.finance_tracker.entities.UserEntity;
import com.example.finance_tracker.exceptions.ResourceNotFoundException;
import com.example.finance_tracker.mappers.UserMapper;
import com.example.finance_tracker.models.User;
import com.example.finance_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    //    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public User create(String userName, String email, String rawPassword) {
        UserEntity user = new UserEntity();
        user.setUserName(userName);
        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPassword(rawPassword);
        return userMapper.toModel(userRepository.save(user));
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).map(userMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));
    }
}
