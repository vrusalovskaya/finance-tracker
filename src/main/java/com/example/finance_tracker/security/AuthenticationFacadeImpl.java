package com.example.finance_tracker.security;

import com.example.finance_tracker.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public String login(String email, String password) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, password)
                );

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof SecurityUser user)) {
            throw new IllegalStateException("Authenticated principal is not SecurityUser");
        }

        return jwtService.generateToken(user);

    }

    @Override
    @Transactional
    public String register(String userName, String email, String rawPassword) {
        var user = userService.create(userName, email, rawPassword);

        return login(user.getEmail(), rawPassword);
    }
}
