package com.project.note.security;

import com.project.note.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MyUserDetails implements UserDetails {
    private String username;
    private String password;
    private int active;
    private boolean is2FaEnabled;
    private List<GrantedAuthority> authorities;

    public MyUserDetails(User user, List<GrantedAuthority> authorities) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.active = user.getActive();
        this.is2FaEnabled = user.getTotpSecret() != null;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active == 1;
    }

    public boolean is2FaEnabled() {
        return is2FaEnabled;
    }
}
