package com.caiobruno.library_api.service;

import com.caiobruno.library_api.exceptions.BusinessException;
import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.repository.BookRepository;
import com.caiobruno.library_api.service.imp.BookServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
public class BookServiceTest {

    @Autowired
    BookServiceImp service;

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
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(
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
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //ececução
        Throwable exception= Assertions.catchThrowable(()-> service.save(book));

        //Verificações
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn ja cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }



    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest(){
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacoes
        assertThat( foundBook.isPresent() ).isTrue();
        assertThat( foundBook.get().getId()).isEqualTo(id);
        assertThat( foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat( foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }
    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base.")
    public void bookNotFoundByIdTest(){
        Long id = 1l;
        when( repository.findById(id) ).thenReturn(Optional.empty());

        //execucao
        Optional<Book> book = service.getById(id);

        //verificacoes
        assertThat( book.isPresent() ).isFalse();

    }
    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest(){
     Book book = Book.builder().id(1L).build();

    org.junit.jupiter.api.Assertions.assertDoesNotThrow(()-> service.delete(book));

     Mockito.verify(repository, Mockito.times(1)).delete(book);
    }
    @Test
    @DisplayName("Deve ocorrer um erro ao tentar deletar um livro inexistente.")
    public void deleteInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> service.delete(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar atualizar um livro inexistente.")
    public void updateInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> service.update(book));

        Mockito.verify(repository, Mockito.never()).save(book);
    }
@Test
@DisplayName("Deve atualizar um livro.")
    public void updateBookTest(){
    //cenario
    Long id = 1L;

    //livro a atualizar
     Book updatingBook = Book.builder().id(id).build();

     //simulação
    Book updatedBook = createValidBook();
     updatedBook.setId(id);
     when(repository.save(updatingBook)).thenReturn(updatedBook);

     //execução
    Book book = service.update(updatingBook);

     //verificações
    assertThat(book.getId()).isEqualTo(updatedBook.getId());
    assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
    assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
    assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        //cenario
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Book> result = service.find(book, pageRequest);


        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

}
