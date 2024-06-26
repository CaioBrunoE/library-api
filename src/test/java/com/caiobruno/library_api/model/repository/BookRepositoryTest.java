package com.caiobruno.library_api.model.repository;

import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@DataJpaTest
public class BookRepositoryTest {

  @Autowired
    TestEntityManager entityManager;

  @Autowired
    BookRepository repository;

  @Test
  @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado ")
  public void returnTrueWhenIsbnExists(){
      //cenario
      String  isbn = "123";
      Book book = Book.builder().title("As aventuras de Fino").author("Arhur").isbn(isbn).build();
      entityManager.persist(book);
      //execução
      boolean exists  = repository.existsByIsbn(isbn);

      //verificação
      assertThat(exists).isTrue();
  }

    @Test
    @DisplayName("Deve retornar falso quando não  existir um livro na base com o isbn informado ")
    public void returnFalseWhenIsbnDoestntExists(){
        //cenario
        String  isbn = "123";

        //execução
        boolean exists  = repository.existsByIsbn(isbn);

        //verificação
        assertThat(exists).isFalse();
    }
}
