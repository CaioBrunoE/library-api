package com.caiobruno.library_api.service.imp;

import com.caiobruno.library_api.exceptions.BusinessException;
import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.repository.BookRepository;
import com.caiobruno.library_api.service.BookService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImp implements BookService {
    @Autowired
    private BookRepository repository;

    @Override
    public Book save(@NotNull Book entity) {
        if (repository.existsByIsbn(entity.getIsbn())){
            throw new BusinessException("Isbn ja cadastrado");
        }
        return repository.save(entity);
    }

    public Optional<Book> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Book id can't be null.");
        }
        return repository.findById(id);
    }


    @Override
    public void delete(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("Book id cant be null.");
        }
        this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("Book id cant be null.");
        }
      return this.repository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {

        Example<Book> example= Example.of(filter,
                ExampleMatcher
                    .matching()
                    .withIgnoreCase()
                    .withIgnoreNullValues()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        return repository.findAll(example, pageRequest);
    }
}
