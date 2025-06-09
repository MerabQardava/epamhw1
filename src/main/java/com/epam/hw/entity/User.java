package com.epam.hw.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer userId;

    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected boolean isActive = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Trainee trainee;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Trainer trainer;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = generateUsername();
        this.password = generatePassword();
    }

    protected String generateUsername() {
        return firstName + "." + lastName;
    }

    protected String generatePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

}
