package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Account implements UserDetails {

    public Account(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.joined = LocalDateTime.now();
        this.role = Role.USER;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "author")
    private List<Resource> resources = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reporter")
    private List<Report> reports = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime joined;

    private int totalDownloads;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(mappedBy = "members")
    private List<MessageGroup> groups;

    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "author")
    private List<Message> messages = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role.toString()));
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

    public enum Role {
        USER,
        ADMIN;

        @Override
        public String toString() {
            return "ROLE_".concat(super.toString());
        }
    }
}