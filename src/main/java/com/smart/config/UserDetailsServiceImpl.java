package com.smart.config;

import com.smart.model.User;
import com.smart.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //fetching user from database
        User user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("Could not find user !!");
        }
        return new CustomUserDetails(user);
    }
}
