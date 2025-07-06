package com.linktune.api.Service;

import com.linktune.api.Repository.UserRepository;
import com.linktune.api.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
// 1. UserDetailsService'i implemente et. Bu, sınıfımızı Spring Security için resmi kullanıcı bulma servisi yapar.
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 2. Döngüye neden olan AuthenticationManager ve JwtService'i buradan siliyoruz!
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String plainTextPassword) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(plainTextPassword));
        return userRepository.save(newUser);
    }

    // 3. UserDetailsService arayüzünün zorunlu kıldığı metot.
    // Spring Security, bir kullanıcıyı doğrulamak istediğinde BU METODU çağırır.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Spring Security'nin anladığı UserDetails nesnesine çeviriyoruz.
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}