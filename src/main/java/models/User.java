package models;

/*
  Base User class for the store.
 */

import java.io.Serializable;

public abstract class User implements Serializable {
// [Katramados] ChatGPT assisted, simple auto-incrementing ID for users, Finalized Nov. 30th

    private static int counter = 1;

    private final int id;
    private String username;
    private String password;
    private final Role role;

    public enum Role {
        CUSTOMER,
        OWNER
    }
 // [Katramados] ChatGPT assisted, constructor validates fields and sets the role, Finalized Nov. 30th
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
// [Katramados] Returns unique ID, Finalized Nov. 30th
    public int getID() { return id; }
// [Katramados] Username getter, Finalized Nov. 30th
    public String getUsername() { return username; }
// [Katramados] Validates and updates the username, Finalized Nov. 30th
    public void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        this.username = username;
    }
// [Katramados] Checks password equality, returns boolean, Finalized Nov. 30th
    public boolean checkPass(String input) {
        return password.equals(input);
    }
// [Katramados] Validates and updates the password, Finalized Nov. 30th
    public void setPassword(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = password;
    }

    public String getPassword() {return password; }

    public Role getRole() { return role; }
// [Katramados] Simple string output (no sensitive data), Finalized Nov. 30th
    @Override
    public String toString() {
        return "User: " + username + ", ID: " + id + " || Type: " + role;
    }
}
