package com.dailyhelp.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(name="uk_users_email", columnNames="email"))
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String name;
 @Column(nullable=false,length=190) private String email;
 @Column(name="password_hash",nullable=false,length=100) private String passwordHash;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected AppUser() {}
 public AppUser(String name,String email,String passwordHash){this.name=name;this.email=email;this.passwordHash=passwordHash;}
 @PrePersist void prePersist(){createdAt=Instant.now();}
 public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public Instant getCreatedAt(){return createdAt;}
}