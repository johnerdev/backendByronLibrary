package com.lordbyron.backendByronLibrary.controller;

import com.lordbyron.backendByronLibrary.entity.Book;
import com.lordbyron.backendByronLibrary.entity.Users;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.services.BookService;
import com.lordbyron.backendByronLibrary.services.UsersServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
/*@CrossOrigin("*")*/
@RequestMapping("/book")
public class BookController {
    private final BookService  bookService;
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or hasRole('USER')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllBooks() {
        try {
            Iterable<Book> books = bookService.getbooks();
            return ResponseEntity.ok(books);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @PostMapping("/add")
    public ResponseEntity<?>addBook(@RequestBody Book book){
        try {
            bookService.saveBook(book);
            return ResponseEntity.ok(Map.of("message", "Libro creado con exito"));
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateBook(@RequestBody Book book) {
        try {
            return bookService.updateBook(book);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/count")
    public Long countBooks(){
        return bookService.countBooks();
    }

    @GetMapping("find/{title}")
    public ResponseEntity<?> getUser(@PathVariable("title") String title) {
        try {
            List <Book> book = bookService.getBook(title);
            return ResponseEntity.ok(book);
        } catch (ExceptionMessage e) {
            log.error("Error fetching user: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            return ResponseEntity.ok(Map.of("message", e.getMessage()));
        }
    }
}
