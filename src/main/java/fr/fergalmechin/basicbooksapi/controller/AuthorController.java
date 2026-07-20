package fr.fergalmechin.basicbooksapi.controller;

import fr.fergalmechin.basicbooksapi.entity.Author;
import fr.fergalmechin.basicbooksapi.exception.DuplicateResourceException;
import fr.fergalmechin.basicbooksapi.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Slf4j
public class AuthorController {
    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping
    public List<Author> getAll() {
        log.info("Getting all authors");
        return authorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getById(@PathVariable long id) {
        log.info("Getting author with id {0}", id);
        return authorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Author author) {
        try {
            Author saved = authorRepository.save(author);
            log.info("Creating new author {0}", author);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Author is already existing.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody Author updatedAuthor) {
        return authorRepository.findById(id)
            .map(author -> {
                author.setName(updatedAuthor.getName());
                author.setCountry(updatedAuthor.getCountry());
                try {
                    var saved = authorRepository.save(author);
                    log.info("Updating author {0}", author);
                    return ResponseEntity.ok(saved);
                } catch (DataIntegrityViolationException e) {
                    throw new DuplicateResourceException("Author with this name already exists.");
                }
            })
            .orElseGet(() -> {
                log.warn("No author found with id {0}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (!authorRepository.existsById(id)) {
            log.warn("No author found with id {0}", id);
            return ResponseEntity.notFound().build();
        }
        authorRepository.deleteById(id);
        log.info("Deleting author with id {0}", id);
        return ResponseEntity.noContent().build();
    }

}
