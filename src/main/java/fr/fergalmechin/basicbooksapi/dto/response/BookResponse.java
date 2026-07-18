package fr.fergalmechin.basicbooksapi.dto.response;

import fr.fergalmechin.basicbooksapi.entity.Book;

public record BookResponse(Long id, String title, Integer year, Long authorId, String authorName) {
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
