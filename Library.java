package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Library {
    private ObservableList<Document> documents = FXCollections.observableArrayList();
    private Map<String, User> users = new HashMap<>();

    private static final String DOCUMENTS_FILE = "documents.csv";
    private static final String BORROWED_FILE = "borrowed.csv";
    private static final String RATINGS_FILE = "ratings.csv";
    private static final String ACCOUNT_FILE = "account.csv"; // Quản lý
    private static final String USER_FILE = "user.csv"; // Người dùng

    public Library() {
        loadDocumentsFromFile();
        loadUsersFromFile();
        loadBorrowedRecordsFromFile();
    }

    public ObservableList<Document> getDocuments() {
        return documents;
    }

    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }

    public void addDocument(Document doc) {
        documents.add(doc);
        saveDocumentsToFile();
    }

    public void removeDocumentById(String id) {
        documents.removeIf(doc -> doc.getId().equals(id));
        saveDocumentsToFile();
    }

    public Document findById(String id) {
        return documents.stream()
                .filter(doc -> doc.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
        saveUsersToFile();
    }

    public void clearDocuments() {
        documents.clear();
        saveDocumentsToFile();
    }

    private void loadDocumentsFromFile() {
        documents.clear(); // Xóa toàn bộ dữ liệu cũ
        Map<String, Document> uniqueDocuments = new HashMap<>();
        File file = new File(DOCUMENTS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(DOCUMENTS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String id = parts[0];
                        String title = parts[1];
                        String author = parts[2];
                        int quantity = Integer.parseInt(parts[3]);
                        String subject = parts[4];
                        if (uniqueDocuments.containsKey(id)) {
                            Document existingDoc = uniqueDocuments.get(id);
                            existingDoc.setQuantity(existingDoc.getQuantity() + quantity);
                        } else {
                            uniqueDocuments.put(id, new Book(id, title, author, quantity, subject));
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load documents: " + e.getMessage());
            }
        }
        documents.addAll(uniqueDocuments.values());
    }

    public void saveDocumentsToFile() {
        Map<String, Document> uniqueDocuments = new HashMap<>();
        for (Document doc : documents) {
            if (uniqueDocuments.containsKey(doc.getId())) {
                Document existingDoc = uniqueDocuments.get(doc.getId());
                existingDoc.setQuantity(existingDoc.getQuantity() + doc.getQuantity());
            } else {
                uniqueDocuments.put(doc.getId(), new Book(doc.getId(), doc.getTitle(), doc.getAuthor(), doc.getQuantity(), doc.getSubject()));
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOCUMENTS_FILE))) {
            for (Document doc : uniqueDocuments.values()) {
                writer.write(doc.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save documents: " + e.getMessage());
        }
    }

    private void loadUsersFromFile() {
        users.clear();
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        User user = new User(parts[0], parts[1]);
                        user.setName(parts[2]);
                        users.put(parts[0], user);
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to load users: " + e.getMessage());
            }
        }
    }

    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : users.values()) {
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getName());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    private void loadBorrowedRecordsFromFile() {
        for (User user : users.values()) {
            user.getBorrowedDocuments().clear();
        }
        File file = new File(BORROWED_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(BORROWED_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String userId = parts[0];
                        String docId = parts[1];
                        int quantity = Integer.parseInt(parts[2]);
                        User user = users.get(userId);
                        Document doc = findById(docId);
                        if (user != null && doc != null) {
                            user.borrowDocument(doc, quantity);
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load borrowed records: " + e.getMessage());
            }
        }
    }

    public void saveBorrowedRecordsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BORROWED_FILE))) {
            for (User user : users.values()) {
                for (Map.Entry<String, Integer> entry : user.getBorrowedDocuments().entrySet()) {
                    String docId = entry.getKey();
                    int quantity = entry.getValue();
                    String record = user.getUsername() + "," + docId + "," + quantity;
                    writer.write(record);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to save borrowed records: " + e.getMessage());
        }
    }
}