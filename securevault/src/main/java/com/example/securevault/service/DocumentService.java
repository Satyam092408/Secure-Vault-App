package com.example.securevault.service;

import com.example.securevault.model.SecureDocument;
import com.example.securevault.model.User;
import com.example.securevault.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    private final Path rootLocation;
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.rootLocation = Paths.get("./uploads");
        this.documentRepository = documentRepository;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public SecureDocument store(MultipartFile file, String description, User user) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));

        SecureDocument document = new SecureDocument();
        document.setName(file.getOriginalFilename());
        document.setDescription(description);
        document.setFilePath(filename);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setUploadDate(LocalDateTime.now());
        document.setUser(user);

        return documentRepository.save(document);
    }

    public List<SecureDocument> getAllUserDocuments(User user) {
        return documentRepository.findByUser(user);
    }

    public List<SecureDocument> searchDocuments(User user, String query) {
        return documentRepository.searchByUserAndQuery(user, query);
    }

    public List<SecureDocument> filterDocumentsByType(User user, String fileType) {
        return documentRepository.findByUserAndFileTypeContaining(user, fileType);
    }

    public void deleteDocument(Long id, User user) throws IOException {
        SecureDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!document.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        Files.deleteIfExists(rootLocation.resolve(document.getFilePath()));
        documentRepository.delete(document);
    }
}