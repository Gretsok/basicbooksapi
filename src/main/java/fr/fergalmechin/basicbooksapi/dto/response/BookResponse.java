package fr.fergalmechin.basicbooksapi.dto.response;

import fr.fergalmechin.basicbooksapi.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;

public record BookResponse(
        @Schema(description = "Id of book", example = "1")
        Long id,
        @Schema(description = "Title of book", example = "Vingt Mille Lieues sous les mers")
        String title,
        @Schema(description = "Year of publication", example = "1869")
        Integer year,
        @Schema(description = "Author id", example = "1")
        Long authorId,
        @Schema(description = "Author name", example = "Jules Verne")
        String authorName) {
    public static BookResponse fromEntity(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getYear(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }
}
