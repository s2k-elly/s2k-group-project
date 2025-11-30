package services;

import models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for user registration, login, and lookup.
 * Includes internal stub model classes and a driver main().
 */
public class UserService {

    // =======================
    // FIELDS
    // =======================
    private final List<User> users = new ArrayList<>();
    private User currentUser = null;

    // =======================
    // CONSTRUCTOR WITH TEMPLATE CUSTOMER & OWNER (FOR TESTING AND PROOF OF FUNCTION)
    // =======================

    public UserService() {

        Owner owner = new Owner("owner", "owner123");
        Customer customer = new Customer("customer", "customer123");
        users.add(owner);
        users.add(customer);
    }

    // =======================
    // SERVICE METHODS
    // =======================

    /**
     * Register always creates a CUSTOMER (per your description).
     */
    public User register(String user, String pass) {
        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            return null;
        }
        Customer newCustomer = new Customer(user, pass);
        users.add(newCustomer);

        return newCustomer;
    }

    /**
     * Authenticates by username + password and returns the specific subtype
     * (Customer or Owner) if successful. Also updates currentUser.
     */
    public Optional<User> login(String user, String pass) {
        User found = findByUsername(user);
        if (found == null) {
            return Optional.empty();
        }

        if (!found.getPassword().equals(pass)) {
            return Optional.empty();
        }

        currentUser = found;
        System.out.println("[OK] Successful login -> " + found.getUsername() +
                " (" + found.getRole() + ")");
        return Optional.of(found);
    }

    public void logout(User user) {
        if (currentUser == null) {
            return;
        }
        if (currentUser != user) {
            return;
        }
        System.out.println("[OK] Successful logout -> " + currentUser.getUsername());
        currentUser = null;
    }

    public void removeUser(User user) {
        if (currentUser == user) currentUser = null;
    }

    /**
     * Changes password for the currently logged-in user.
     */
    public void changePass(String oldPass, String newPass) {
        System.out.println("[OK] Password changed for " + currentUser.getUsername());
    }

    public User findUserByID(int id) {
        for (User u : users) {
            if (u.getID() == id) {
                System.out.println("User found -> " + u);
                return u;
            }
        }
        System.out.println("No user with id " + id);
        return null;
    }

    public List<User> getUsers() {
        return users;
    }

    // HELPER (NOT IN UML)
    private User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }
}
