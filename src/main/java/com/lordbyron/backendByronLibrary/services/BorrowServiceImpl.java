package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.Dto.userDto.BorrowDto.BorrowDTO;
import com.lordbyron.backendByronLibrary.entity.*;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.repository.BookRepository;
import com.lordbyron.backendByronLibrary.repository.BorrowRepository;
import com.lordbyron.backendByronLibrary.repository.ReserveRepository;
import com.lordbyron.backendByronLibrary.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class BorrowServiceImpl implements BorrowService {
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);
    private BorrowRepository borrowRepository;
    private BookRepository bookRepository;
    private UsersRepository  usersRepository;
    private ReserveRepository reserveRepository;
//   public BorrowServiceImpl(BorrowRepository borrowRepository){this.borrowRepository=borrowRepository;}

    @Override
    public List<BorrowDTO> getBorrows() {
        log.info("Fetching all borrows");

        // Obtener la lista de préstamos
        List<Borrow> borrows = (List<Borrow>) borrowRepository.findAll();

        // Validar si la lista está vacía
        if (borrows.isEmpty()) {
            log.warn("No borrows found in the database");
            throw new ExceptionMessage("No se encontraron préstamos registrados");
        }

        // Convertir la lista de préstamos a una lista de DTOs
        List<BorrowDTO> borrowDTOs = borrows.stream().map(nborrow -> new BorrowDTO(
                nborrow.getIdBorrow(),
                nborrow.getBook().getTitle(),
                nborrow.getUser().getEmail(),
                nborrow.getDateBorrow().toString(),
                nborrow.getDateDevolution().toString(),
                nborrow.getState().toString(),
                nborrow.getDescription()
        )).collect(Collectors.toList());

        log.info("Total borrows found: {}", borrowDTOs.size());
        return borrowDTOs;
    }
    @Override
    public ResponseEntity<?> saveBorrow(Long bookId, String userEmail) {
        log.info("Creating a new borrow for book ID: {} and user email: {}", bookId, userEmail);

        // Validar entrada
        if (bookId == null || userEmail == null || userEmail.isBlank()) {
            log.warn("Invalid input: bookId or userEmail is null/empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Los parámetros 'bookId' y 'userEmail' son obligatorios y no pueden estar vacíos."));
        }

        try {
            // Validar si el usuario existe
            Users user = usersRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ExceptionMessage("Usuario no encontrado con el email: " + userEmail));

            // Validar si el libro existe
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ExceptionMessage("Libro no encontrado con el ID: " + bookId));

            // Verificar si el libro tiene una reserva activa
            Optional<Reserve> reserve = reserveRepository.findByBook_IdAndState(bookId, StateReserve.ACTIVA);
            if (reserve.isPresent() && !reserve.get().getUser().getId().equals(user.getId())) {
                log.warn("Attempted borrow for reserved book ID: {}", bookId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El libro está reservado para otro usuario."));
            }

            // Verificar si el libro está actualmente prestado
            Optional<Borrow> activeBorrow = borrowRepository.findByBookIdAndState(bookId, StateBorrow.PENDIENTE);
            if (activeBorrow.isPresent()) {
                log.warn("Attempted borrow for a book already borrowed. Book ID: {}", bookId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El libro ya está actualmente prestado."));
            }

            // Crear y configurar el préstamo
            Borrow borrow = new Borrow();
            borrow.setBook(book);
            borrow.setUser(user);
            borrow.setDateBorrow(LocalDate.now());
            borrow.setDateDevolution(LocalDate.now().plusWeeks(1)); // 1 semana de préstamo
            borrow.setState(StateBorrow.PENDIENTE);

            // Actualizar la reserva (si existe) como completada
            reserve.ifPresent(r -> {
                r.setDateReserve(LocalDate.now());
                r.setState(StateReserve.COMPLETADA);
                reserveRepository.save(r);
            });

            // Actualizar el estado del libro y guardar entidades
            book.setAvailable(false);
            bookRepository.save(book);
            borrowRepository.save(borrow);

            log.info("Préstamo creado con éxito para el usuario: {} y libro ID: {}", user.getEmail(), bookId);

            // Construir y devolver la respuesta
            Map<String, Object> response = Map.of(
                    "message", "Préstamo creado con éxito!"
            );

            return ResponseEntity.ok(response);

        } catch (ExceptionMessage ex) {
            log.error("Error creating borrow: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error creating borrow: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado. Por favor, inténtelo más tarde."));
        }
    }





    @Override
    public ResponseEntity<Map<String, String>> updateBorrow(Long id) {
        // Verificar si el préstamo existe
        Borrow existingBorrow = borrowRepository.findById(id)
                .orElseThrow(() -> new ExceptionMessage("Préstamo no encontrado con el ID: " ));

        // Validar si el préstamo ya está marcado como devuelto
        if (existingBorrow.getState() == StateBorrow.DEVUELTO) {
            throw new ExceptionMessage("El préstamo ya ha sido marcado como devuelto.");
        }

        // Verificar si el libro asociado existe
        Book book = bookRepository.findById(existingBorrow.getBook().getId())
                .orElseThrow(() -> new ExceptionMessage("Libro no encontrado con el ID: " + existingBorrow.getBook().getId()));

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
    public List<BorrowDTO> getBorrow(String email) {
        log.info("Fetching borrows for user email: {}", email);

        // Buscar el usuario por email
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ExceptionMessage("Usuario no encontrado con el correo: " + email));

        // Buscar los préstamos asociados al usuario
        List<Borrow> borrows = borrowRepository.findByUserId(user.getId());

        // Validar si no se encontraron préstamos
        if (borrows.isEmpty()) {
            throw new ExceptionMessage("No se encontraron préstamos para el usuario con el correo: " + email);
        }

        log.info("Found {} borrows for user email: {}", borrows.size(), email);

        // Mapear los préstamos a BorrowDTO
        return borrows.stream()
                .map(borrow -> new BorrowDTO(
                        borrow.getIdBorrow(),
                        borrow.getBook().getTitle(),
                        borrow.getUser().getEmail(),
                        borrow.getDateBorrow().toString(),
                        borrow.getDateDevolution().toString(),
                        borrow.getState().toString(),
                        borrow.getBook().getDescription() // Agregar descripción del libro
                ))
                .collect(Collectors.toList());
    }


}