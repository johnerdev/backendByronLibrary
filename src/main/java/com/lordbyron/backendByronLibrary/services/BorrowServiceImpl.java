package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.entity.Book;
import com.lordbyron.backendByronLibrary.entity.Borrow;
import com.lordbyron.backendByronLibrary.entity.StateBorrow;
import com.lordbyron.backendByronLibrary.entity.Users;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.repository.BookRepository;
import com.lordbyron.backendByronLibrary.repository.BorrowRepository;
import com.lordbyron.backendByronLibrary.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class BorrowServiceImpl implements BorrowService {
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);
    private BorrowRepository borrowRepository;
    private BookRepository bookRepository;
    private UsersRepository  usersRepository;
//   public BorrowServiceImpl(BorrowRepository borrowRepository){this.borrowRepository=borrowRepository;}

    @Override
    public List<Borrow> getBorrows() {
        log.info("Fetching all borrows");

        // Obtener la lista de prestamos
        List<Borrow> borrow = (List<Borrow>)borrowRepository.findAll();

        // Validar si la lista está vacía
        if (borrow.isEmpty()) {
            log.warn("No borrow found in the database");
            throw new ExceptionMessage("No se encontraron prestamos registrados");
        }

        log.info("Total borrows found: {}", borrow.size());
        return borrow;
    }

    @Override
    public ResponseEntity<?> saveBorrow(Long id, String userEmail) {

        // Verificar si el usuario existe
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el email: " + userEmail));

        // Verificar si el libro existe
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con el ID: " + id));

        // Validar disponibilidad del libro
        if (!book.getAvailable()) {
            throw new IllegalStateException("El libro no está disponible para préstamo");
        }

        // Asignar el estado del libro y el préstamo
        book.setAvailable(false);
        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setUser(user); // Asignar el usuario que realiza el préstamo
        borrow.setDateBorrow(LocalDate.now());
        borrow.setDateDevolution(LocalDate.now().plusWeeks(1)); // 1 semana de préstamo
        borrow.setState(StateBorrow.PENDIENTE);

        // Guardar el libro actualizado y el préstamo
        bookRepository.save(book);
        borrowRepository.save(borrow);

        log.info("Préstamo creado con éxito para el usuario: {}", user.getEmail());

        // Construir la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Préstamo creado con éxito!");
         return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<Map<String, String>> updateBorrow(Borrow borrow) {
        // Verificar si el préstamo existe
        Borrow existingBorrow = borrowRepository.findById(borrow.getIdBorrow())
                .orElseThrow(() -> new ExceptionMessage("Préstamo no encontrado con el ID: " + borrow.getIdBorrow()));

        // Validar si el préstamo ya está marcado como devuelto
        if (existingBorrow.getState() == StateBorrow.DEVUELTO) {
            throw new ExceptionMessage("El préstamo ya ha sido marcado como devuelto.");
        }

        // Verificar si el libro asociado existe
        Book book = bookRepository.findById(borrow.getBook().getId())
                .orElseThrow(() -> new ExceptionMessage("Libro no encontrado con el ID: " + borrow.getBook().getId()));

        // Actualizar el estado del libro y el préstamo
        book.setAvailable(true); // Marcar el libro como disponible
        existingBorrow.setDateDevolution(LocalDate.now()); // Actualizar la fecha de devolución
        existingBorrow.setState(StateBorrow.DEVUELTO); // Cambiar el estado a devuelto

        // Guardar cambios en la base de datos
        bookRepository.save(book);
        Borrow updatedBorrow = borrowRepository.save(existingBorrow);


        // Construir la respuesta
        Map<String, String> response = new HashMap<>();
        response.put("message", "Préstamo devuelto con éxito!");
        return ResponseEntity.ok(response);
    }

    @Override
    public Long countBorrow() {
        return borrowRepository.count();
    }

    @Override
    public Borrow getBorrow(Long id) {
        // Verificar si el préstamo existe
        Borrow existingBorrow = borrowRepository.findById(id)
                .orElseThrow(() -> new ExceptionMessage("Préstamo no encontrado con el ID: " + id));
        return existingBorrow;
    }
}