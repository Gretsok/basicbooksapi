package fr.fergalmechin.basicbooksapi.controller;

import fr.fergalmechin.basicbooksapi.dto.request.BookRequest;
import fr.fergalmechin.basicbooksapi.entity.Author;
import fr.fergalmechin.basicbooksapi.entity.Book;
import fr.fergalmechin.basicbooksapi.repository.AuthorRepository;
import fr.fergalmechin.basicbooksapi.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private AuthorRepository authorRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findAll_returnsAllBooks() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Fergal M.");
        author.setCountry("France");

        Book book = new Book();
        book.setId(1L);
        book.setYear(2000);
        book.setTitle("Le livre");
        book.setAuthor(author);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findAll()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].title").value("Le livre"))
                .andExpect(jsonPath("[0].authorName").value("Fergal M."));
    }

    @Test
    public void findById_bookFound_returnsBook() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Fergal M.");
        author.setCountry("France");

        Book book = new Book();
        book.setId(1L);
        book.setYear(2000);
        book.setTitle("Le livre");
        book.setAuthor(author);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1" ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Le livre"))
                .andExpect(jsonPath("$.authorName").value("Fergal M."));
    }

    @Test
    public void findById_noBook_returnsNotFound() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/1" ))
                .andExpect(status().isNotFound());
    }

    @Test
    public void create_returnsCreatedBook() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Fergal M.");
        author.setCountry("France");

        BookRequest inputBook = new BookRequest("Le livre", 2000, author.getId());

        Book outputBook = new Book();
        outputBook.setId(1L);
        outputBook.setYear(2000);
        outputBook.setTitle("Le livre");
        outputBook.setAuthor(author);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(outputBook);

        mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Le livre"))
                .andExpect(jsonPath("$.authorName").value("Fergal M."));
    }

    @Test
    public void create_noAuthor_returnsNotFound() throws Exception {
        BookRequest inputBook = new BookRequest("Le livre", 2000, 1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_returnsUpdatedBook() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Fergal M.");
        author.setCountry("France");

        BookRequest inputBook = new BookRequest("Le roman", 2008, author.getId());

        Book outputBook = new Book();
        outputBook.setId(1L);
        outputBook.setYear(2008);
        outputBook.setTitle("Le roman");
        outputBook.setAuthor(author);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(outputBook));
        when(bookRepository.save(any(Book.class))).thenReturn(outputBook);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Le roman"))
                .andExpect(jsonPath("$.authorName").value("Fergal M."));
    }

    @Test
    public void update_noAuthor_returnsNotFound() throws Exception {
        Book outputBook = new Book();
        outputBook.setId(1L);
        outputBook.setYear(2000);
        outputBook.setTitle("Le livre");

        BookRequest inputBook = new BookRequest("Le livre", 2000, 1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(outputBook));

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_authorButNoBook_returnsNotFound() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Fergal M.");
        author.setCountry("France");

        BookRequest inputBook = new BookRequest("Le livre", 2000, 1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_bookFound_returnsNoContent() throws Exception {
        when(bookRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/books/1" ))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_bookNotFound_returnsNotFound() throws Exception {
        when(bookRepository.existsById(1L)).thenReturn(false);
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNotFound());
    }
}
