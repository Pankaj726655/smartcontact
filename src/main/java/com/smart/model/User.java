package com.smart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"USER\"")
@RequiredArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Name should not be null !!")
    @Size(min = 3, max = 20, message = "Name length only in between 2 to 20 !!")
    private String name;
    @Column(unique = true)
    @NotBlank(message = "Email field is required !!")
    private String email;
    @Size(min = 8, message = "Password length should be greater than 8 ")
    private  String password;
    private String role;
    private boolean enabled;
    private String imageUrl;
    @Column(length = 500)
    private String about;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Contact> contacts = new ArrayList<>();
}
