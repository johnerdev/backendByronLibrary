package com.lordbyron.backendByronLibrary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Role name is mandatory")
    @Column(nullable = false)
    private String name;

//    public Role() {
//    }
//
//    public Role(Long id, String name) {
//        this.id = id;
//        this.name = name;
//    }
}