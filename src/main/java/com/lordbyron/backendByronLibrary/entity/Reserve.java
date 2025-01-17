package com.lordbyron.backendByronLibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Reserve")
public class Reserve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserve;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "id_libro", nullable = false)
    private Book book;

    @Temporal(TemporalType.DATE)
    private LocalDate dateReserve;

    @Enumerated(EnumType.STRING)
    private StateReserve state;

    // Getters y Setters
    // Constructor vacío y parametrizado
}