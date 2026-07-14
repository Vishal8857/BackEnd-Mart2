package com.product.CommanClasses;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.
SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.product.Entity.User;
import com.product.Repository.UserRepo;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String mail)
            throws UsernameNotFoundException {

        User user = repo.findByMail(mail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"));

        return new org.springframework.security.core.userdetails.User(

                user.getMail(),

                user.getPassword(),

                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole()
                        )
                )
        );
    }
}