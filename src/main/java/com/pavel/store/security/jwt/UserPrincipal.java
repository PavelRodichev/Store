package com.pavel.store.security.jwt;

import com.pavel.store.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class UserPrincipal implements UserDetails {

    @Getter
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, @Email @NotEmpty String email,
                         @Size(min = 6, message = "Password must be at least 6 characters") @NotEmpty String password,
                         Collection<? extends GrantedAuthority> authority) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authority;
    }


    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authority = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()));

        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), authority);
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
        return email;
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
        return true;
    }
}
