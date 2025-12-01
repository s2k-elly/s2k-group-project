package ui;

import models.*;
import services.*;
import exceptions.*;

import java.util.Optional;
import java.util.Scanner;

/**
 * Robust console UI for the Videogame Store.
 * Includes strong input validation and crash prevention.
 */
public class MainUI {

    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();
    private final CartService cartService = new CartService();
    private User currentUser = null;

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new MainUI().start();
    }

    /** Main loop */
    private void start() {
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
                    }
                    else {
                        System.out.println("[X] Invalid option. Try again.");
                    }
                }
                case 5 -> showCartMenu();
                case 6 -> {
                    if (currentUser instanceof Owner owner) {
                        ownerMenu();
                    }
                    else {
                        System.out.println("[X] Invalid option. Try again.");
                    }
                }
                case 0 -> exit = true;
                default -> System.out.println("[X] Invalid option. Try again.");
            }
        }

        System.out.println("Goodbye!");
    }

    private void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1) Browse games");
        if (currentUser == null) {
            System.out.println("2) Register (become a customer)"); // only visible to guests
            System.out.println("3) Login"); // only visible to guests
        }
        if (currentUser != null) {
            System.out.println("4) Logout"); // only visible to users who are logged in
        }
        System.out.println("5) Cart & Checkout"); // only visible to customers
        if (currentUser instanceof Owner o) {
            System.out.println("6) Admin options");   // only visible to Owners
        }
        System.out.println("0) Exit");
        System.out.println("Current user: " + (currentUser == null ? "guest" : currentUser.getUsername() + " (" + currentUser.getRole() + ")"));
    }

    // ========================
    // GAME LISTING (ACCIDENTALLY LISTED TWICE IN THE UML,
    // FUNCTIONALLY THE SAME AS WHAT "listGames()" WAS MEANT TO DO)
    // ========================
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

    // ========================
    // INDIVIDUAL GAME MENU
    // ========================

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

    // ========================
    // USER REGISTRATION
    // ========================
    private void register() {
        System.out.println("\n--- REGISTER NEW CUSTOMER ---");
        while (true) {
            try {
                String u = readString("Username: ");
                String p = readString("Password: ");
                if (p.isBlank() || u.isBlank()) {
                    throw new StoreExceptions.InvalidInputException("[X] Username and/or Password cannot be empty.");
                }
                for (User user : userService.getUsers() ) {
                    if (user.getUsername().equals(u)) {
                        throw new StoreExceptions.UserException("[X] Username already in use.");
                    }
                }
                User newUser = userService.register(u, p);
                System.out.println("[OK] Successfully registered as: " + newUser.getUsername());
                break;
            } catch (StoreExceptions.InvalidInputException | StoreExceptions.UserException e) { System.out.println(e.getMessage()); }
        }
    }

    // ========================
    // LOGIN / LOGOUT
    // ========================
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

    private void logout() {
        if (currentUser != null) {
            System.out.println("Logged out: " + currentUser.getUsername());
            currentUser = null;
        } else {
            System.out.println("You are not logged in.");
        }
    }

    // ========================
    // CART MENU
    // ========================
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

    // ========================
    // OWNER MENU (METHOD WRAPPER NOT IN THE UML, ADDED FOR MODULARITY)
    // ========================
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

    private void defineGameRemove(Owner owner) { // FORGOTTEN TO BE IMPLEMENTED INTO ORIGINAL UML
        int id = readInt("Game ID to remove: ");
        gameService.removeGame(owner, gameService.findByID(id));
    }

    private void updatePrice(Owner owner) {
        int id = readInt("Game ID: ");
        double p = readDouble("New price: ");
        gameService.updatePrice(owner, id, p);
    }

    private void updateStock(Owner owner) {
        int id = readInt("Game ID: ");
        int stock = readInt("New stock: ");
        gameService.updateStock(owner, id, stock);
    }

    // ========================
    // INPUT VALIDATION HELPERS
    // ========================
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

    private double readDouble(String prompt) { // FORGOTTEN TO BE IMPLEMENTED INTO ORIGINAL UML
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

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}