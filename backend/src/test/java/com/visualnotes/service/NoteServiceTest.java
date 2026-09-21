package com.visualnotes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.AiService;
import com.visualnotes.dto.GenerateNotesRequest;
import com.visualnotes.dto.NoteDocumentDto;
import com.visualnotes.dto.PageContentDto;
import com.visualnotes.entity.NoteDocument;
import com.visualnotes.entity.NotePage;
import com.visualnotes.entity.User;
import com.visualnotes.repository.NoteDocumentRepository;
import com.visualnotes.repository.NotePageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteDocumentRepository documentRepository;
    @Mock
    private NotePageRepository pageRepository;
    @Mock
    private AiService aiService;
    @Mock
    private AuthService authService;

    private ObjectMapper objectMapper;
    private NoteService noteService;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        noteService = new NoteService(
                documentRepository,
                pageRepository,
                aiService,
                authService,
                objectMapper
        );

        alice = new User();
        alice.setId(10L);
        alice.setEmail("alice@test.com");
        alice.setName("Alice");

        bob = new User();
        bob.setId(20L);
        bob.setEmail("bob@test.com");
        bob.setName("Bob");
    }

    @Test
    void testGetDocumentByIdOwnershipCheck() {
        NoteDocument doc = new NoteDocument();
        doc.setId(100L);
        doc.setUser(alice);
        doc.setTitle("Alice's Private Notes");
        doc.setPages(new ArrayList<>());

        when(documentRepository.findByIdWithPages(100L)).thenReturn(Optional.of(doc));

        // Alice accessing her own document
        when(authService.getCurrentUser()).thenReturn(alice);
        NoteDocumentDto result = noteService.getDocumentById(100L);
        assertNotNull(result);
        assertEquals(100L, result.getId());

        // Bob attempting to access Alice's document -> SecurityException
        when(authService.getCurrentUser()).thenReturn(bob);
        assertThrows(SecurityException.class, () -> {
            noteService.getDocumentById(100L);
        });

        // Anonymous user attempting to access Alice's document -> SecurityException
        when(authService.getCurrentUser()).thenReturn(null);
        assertThrows(SecurityException.class, () -> {
            noteService.getDocumentById(100L);
        });
    }

    @Test
    void testRenameDocumentOwnershipCheck() {
        NoteDocument doc = new NoteDocument();
        doc.setId(200L);
        doc.setUser(alice);
        doc.setTitle("Original Title");
        doc.setPages(new ArrayList<>());

        when(documentRepository.findByIdWithPages(200L)).thenReturn(Optional.of(doc));
        when(documentRepository.save(any(NoteDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        // Alice renames her document
        when(authService.getCurrentUser()).thenReturn(alice);
        NoteDocumentDto renamed = noteService.renameDocument(200L, "New Title");
        assertEquals("New Title", renamed.getTitle());

        // Bob tries to rename Alice's document -> SecurityException
        when(authService.getCurrentUser()).thenReturn(bob);
        assertThrows(SecurityException.class, () -> {
            noteService.renameDocument(200L, "Hacked Title");
        });
    }

    @Test
    void testDeleteDocumentOwnershipCheck() {
        NoteDocument doc = new NoteDocument();
        doc.setId(300L);
        doc.setUser(alice);

        when(documentRepository.findById(300L)).thenReturn(Optional.of(doc));

        // Bob tries to delete Alice's document -> SecurityException
        when(authService.getCurrentUser()).thenReturn(bob);
        assertThrows(SecurityException.class, () -> {
            noteService.deleteDocument(300L);
        });

        // Alice deletes her document
        when(authService.getCurrentUser()).thenReturn(alice);
        assertDoesNotThrow(() -> {
            noteService.deleteDocument(300L);
        });
        verify(documentRepository).delete(doc);
    }
}
