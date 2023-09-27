package com.capibara.plaigroundbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "owners")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;
    private String nickname;
    private String email;
    private String password;
    private UUID refreshTokenUUID;

    private boolean isEnabled;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "verification_tokens_id")
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Application> appList;

    @ManyToMany(mappedBy = "users")
    private List<Application> used;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
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
        return isEnabled;
    }
}
