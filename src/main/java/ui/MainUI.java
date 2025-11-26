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
        System.out.println("=== Welcome to Videogame Store ===");

        boolean exit = false;
        while (!exit) {
            showMainMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> showGamesMenu();
                case 2 -> register();
                case 3 -> login();
                case 4 -> logout();
                case 5 -> showCartMenu();
                case 6 -> {
                    if (currentUser instanceof Owner owner) {
                        ownerMenu();
                    }
                    else {
                        System.out.println("❌ Invalid option. Try again.");
                    }
                }
                case 0 -> exit = true;
                default -> System.out.println("❌ Invalid option. Try again.");
            }
        }

        System.out.println("Goodbye!");
    }

    private void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1) Browse games");
        System.out.println("2) Register (become a customer)");
        System.out.println("3) Login");
        System.out.println("4) Logout");
        System.out.println("5) Cart & Checkout");
        if (currentUser instanceof Owner) {
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
    }

    // ========================
    // USER REGISTRATION
    // ========================
    private void register() {
        System.out.println("\n--- REGISTER NEW CUSTOMER ---");
        String u = readString("Username: ");
        String p = readString("Password: ");

        User newUser = userService.register(u, p);
        System.out.println("Successfully registered as: " + newUser.getUsername());
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
            System.out.println("❌ Incorrect username or password.");
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
            System.out.println("❌ You must be logged in as a CUSTOMER to access a cart.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n--- CART MENU ---");
            System.out.println("1) View cart");
            System.out.println("2) Add item");
            System.out.println("3) Remove item");
            System.out.println("4) Checkout");
            System.out.println("0) Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> {
                    System.out.println(c.getCart());
                    System.out.printf("Total: %.2f%n", cartService.total(c));
                }
                case 2 -> {
                    int id = readInt("Enter game ID to add: ");
                    gameService.optionalID(id).ifPresentOrElse(
                            g -> {
                                cartService.addCart(c, g);
                                System.out.println("✔ Added to cart: " + g.getTitle());
                            },
                            () -> System.out.println("❌ Game not found.")
                    );
                }
                case 3 -> {
                    int id = readInt("Enter game ID to remove: ");
                    gameService.optionalID(id).ifPresentOrElse(
                            g -> {
                                cartService.removeCart(c, g);
                                System.out.println("✔ Removed from cart: " + g.getTitle());
                            },
                            () -> System.out.println("❌ Game not found.")
                    );
                }
                case 4 -> {
                    try {
                        cartService.checkout(c);
                    } catch (StoreExceptions.OutOfStockException e) {
                        System.out.println("❌ Checkout failed: game is out of stock!");
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
            System.out.println("❌ Owner privileges required.");
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
                System.out.println("❌ Error: " + e.getMessage());
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
                    throw new NullPointerException("❌ Title cannot be empty.");
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
                System.out.println("❌ Genre does not exist in our list. Please enter another genre.");
            }
        }
        while (true) {
            try {
                desc = readString("Description: ");
                if (desc.trim().isEmpty()) {
                    throw new NullPointerException("❌ Description cannot be empty.");
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

        System.out.println("✔ Added game: " + game.getTitle());
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
                System.out.println("❌ Invalid number. Try again.");
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
                System.out.println("❌ Invalid decimal number. Try again.");
            }
        }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}