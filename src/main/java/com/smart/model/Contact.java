package com.smart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long cid;
    private String name;
    private String secondName;
    private String work;
    @Column(unique = true)
    private String email;
    private String phone;
    private String image;
    @Column(length = 500)
    private String description;

    @ManyToOne
    private User user;

}
