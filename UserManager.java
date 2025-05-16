package com.example.demo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to manage library admin accounts.
 * Handles registration, login, and persistence of admin accounts.
 */
public class UserManager {
    private static UserManager instance = null; // Singleton instance
    private Map<String, User> accounts = new HashMap<>();
    private static final String ACCOUNT_FILE = "account.csv";

    // Private constructor for Singleton pattern
    UserManager() {
        loadAccountsFromFile();
    }

    /**
     * Gets the singleton instance of UserManager.
     * @return The single instance of UserManager.
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Registers a new admin user.
     * @param username The username of the new admin.
     * @param password The password of the new admin.
     * @return true if registration is successful, false if username already exists.
     */
    public boolean register(String username, String password) {
        if (accounts.containsKey(username)) return false;
        User newUser = new User(username, password);
        accounts.put(username, newUser);
        saveAccountsToFile();
        return true;
    }

    /**
     * Logs in an admin user.
     * @param username The username to log in.
     * @param password The password to verify.
     * @return The User object if login is successful, null otherwise.
     */
    public User login(String username, String password) {
        User user = accounts.get(username);
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }

    /**
     * Saves all admin accounts to account.csv.
     */
    public void saveAccountsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ACCOUNT_FILE))) {
            for (User user : accounts.values()) {
                pw.println(user.getUsername() + "," + user.getPassword());
            }
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    /**
     * Loads admin accounts from account.csv.
     */
    private void loadAccountsFromFile() {
        File file = new File(ACCOUNT_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 2) {
                    User user = new User(p[0], p[1]);
                    accounts.put(p[0], user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    /**
     * Gets a user by their username.
     * @param username The username to look up.
     * @return The User object if found, null otherwise.
     */
    public User getUser(String username) {
        return accounts.get(username);
    }
}
