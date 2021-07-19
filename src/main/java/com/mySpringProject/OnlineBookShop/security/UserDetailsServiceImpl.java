package com.mySpringProject.OnlineBookShop.security;

import com.mySpringProject.OnlineBookShop.user.db.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final AdminConfig adminConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (adminConfig.getUsername().equalsIgnoreCase(username)) {
            return adminConfig.adminUser();
        }
        return userEntityRepository.findByUsernameIgnoreCase(username)
                .map(UserEntityDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
