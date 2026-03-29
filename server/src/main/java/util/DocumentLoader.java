package util;

import model.Document;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentLoader {

    // Loads .txt files from the docs/ folder next to where you run the server
    private static final String DOCS_DIR = "docs";

    public static List<Document> loadDocuments() {
        List<Document> documents = new ArrayList<>();
        File dir = new File(DOCS_DIR);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("[DocumentLoader] No 'docs/' folder found — starting with no documents.");
            return documents;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) return documents;

        for (File file : files) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                documents.add(new Document(file.getName(), content));
                System.out.println("[DocumentLoader] Loaded: " + file.getName());
            } catch (IOException e) {
                System.out.println("[DocumentLoader] Could not read: " + file.getName());
            }
        }

        return documents;
    }
}