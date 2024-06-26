package com.caiobruno.library_api.service;

import com.caiobruno.library_api.exceptions.BusinessException;
import com.caiobruno.library_api.model.dto.BookDTO;
import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
public class BookServiceTest {

    @Autowired
    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){}

    private Book createValidBook(){
        return Book.builder().author("Jorge").title("As aventuras").isbn("123").build();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(
                Book.builder().id(1L)
                        .author("Jorge")
                        .title("As aventuras")
                        .isbn("123")
                        .build());

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getAuthor()).isEqualTo("Jorge");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");

    }


    @Test
    @DisplayName("Deve lançar um erro ao tentar salvar um livro com isbn ja duplicado  ")
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenario
        Book book =createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //ececução
        Throwable exception= Assertions.catchThrowable(()-> service.save(book));

        //Verificações
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn ja cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

}
