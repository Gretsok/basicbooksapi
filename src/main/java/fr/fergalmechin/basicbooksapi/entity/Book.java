package fr.fergalmechin.basicbooksapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter@Getter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
