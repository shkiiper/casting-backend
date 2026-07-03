package com.casting.platform.security;

import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import com.casting.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrderByIdAsc(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return toPrincipal(user);
    }

    public UserDetails loadUserByEmailAndRole(String email, UserRole role)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email + " / " + role));

        return toPrincipal(user);
    }

    private UserDetails toPrincipal(User user) {
        if (!user.isActive()) {
            throw new DisabledException("User is inactive");
        }
        if (user.isBanned()) {
            throw new DisabledException("User is banned");
        }

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.isActive(),
                user.isBanned()
        );
    }
}
