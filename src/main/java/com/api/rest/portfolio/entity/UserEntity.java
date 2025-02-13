package com.api.rest.portfolio.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String username;
    private String password;    
    private String token;

    @Column(name = "token_expired_at")
    private Long tokenExpiredAt;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(updatable = true, name = "updated_at")
    private Date updatedAt;

    @OneToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private ProfileEntity profileEntity;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<SkillEntity> skills;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<ProjectEntity> projects;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<ServiceEntity> services;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<SocialAccountEntity> socialAccounts;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<RoleEntity> roles;
}
