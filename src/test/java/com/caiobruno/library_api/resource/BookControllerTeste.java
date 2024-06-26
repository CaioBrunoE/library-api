package com.caiobruno.library_api.resource;


import com.caiobruno.library_api.exceptions.ApiErros;
import com.caiobruno.library_api.exceptions.BusinessException;
import com.caiobruno.library_api.model.dto.BookDTO;
import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTeste {
    static  String  BOOK_API = "/api/books";
    @Autowired
    MockMvc mvc ;
    @MockBean
    BookService service;

    private BookDTO createNewBook(){
        return BookDTO.builder().id(1L).title("As aventuras de Fino").author("Arhur").isbn("102").build();
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso. ")
    public void createBookTest() throws Exception {

        BookDTO dto = createNewBook();

        Book savedBook = Book.builder().title("As aventuras de Fino").author("Arhur").isbn("102").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
            .perform(request)
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("title").value("As aventuras de Fino"))
            .andExpect(MockMvcResultMatchers.jsonPath("author").value("Arhur"))
            .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("102"));


    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para a criação do livro ")
    public void createInvalidBookTest() throws Exception {

       BookDTO dto = new BookDTO();
       String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }


    @Test
    @DisplayName("Deve lançar error ao tentar cadastrar um livro com isbn ja ultilizado por outro. ")
    public void createBookWithDuplicatedIsbn () throws Exception{

        BookDTO dto = createNewBook();
        String menssageErro = "Isbn ja cadastrado";
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(menssageErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(menssageErro));
    }
}
