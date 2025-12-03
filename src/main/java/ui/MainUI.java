/**
 * Robust console UI for the Videogame Store.
 * Includes strong input validation and crash prevention.
 * Chat GPT assisted Kaloudis last edited 2/12 **/


package ui;

import models.*;
import services.*;
import exceptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class MainUI {

    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();
    private final CartService cartService = new CartService();
    private User currentUser = null;

    private final Scanner scanner = new Scanner(System.in);
    private static final String USER_FILE = "users.txt";

    /**
     * Program entry point 
     * Chat GPT assisted Kaloudis last edited 2/12.
     */
     
     
    public static void main(String[] args) {
        new MainUI().start();
    }

    /**
     * Starts the UI. Loads persisted users at startup and saves users on exit 
     * Chat GPT assisted Kaloudis last edited 2/12.
     */
     
     
    private void start() {
        loadUsers();
        System.out.println("=== Welcome to S2K's Videogame Store ===");

        boolean exit = false;
        while (!exit) {
            showMainMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> showGamesMenu();
                case 2 -> {
                    if (currentUser == null) {
                        register();
                    } else {
                        System.out.println("[X] Invalid option. Try again.");
                    }
                }
                case 3 -> {
                    if (currentUser == null) {
                        login();
                    } else {
                        System.out.println("[X] Invalid option. Try again.");
                    }
                }
                case 4 -> {
                    if (currentUser != null) {
                        logout();
                    } else {
                        System.out.println("[X] Invalid option. Try again.");
                    }
                }
                case 5 -> showCartMenu();
                case 6 -> {
                    if (currentUser instanceof Owner) {
                        ownerMenu();
                    } else {
                        System.out.println("[X] Invalid option. Try again.");
                    }
                }
                case 0 -> exit = true;
                default -> System.out.println("[X] Invalid option. Try again.");
            }
        }

        /** Save users on exit to persist latest state 
        Chat GPT assisted Kaloudis last edited 2/12. */
        
        saveUsers();
        System.out.println("Goodbye!");
    }

    /**
     * Displays the main menu. The menu shows/hides options depending on login state and role 
     * Kaloudis last edited 2/12.
     */
     
     
    private void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1) Browse games");
        if (currentUser == null) {
            System.out.println("2) Register (become a customer)");
            System.out.println("3) Login");
        }
        if (currentUser != null) {
            System.out.println("4) Logout");
        }
        System.out.println("5) Cart & Checkout");
        if (currentUser instanceof Owner) {
            System.out.println("6) Admin options");
        }
        System.out.println("0) Exit");
        System.out.println("Current user: " + (currentUser == null ? "guest" : currentUser.getUsername() + " (" + currentUser.getRole() + ")"));
    }

    /**
     * Shows available games, supports simple search and selecting one to view details. 
     * Chat GPT assisted Kaloudis last edited 2/12
     */
     
     
    private void showGamesMenu() {
        System.out.println("\n--- AVAILABLE GAMES ---");
        gameService.listAll().forEach(System.out::println);

        String q = readString("\nSearch by title (leave empty to skip): ");
        if (!q.isBlank()) {
            System.out.println("--- SEARCH RESULTS ---");
            gameService.findByTitle(q).forEach(System.out::println);
        }

        System.out.print("\nTo view a specific game, provide the ID (leave empty to skip): ");
        showGameInfo();
    }

    /**
     * Reads a game ID from input and shows game detail and per-game options.
     * Allows adding to cart (only if logged in as Customer).
     * Chat GPT assisted Kaloudis last edited 2/12
     */
     
     
    private void showGameInfo() {
        while (true) {
            String q = scanner.nextLine().trim();

            if (q.isBlank()) {
                return;
            }

            int id;
            try {
                id = Integer.parseInt(q);
            } catch (NumberFormatException e) {
                System.out.print("[X] Invalid ID. Try again: ");
                continue;
            }

            Videogame vg = gameService.findByID(id);
            if (vg == null) {
                System.out.print("[X] Game not found. Try again: ");
                continue;
            }
            gameService.showDetail(vg);

            while (true) {
                System.out.println("\n--- GAME OPTIONS ---");
                System.out.println("1) Add to cart");
                System.out.println("0) Back");

                System.out.print("Choose: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> {
                        if (currentUser instanceof Customer c) {
                            cartService.addCart(c, vg);
                            System.out.println("[OK] Added to cart.");
                        } else {
                            System.out.println("[X] Only customers can use a cart.");
                        }
                    }
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("[X] Invalid choice.");
                }
            }
        }
    }

    /**
     * Register a new customer account.
     * On success, automatically persists users to file.
     * Chat GPT assisted Kaloudis last edited 2/12
     */
     
     
    private void register() {
        System.out.println("\n--- REGISTER NEW CUSTOMER ---");
        while (true) {
            try {
                String u = readString("Username: ");
                String p = readString("Password: ");
                if (p.isBlank() || u.isBlank()) {
                    throw new StoreExceptions.InvalidInputException("[X] Username and/or Password cannot be empty.");
                }
                for (User user : userService.getUsers()) {
                    if (user.getUsername().equals(u)) {
                        throw new StoreExceptions.UserException("[X] Username already in use.");
                    }
                }
                User newUser = userService.register(u, p);
                /** Persist immediately after registration
                Chat GPT assisted Kaloudis last edited 2/12 **/
                
                
                saveUsers();
                System.out.println("[OK] Successfully registered as: " + newUser.getUsername());
                break;
            } catch (StoreExceptions.InvalidInputException | StoreExceptions.UserException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Login using username/password. If successful, currentUser is set.
     * Kaloudis last edited 2/12
     */
    private void login() {
        System.out.println("\n--- LOGIN ---");
        String u = readString("Username: ");
        String p = readString("Password: ");
        Optional<User> opt = userService.login(u, p);
        if (opt.isPresent()) {
            currentUser = opt.get();
            System.out.println("Welcome, " + currentUser.getUsername() + "!");
        } else {
            System.out.println("[X] Incorrect username or password.");
        }
    }

    /**
     * Logs out the current user if any.
     * Kaloudis last edited 2/12
     */
     
     
    private void logout() {
        if (currentUser != null) {
            System.out.println("Logged out: " + currentUser.getUsername());
            currentUser = null;
        } else {
            System.out.println("You are not logged in.");
        }
    }

    /**
     * Displays cart menu and handles cart operations: view, remove, checkout.
     * Chat GPT assisted Kaloudis last edited 2/12
     */
    private void showCartMenu() {
        if (!(currentUser instanceof Customer c)) {
            System.out.println("[X] You must be logged in as a CUSTOMER to access a cart.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n--- CART MENU ---");
            System.out.println("1) View cart");
            System.out.println("2) Remove item");
            System.out.println("3) Checkout");
            System.out.println("0) Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> {
                    System.out.println(c.getCart());
                    System.out.printf("Total: %.2f%n", cartService.total(c));
                }
                case 2 -> {
                    int id = readInt("Enter game ID to remove: ");
                    gameService.optionalID(id).ifPresentOrElse(
                            g -> {
                                cartService.removeCart(c, g);
                                System.out.println("[OK] Removed from cart: " + g.getTitle());
                            },
                            () -> System.out.println("[X] Game not found.")
                    );
                }
                case 3 -> {
                    if (c.getCart().getItems().isEmpty()) {
                        System.out.println("Cart is empty.");
                        break;
                    }

                    System.out.println("\n=== CHECKOUT ===");
                    System.out.println("This is a simulation. Do NOT enter real payment details.");

                    // ===== NAME =====
                    String name;
                    while (true) {
                        name = readString("Cardholder name: ");
                        if (!name.isBlank()) {
                            break;
                        }
                        System.out.println("[X] Name cannot be empty.");
                    }

                    // ===== CARD NUMBER =====
                    String card;
                    while (true) {
                        card = readString("Card number (15-19 digits): ");

                        // check digits only
                        if (!card.matches("\\d+")) {
                            System.out.println("[X] Card number must contain digits only.");
                            continue;
                        }

                        // check length
                        if (card.length() < 15 || card.length() > 19) {
                            System.out.println("[X] Card number must be between 15-19 digits.");
                            continue;
                        }

                        break;
                    }

                    // ===== CONFIRMATION =====
                    System.out.println("\nProcessing payment...");
                    try {
                        cartService.checkout(c);
                    } catch (StoreExceptions.OutOfStockException e) {
                        System.out.println("[X] Checkout failed: game is out of stock!");
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Owner-only menu for administrative tasks (add/remove/update games).
     * Chat GPT assisted Kaloudis last edited 3/12
     */
    private void ownerMenu() {
        if (!(currentUser instanceof Owner owner)) {
            System.out.println("[X] Owner privileges required.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n--- OWNER MENU ---");
            System.out.println("1) Add game");
            System.out.println("2) Remove game");
            System.out.println("3) Update price");
            System.out.println("4) Update stock");
            System.out.println("0) Back");

            int choice = readInt("Choose: ");

            try {
                switch (choice) {
                    case 1 -> defineGameAdd(owner);
                    case 2 -> defineGameRemove(owner);
                    case 3 -> updatePrice(owner);
                    case 4 -> updateStock(owner);
                    case 0 -> back = true;
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("[X] Error: " + e.getMessage());
            }
        }
    }

    /**
     * Prompts owner for game details and adds a new videogame to the store.
     *
     * @param owner the owner performing the action
     * Chat GPT assisted Kaloudis last edited 3/12
     */
    private void defineGameAdd(Owner owner) {
        System.out.println("\n--- Add New Game ---");
        String desc = null;
        String title = null;
        while (true) {
            try {
                title = readString("Title: ");
                if (title.trim().isEmpty()) {
                    throw new NullPointerException("[X] Title cannot be empty.");
                }
                break;
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
        Videogame.Genre genre = null;
        while (true) {
            try {
                genre = Videogame.Genre.valueOf(readString("Genre: ").toUpperCase().replace(" ", "_"));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("[X] Genre does not exist in our list. Please enter another genre.");
            }
        }
        while (true) {
            try {
                desc = readString("Description: ");
                if (desc.trim().isEmpty()) {
                    throw new NullPointerException("[X] Description cannot be empty.");
                }
                break;
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
        double price = readDouble("Price: ");
        int stock = readInt("Initial stock: ");

        Videogame game = new Videogame(title, genre, desc, price, stock);
        gameService.addGame(owner, game);

        System.out.println("[OK] Added game: " + game.getTitle());
    }

    /**
     * Removes a videogame by ID.
     *
     * Chat GPT assisted Kaloudis last edited 3/12
     */
     
     
    private void defineGameRemove(Owner owner) {
        int id = readInt("Game ID to remove: ");
        Videogame toRemove = gameService.findByID(id);
        if (toRemove == null) {
            System.out.println("[X] Game not found.");
            return;
        }
        gameService.removeGame(owner, toRemove);
        System.out.println("[OK] Removed game: " + toRemove.getTitle());
    }

    /**
     * Updates the price of a videogame by ID.
     *
     * Chat GPT assisted Kaloudis last edited 3/12
     */
     
     
    private void updatePrice(Owner owner) {
        int id = readInt("Game ID: ");
        double p = readDouble("New price: ");
        gameService.updatePrice(owner, id, p);
        System.out.println("[OK] Price updated.");
    }

    /**
     * Updates the stock of a videogame by ID.
     *
     * Chat GPT assisted Kaloudis last edited 3/12
     */
    private void updateStock(Owner owner) {
        int id = readInt("Game ID: ");
        int stock = readInt("New stock: ");
        gameService.updateStock(owner, id, stock);
        System.out.println("[OK] Stock updated.");
    }

    // ========================
    // INPUT VALIDATION HELPERS
    // ========================

    /**
     * Reads an integer from stdin with prompt and basic validation.
     *
     * Chat GPT assisted Kaloudis last edited 3/12
     * @return parsed integer
     */
    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = scanner.nextLine().trim();
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("[X] Invalid number. Try again.");
            }
        }
    }

    /**
     * Reads a double from stdin with prompt and basic validation.
     *
     * @param prompt prompt message
     * @return parsed double
     * Chat GPT assisted Kaloudis last edited 3/12
     */
     
     
    private double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = scanner.nextLine().trim();
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("[X] Invalid decimal number. Try again.");
            }
        }
    }

    /**
     * Reads a trimmed line from stdin after printing a prompt.
     *
     * @param prompt prompt message
     * @return trimmed input string
     * Chat GPT assisted Kaloudis last edited 3/12
     */
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // ========================
    // USER PERSISTENCE (SAVE / LOAD)
    // ========================

    /**
     * Saves all users returned by userService.getUsers() to the USER_FILE.
     * Format: username;password;role
     *
     * This method overwrites the file completely on each save.
     * 
     * 
     * 
     * Chat GPT assisted Kaloudis last edited 3/12
     */
    private void saveUsers() {
        List<User> users = userService.getUsers();
        Path p = Paths.get(USER_FILE);

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (User u : users) {
                //storing plaintext passwords. For production, hash them instead.
                String line = String.join(";", u.getUsername(), u.getPassword(), u.getRole());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("[X] Unable to save users: " + e.getMessage());
        }
    }

    /**
     * Loads users from USER_FILE and registers customers that do not already exist.
     *
     * Chat GPT assisted Kaloudis last edited 3/12
     */
    private void loadUsers() {
        Path p = Paths.get(USER_FILE);
        if (!Files.exists(p)) {
            // No users file yet -> nothing to load (this is normal for first run) Chat GPT assisted Kaloudis last edited 2/12
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(p)) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 3) {
                    System.out.println("[X] Skipping malformed line " + lineNo + " in users file.");
                    continue;
                }
                String username = parts[0].trim();
                String password = parts[1].trim();
                String role = parts[2].trim();

                boolean exists = userService.getUsers().stream().anyMatch(u -> u.getUsername().equals(username));
                if (exists) {
                    // Skip existing users
                    continue;
                }

                if ("CUSTOMER".equalsIgnoreCase(role)) {
                    try {
                        userService.register(username, password);
                    } catch (Exception e) {
                        // If registration fails for some reason, print and continue.
                        System.out.println("[X] Failed to register loaded user '" + username + "': " + e.getMessage());
                    }
                } else if ("OWNER".equalsIgnoreCase(role)) {
                    // Owners are skipped by default. If you'd like to auto-create owners, implement it here.
                    System.out.println("[!] Skipping owner account in users file: " + username + " (manual creation required).");
                } else {
                    // Unknown role: attempt to register as customer to at least create an account
                    try {
                        userService.register(username, password);
                        System.out.println("[!] Loaded user '" + username + "' with unknown role '" + role + "' as CUSTOMER.");
                    } catch (Exception e) {
                        System.out.println("[X] Failed to register loaded user '" + username + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[X] Unable to load users: " + e.getMessage());
        }
    }
}
