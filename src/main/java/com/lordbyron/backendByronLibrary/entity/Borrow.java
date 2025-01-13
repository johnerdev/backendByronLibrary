package com.lordbyron.backendByronLibrary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "borrow")
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_borrow", nullable = false, updatable = false)
    private Long idBorrow;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "date_borrow", nullable = false)
    private LocalDate dateBorrow;

    @Column(name = "date_devolution", nullable = false)
    private LocalDate dateDevolution;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private StateBorrow state;

    @Column(length = 500)
    private String description;


}

