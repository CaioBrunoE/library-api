package com.caiobruno.library_api.repository;

import com.caiobruno.library_api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository  extends JpaRepository<Book ,Long > {
    boolean existsByIsbn(String isbn);
}
