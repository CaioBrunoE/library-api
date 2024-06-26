package com.caiobruno.library_api.service;

import com.caiobruno.library_api.exceptions.BusinessException;
import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    @Autowired
    private BookRepository repository;
    public Book save(Book entity) {
        if (repository.existsByIsbn(entity.getIsbn())){
            throw new BusinessException("Isbn ja cadastrado");
        }
        return repository.save(entity);
    }
}
