package com.leave.config;

import com.leave.entity.SysUser;
import com.leave.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.findByUsername("admin").ifPresent(user -> {
            if (!passwordEncoder.matches("123456", user.getPassword())) {
                user.setPassword(passwordEncoder.encode("123456"));
                userRepository.save(user);
                log.info("管理员密码已重置为: 123456");
            }
        });
    }
}
