package com.visualnotes.repository;

import com.visualnotes.entity.NoteDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteDocumentRepository extends JpaRepository<NoteDocument, Long> {

    @Query("SELECT d FROM NoteDocument d LEFT JOIN FETCH d.pages WHERE d.id = :id")
    Optional<NoteDocument> findByIdWithPages(@Param("id") Long id);

    @Query("SELECT d FROM NoteDocument d WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
    List<NoteDocument> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT d FROM NoteDocument d WHERE d.user.id = :userId AND " +
           "(LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.originalPrompt) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY d.createdAt DESC")
    List<NoteDocument> searchByUserIdAndQuery(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT d FROM NoteDocument d WHERE d.user IS NULL ORDER BY d.createdAt DESC")
    List<NoteDocument> findAnonymousDocuments();

    @Query("SELECT d FROM NoteDocument d WHERE d.user IS NULL AND " +
           "(LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.originalPrompt) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY d.createdAt DESC")
    List<NoteDocument> searchAnonymousDocuments(@Param("query") String query);
}
