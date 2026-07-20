package fr.fergalmechin.basicbooksapi.controller;

import fr.fergalmechin.basicbooksapi.entity.Author;
import fr.fergalmechin.basicbooksapi.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returnsListOfAuthors() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Fergal M.");
        author.setCountry("France");

        when(authorRepository.findAll()).thenReturn(List.of(author));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fergal M."));
    }

    @Test
    void getById_returnsAuthor() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Fergal M.");
        author.setCountry("France");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fergal M."));
    }

    @Test
    void getById_returnsNotFound() throws Exception {
        when(authorRepository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/authors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returnsCreatedAuthor() throws Exception {
        Author inputAuthor = new Author();
        inputAuthor.setName("Fergal M.");
        inputAuthor.setCountry("France");

        Author outputAuthor = new Author();
        outputAuthor.setId(5L);
        outputAuthor.setName("Fergal M.");
        outputAuthor.setCountry("France");

        when(authorRepository.save(any(Author.class))).thenReturn(outputAuthor);

        mockMvc.perform(post("/api/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputAuthor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    public void create_sqlDataConstraintViolated_returnsConflict() throws Exception {
        Author inputAuthor = new Author();
        inputAuthor.setName("Fergal M.");
        inputAuthor.setCountry("France");

        when(authorRepository.save(any(Author.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/api/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputAuthor)))
                .andExpect(status().isConflict());
    }

    @Test
    public void update_returnsUpdatedAuthor() throws Exception {
        Author inputAuthor = new Author();
        inputAuthor.setName("Fergal M.");
        inputAuthor.setCountry("France");

        Author outputAuthor = new Author();
        outputAuthor.setId(5L);
        outputAuthor.setName("Fergal M.");
        outputAuthor.setCountry("France");

        when(authorRepository.findById(any(Long.class))).thenReturn(Optional.of(outputAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(outputAuthor);

        mockMvc.perform(put("/api/authors/5")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fergal M."))
                .andExpect(jsonPath("$.country").value("France"))
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    public void update_sqlDataConstraintViolated_returnsConflict() throws Exception {
        Author inputAuthor = new Author();
        inputAuthor.setName("Fergal M.");
        inputAuthor.setCountry("France");

        Author outputAuthor = new Author();
        outputAuthor.setId(5L);
        outputAuthor.setName("Fergal M.");
        outputAuthor.setCountry("France");

        when(authorRepository.findById(any(Long.class))).thenReturn(Optional.of(outputAuthor));
        when(authorRepository.save(any(Author.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(put("/api/authors/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputAuthor)))
                .andExpect(status().isConflict());
    }

    @Test
    public void update_noAuthor_returnsUpdatedAuthor() throws Exception {
        Author inputAuthor = new Author();
        inputAuthor.setName("Fergal M.");
        inputAuthor.setCountry("France");
        when(authorRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/authors/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputAuthor)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_returnsNoContent() throws Exception {
        when(authorRepository.existsById(any(Long.class))).thenReturn(true);

        mockMvc.perform(delete("/api/authors/5"))
                .andExpect(status().isNoContent());
    }


    @Test
    public void delete_noAuthor_returnsNotFound() throws Exception {
        when(authorRepository.existsById(any(Long.class))).thenReturn(false);

        mockMvc.perform(delete("/api/authors/5"))
                .andExpect(status().isNotFound());
    }
}
