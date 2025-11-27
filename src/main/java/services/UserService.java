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
            System.out.println("register: username and password must be non-empty.");
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
            System.out.println("LOGIN: user not found.");
            return Optional.empty();
        }

        if (!found.getPassword().equals(pass)) {
            System.out.println("LOGIN: incorrect password.");
            return Optional.empty();
        }

        currentUser = found;
        System.out.println("LOGIN: success -> " + found.getUsername() +
                " (" + found.getRole() + ")");
        return Optional.of(found);
    }

    public void logout(User user) {
        if (currentUser == null) {
            System.out.println("Nobody is logged in.");
            return;
        }
        if (currentUser != user) {
            System.out.println("Given user is not currently logged in.");
            return;
        }
        System.out.println("LOGOUT: " + currentUser.getUsername());
        currentUser = null;
    }

    public void removeUser(User user) {
        if (user == null) {
            System.out.println("User is null.");
            return;
        }
        if (user.getRole() == User.Role.OWNER) {
            System.out.println("Owner cannot be removed.");
            return;
        }
        if (users.remove(user)) {
            System.out.println("User removed: " + user.getUsername());
        } else {
            System.out.println("User not found.");
        }
        if (currentUser == user) currentUser = null;
    }

    /**
     * Changes password for the currently logged-in user.
     */
    public void changePass(String oldPass, String newPass) {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }
        if (!currentUser.getPassword().equals(oldPass)) {
            System.out.println("Old password incorrect.");
            return;
        }
        if (newPass == null || newPass.isBlank()) {
            System.out.println("New password must be non-empty.");
            return;
        }
        currentUser.setPassword(newPass);
        System.out.println("Password changed for " + currentUser.getUsername());
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
