package fr.fergalmechin.basicbooksapi.repository;

import fr.fergalmechin.basicbooksapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
