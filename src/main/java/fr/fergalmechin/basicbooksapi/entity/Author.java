package fr.fergalmechin.basicbooksapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Author {

    @Schema(description = "Id of author", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Schema(description = "Name of author", example = "Jules Verne")
    private String name;
    @Schema(description = "Country of author", example = "France")
    private String country;
}
