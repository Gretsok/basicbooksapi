package fr.fergalmechin.basicbooksapi.controller;

import fr.fergalmechin.basicbooksapi.entity.Author;
import fr.fergalmechin.basicbooksapi.exception.DuplicateResourceException;
import fr.fergalmechin.basicbooksapi.repository.AuthorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/authors", produces = "application/json")
@Slf4j
public class AuthorController {
    @Autowired
    private AuthorRepository authorRepository;

    @Operation(summary = "Get all authors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List returned")
    })
    @GetMapping
    public List<Author> getAll() {
        log.info("Getting all authors");
        return authorRepository.findAll();
    }

    @Operation(summary = "Get author by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Author found"),
        @ApiResponse(responseCode = "404", description = "Author not found", content = @Content(examples = @ExampleObject("")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Author> getById(
            @Parameter(description = "Id of the author to retrieve", example = "1")
            @PathVariable long id
    ) {
        log.info("Getting author with id {}", id);
        return authorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new author")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "New author created"),
        @ApiResponse(responseCode = "409", description = "Author with this name already exists.", content = @Content(examples = @ExampleObject("")))
    })
    @PostMapping
    public ResponseEntity<Author> create(@RequestBody Author author) {
        try {
            Author saved = authorRepository.save(author);
            log.info("Creating new author {}", author);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Author is already existing.");
        }
    }

    @Operation(summary = "Update an existing author")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Author updated"),
        @ApiResponse(responseCode = "409", description = "Another author already has this name", content = @Content(examples = @ExampleObject(""))),
        @ApiResponse(responseCode = "404", description = "Could not find author to update", content = @Content(examples = @ExampleObject("")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Author> update(
            @Parameter(description = "Id of the author to update", example = "1")
            @PathVariable long id,
            @RequestBody Author updatedAuthor
    ) {
        return authorRepository.findById(id)
            .map(author -> {
                author.setName(updatedAuthor.getName());
                author.setCountry(updatedAuthor.getCountry());
                try {
                    var saved = authorRepository.save(author);
                    log.info("Updating author {}", author);
                    return ResponseEntity.ok(saved);
                } catch (DataIntegrityViolationException e) {
                    throw new DuplicateResourceException("Author with this name already exists.");
                }
            })
            .orElseGet(() -> {
                log.warn("No author found with id {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Delete author of given id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Author has been deleted"),
        @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Id of author to delete")
            @PathVariable long id
    ) {
        if (!authorRepository.existsById(id)) {
            log.warn("No author found with id {}", id);
            return ResponseEntity.notFound().build();
        }
        authorRepository.deleteById(id);
        log.info("Deleting author with id {}", id);
        return ResponseEntity.noContent().build();
    }

}
