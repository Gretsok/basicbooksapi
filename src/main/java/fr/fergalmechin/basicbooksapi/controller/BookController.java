package fr.fergalmechin.basicbooksapi.controller;

import fr.fergalmechin.basicbooksapi.dto.request.BookRequest;
import fr.fergalmechin.basicbooksapi.dto.response.BookResponse;
import fr.fergalmechin.basicbooksapi.entity.Author;
import fr.fergalmechin.basicbooksapi.entity.Book;
import fr.fergalmechin.basicbooksapi.repository.AuthorRepository;
import fr.fergalmechin.basicbooksapi.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(BookResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable long id) {
        return bookRepository.findById(id)
            .map(book -> {
                log.info("Get Book with id {0}", book.getId());
                return ResponseEntity.ok(BookResponse.fromEntity(book));
            })
            .orElseGet(() -> {
                log.warn("Book with id {0} not found", id);
                return ResponseEntity.notFound().build();
            });
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookRequest bookRequest) {
        Author author = authorRepository.findById(bookRequest.authorId()).orElse(null);
        if (author == null) {
            log.warn("Author with id {0} not found", bookRequest.authorId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Author not found.");
        }

        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(bookRequest.title());
        book.setYear(bookRequest.year());

        var bookSaved = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.fromEntity(bookSaved));
    }
}
