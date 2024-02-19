package com.example.communiverse.security.services;

import com.example.communiverse.domain.User;
import com.example.communiverse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        User user = userRepository.findByNombreUsuario(nombreUsuario);

        return UserDetailsImpl.build(user);
    }

}