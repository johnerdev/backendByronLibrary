package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.entity.Borrow;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BorrowService {
    List<Borrow> getBorrows();
     ResponseEntity<?> saveBorrow(Long id, String userEmail);
    ResponseEntity<Map<String, String>> updateBorrow(final Borrow borrow);
    Long countBorrow();
    Borrow getBorrow(Long id);
}
