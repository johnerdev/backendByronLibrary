package com.lordbyron.backendByronLibrary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(length = 50)
    private String acquisition;

    @Column(length = 50)
    private String published;

    @Column(length = 50)
    private String genre;

    @Column(length = 50)
    private String language;

    @Column(unique = true, length = 50)
    private String identifier;

    @Column
    private Integer year;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String edition;

    @Column(unique = true, length = 13)
    private String isbn;

    @Column(unique = true, length = 8)
    private String issn;

    @Column(length = 500)
    private String description;

    @Lob
    private String content;

    @Column(length = 50)
    private String area;

    @Column
    private Integer pages;

    @Column(length = 50)
    private String level;

    @Column(length = 100)
    private String location;

    @Column(nullable = false,unique = true, length = 13)
    private String series;

    @Column(nullable = false)
    private Boolean available;
}
