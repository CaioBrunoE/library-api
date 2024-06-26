package com.caiobruno.library_api.resource;

import com.caiobruno.library_api.exceptions.ApiErros;
import com.caiobruno.library_api.exceptions.BusinessException;
import com.caiobruno.library_api.model.dto.BookDTO;
import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/books")
@ResponseStatus(HttpStatus.CREATED)
public class BookController {
    @Autowired
    BookService service;
    @Autowired
    ModelMapper modelMapper;
    @PostMapping
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        Book entity = modelMapper.map(dto, Book.class);
        service.save(entity);
        return modelMapper.map( entity, BookDTO.class);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErros(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleBusinessExceptions(BusinessException ex){
        return new ApiErros(ex);
    }
}
