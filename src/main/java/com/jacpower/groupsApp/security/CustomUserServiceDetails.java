package com.jacpower.groupsApp.security;

import com.jacpower.groupsApp.dao.RoleDao;
import com.jacpower.groupsApp.dao.UserDao;
import com.jacpower.groupsApp.model.AuthUser;
import com.jacpower.groupsApp.model.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserServiceDetails implements UserDetailsService {
    private UserDao userDao;
    private RoleDao roleDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AuthUser> user=userDao.getUser(username);
        Set<Role> roles=roleDao.getRoles(username);

        AuthUser authUser = user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<GrantedAuthority> authorities=roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        return new User(username, authUser.getPassword(), authorities);
    }
}
