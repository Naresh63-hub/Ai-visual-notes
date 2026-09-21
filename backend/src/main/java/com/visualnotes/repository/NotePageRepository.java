package com.visualnotes.repository;

import com.visualnotes.entity.NotePage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotePageRepository extends JpaRepository<NotePage, Long> {
    List<NotePage> findByDocumentIdOrderByPageNumberAsc(Long documentId);
    Optional<NotePage> findByDocumentIdAndPageNumber(Long documentId, Integer pageNumber);
}
