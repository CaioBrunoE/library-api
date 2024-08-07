package com.caiobruno.library_api.resource;

import com.caiobruno.library_api.exceptions.ApiErros;
import com.caiobruno.library_api.exceptions.BusinessException;
import com.caiobruno.library_api.model.dto.BookDTO;
import com.caiobruno.library_api.model.entity.Book;
import com.caiobruno.library_api.service.imp.BookServiceImp;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    BookServiceImp service;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        Book entity = modelMapper.map(dto, Book.class);
        service.save(entity);
        return modelMapper.map( entity, BookDTO.class);
    }

    @GetMapping("/{id}")
    public BookDTO get(@PathVariable Long id) {
        try {
            Book book = service.getById(id).get();
            return modelMapper.map(book, BookDTO.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
        service.delete(book);
    }

    @PutMapping("/{id}")
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO dto){
        return service.getById(id).map( book -> {

            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);
            return modelMapper.map(book, BookDTO.class);

        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
     Book filter = modelMapper.map(dto, Book.class);

     Page<Book> result = service.find(filter, pageRequest);

    List<BookDTO> list = result.getContent()
             .stream()
             .map(entity -> modelMapper.map(entity, BookDTO.class))
             .collect(Collectors.toList());

   return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
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
