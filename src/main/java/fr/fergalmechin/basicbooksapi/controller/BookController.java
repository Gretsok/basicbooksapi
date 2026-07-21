package fr.fergalmechin.basicbooksapi.controller;

import fr.fergalmechin.basicbooksapi.dto.request.BookRequest;
import fr.fergalmechin.basicbooksapi.dto.response.BookResponse;
import fr.fergalmechin.basicbooksapi.entity.Author;
import fr.fergalmechin.basicbooksapi.entity.Book;
import fr.fergalmechin.basicbooksapi.exception.ResourceNotFoundException;
import fr.fergalmechin.basicbooksapi.repository.AuthorRepository;
import fr.fergalmechin.basicbooksapi.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.lang.module.ResolutionException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api/books", produces = "application/json")
@Slf4j
public class BookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Operation(summary = "Get all books")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List returned")
    })
    @GetMapping
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(BookResponse::fromEntity)
                .toList();
    }

    @Operation(summary = "Get a book by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(examples = @ExampleObject("")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(
            @Parameter(description = "Id of book to find")
            @PathVariable long id
    ) {
        return bookRepository.findById(id)
            .map(book -> {
                log.info("Get Book with id {}", book.getId());
                return ResponseEntity.ok(BookResponse.fromEntity(book));
            })
            .orElseGet(() -> {
                log.warn("Book with id {} not found", id);
                return ResponseEntity.notFound().build();
            });
    }

    @Operation(summary = "Create a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created"),
        @ApiResponse(responseCode = "404", description = "Author not found", content = @Content(examples = @ExampleObject("")))
    })
    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody BookRequest bookRequest) {
        Author author = authorRepository.findById(bookRequest.authorId()).orElse(null);
        if (author == null) {
            log.warn("Author with id {} not found", bookRequest.authorId());
            throw new ResourceNotFoundException( "Author not found.");
        }

        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(bookRequest.title());
        book.setYear(bookRequest.year());

        var bookSaved = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.fromEntity(bookSaved));
    }

    @Operation(summary = "Update a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated"),
        @ApiResponse(responseCode = "404", description = "Book or author not found", content = @Content(examples = @ExampleObject("")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @Parameter(description = "Id of book to update")
            @PathVariable long id,
            @RequestBody BookRequest bookRequest) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            log.warn("Book with id {} not found", id);
            throw new ResourceNotFoundException( "Book not found.");
        }

        Author author = authorRepository.findById(bookRequest.authorId()).orElse(null);
        if (author == null) {
            log.warn("Author with id {} not found", bookRequest.authorId());
            throw new ResourceNotFoundException( "Author not found.");
        }

        book.setAuthor(author);
        book.setTitle(bookRequest.title());
        book.setYear(bookRequest.year());

        var bookSaved = bookRepository.save(book);
        log.info("Book with id {} updated", id);
        return ResponseEntity.ok(BookResponse.fromEntity(bookSaved));
    }

    @Operation(summary = "Delete a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "The book has been deleted"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Id of book to delete")
            @PathVariable long id) {
        if (!bookRepository.existsById(id)) {
            log.warn("Book with id {} not found", id);
            throw new ResourceNotFoundException( "Book not found.");
        }

        bookRepository.deleteById(id);
        log.info("Book with id {} deleted", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
