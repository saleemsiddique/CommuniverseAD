package com.example.communiverse.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String nombre;

    private String apellidos;

    private String username;

    @JsonIgnore
    private String contrasenya;

    private ECliente estado;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String nombre, String apellidos, String username, String contrasenya, ECliente estado, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.username = username;
        this.contrasenya = contrasenya;
        this.estado = estado;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Cliente cliente) {
        List<GrantedAuthority> authorities = cliente.getRols().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombre().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getUsername(),
                cliente.getContrasenya(),
                cliente.getEstado(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    @Override
    public String getPassword() {
        return contrasenya;
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
