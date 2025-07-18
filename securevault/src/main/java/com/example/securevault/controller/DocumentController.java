package com.example.securevault.controller;

import com.example.securevault.model.SecureDocument;
import com.example.securevault.model.User;
import com.example.securevault.service.DocumentService;
import com.example.securevault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String documents(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        List<SecureDocument> documents = documentService.getAllUserDocuments(user);
        model.addAttribute("documents", documents);
        return "documents";
    }

    @PostMapping("/upload")
    public String uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            Authentication authentication) throws IOException {

        User user = userService.findByUsername(authentication.getName());
        documentService.store(file, description, user);
        return "redirect:/documents";
    }

    @GetMapping("/search")
    public String searchDocuments(
            @RequestParam("query") String query,
            Authentication authentication,
            Model model) {

        User user = userService.findByUsername(authentication.getName());
        List<SecureDocument> documents = documentService.searchDocuments(user, query);
        model.addAttribute("documents", documents);
        model.addAttribute("searchQuery", query);
        return "documents";
    }

    @GetMapping("/filter")
    public String filterDocuments(
            @RequestParam("type") String type,
            Authentication authentication,
            Model model) {

        User user = userService.findByUsername(authentication.getName());
        List<SecureDocument> documents = documentService.filterDocumentsByType(user, type);
        model.addAttribute("documents", documents);
        model.addAttribute("filterType", type);
        return "documents";
    }

    @PostMapping("/delete/{id}")
    public String deleteDocument(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        User user = userService.findByUsername(authentication.getName());
        documentService.deleteDocument(id, user);
        return "redirect:/documents";
    }


}