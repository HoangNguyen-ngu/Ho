package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String password;
    private String name;
    private Map<String, Integer> borrowed = new HashMap<>(); // Sử dụng Map để theo dõi số lượng mượn theo docId

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name != null ? name : username; }
    public void setName(String name) { this.name = name; }

    public void borrowDocument(Document doc, int quantity) {
        if (doc.getQuantity() >= quantity) {
            doc.setQuantity(doc.getQuantity() - quantity);
            borrowed.put(doc.getId(), borrowed.getOrDefault(doc.getId(), 0) + quantity);
        }
    }

    public void returnDocument(Document doc, int quantity) {
        if (borrowed.containsKey(doc.getId()) && borrowed.get(doc.getId()) >= quantity) {
            doc.setQuantity(doc.getQuantity() + quantity);
            int remaining = borrowed.get(doc.getId()) - quantity;
            if (remaining > 0) {
                borrowed.put(doc.getId(), remaining);
            } else {
                borrowed.remove(doc.getId());
            }
        }
    }

    public Map<String, Integer> getBorrowedDocuments() {
        return new HashMap<>(borrowed); // Trả về bản sao để tránh sửa trực tiếp
    }

    public int getBorrowedQuantity(String docId) {
        return borrowed.getOrDefault(docId, 0);
    }
}