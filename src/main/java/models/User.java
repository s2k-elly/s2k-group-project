package models;

/*
  Base User class for the store.
 */

import java.io.Serializable;

public abstract class User implements Serializable {

    // simple auto-incrementing ID for users
    private static int counter = 1;

    private final int id;
    private String username;
    private String password;
    private final Role role;

    public enum Role {
        CUSTOMER,
        OWNER
    }

    // Constructor used by subclasses
    public User(String username, String password, Role role) {
        this.id = counter++;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, Role role, int id) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.id = id;
    }

    public int getID() { return id; }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        this.username = username;
    }

    public boolean checkPass(String input) {
        return password.equals(input);
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = password;
    }

    public String getPassword() {return password; }

    public Role getRole() { return role; }

    @Override
    public String toString() {
        return "User: " + username + ", ID: " + id + " || Type: " + role;
    }
}