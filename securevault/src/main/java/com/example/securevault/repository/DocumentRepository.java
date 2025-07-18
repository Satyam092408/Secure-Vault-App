package com.example.securevault.repository;

import com.example.securevault.model.SecureDocument;
import com.example.securevault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<SecureDocument, Long> {
    List<SecureDocument> findByUser(User user);

    @Query("SELECT d FROM SecureDocument d WHERE d.user = :user AND " +
            "(LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<SecureDocument> searchByUserAndQuery(@Param("user") User user, @Param("query") String query);

    List<SecureDocument> findByUserAndFileTypeContaining(User user, String fileType);
}