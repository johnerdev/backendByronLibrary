package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.entity.Book;
 import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class BookServiceImpl implements BookService {
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);
    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public ResponseEntity<Map<String, String>> saveBook(final Book book) {
        log.info("Saving a new book with title: {}", book.getTitle());

        // Verificar si el libro ya existe por su numero de serie
        bookRepository.findBySeries(book.getSeries())
                .ifPresent(existingIsbn -> {
                    throw new ExceptionMessage("El libro ya está registrado con isbn: " + book.getIsbn());
                });

        // Guardar el nuevo libro
        Book savedBook = bookRepository.save(book);

        log.info("Libro creado con éxito con ISBN: {}", savedBook.getIsbn());

        // Construir la respuesta
        Map<String, String> response = new HashMap<>();
        response.put("message", "Libro creado con éxito!");
        response.put("ISBN", String.valueOf(savedBook.getIsbn()));

        return ResponseEntity.ok(response);
    }

    @Override
    public List<Book> getBook(String title) {
        log.info("Fetching book with title: {}", title);
        // Buscar libros con el título dado
        List<Book> books = bookRepository.findByTitle(title);
        // Si no se encuentran libros, lanzar una excepción
        if (books.isEmpty()) {
            log.warn("Book with title {} not found", title);
            throw new ExceptionMessage("Libro no registrado con el título: " + title);
        }

        return books;

    }

    @Override
    public List<Book> getbooks() {
        log.info("Fetching all books");

        // Obtener la lista de libros
        List<Book> books = (List<Book>) bookRepository.findAll();

        // Validar si la lista está vacía
        if (books.isEmpty()) {
            log.warn("No books found in the database");
            throw new ExceptionMessage("No se encontraron books registrados");
        }

        log.info("Total books found: {}", books.size());
        return books;
    }

    @Override
    public ResponseEntity<Map<String, String>> updateBook(final Book book) {
        // Buscar el libro existente por id
        var existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new ExceptionMessage("No se encontró un libro con el title: " + book.getTitle()));

        // Actualizar los campos necesarios
        if (book.getTitle() != null && !book.getTitle().isBlank()) {
            existingBook.setTitle(book.getTitle());
        }
        if (book.getAuthor() != null && !book.getAuthor().isBlank()) {
            existingBook.setAuthor(book.getAuthor());
        }
        if (book.getIdentifier() != null && !book.getIdentifier().isBlank()) {
            existingBook.setIdentifier(book.getIdentifier());
        }
        existingBook.setAcquisition(book.getAcquisition());
        existingBook.setPublished(book.getPublished());
        existingBook.setGenre(book.getGenre());
        existingBook.setLanguage(book.getLanguage());
        existingBook.setAcquisition(book.getAcquisition());
        existingBook.setYear(book.getYear());
        existingBook.setCity(book.getCity());
        existingBook.setEdition(book.getEdition());
        existingBook.setDescription(book.getDescription());
        existingBook.setContent(book.getContent());
        existingBook.setArea(book.getArea());
        existingBook.setDescription(book.getDescription());
        existingBook.setPages(book.getPages());
        existingBook.setLevel(book.getLevel());
        existingBook.setLocation(book.getLocation());
        existingBook.setSeries(book.getSeries());
        // Guardar el libro actualizado en la base de datos
        try {
            bookRepository.save(existingBook);
        } catch (Exception e) {
            throw new ExceptionMessage("Error al guardar el libro actualizado: " + e.getMessage());
        }

        // Construir la respuesta
        Map<String, String> response = Map.of("message", "libro actualizado con éxito!");
        return ResponseEntity.ok(response);
    }

    @Override
    public Long countBooks() {
        return bookRepository.count();
    }
}
