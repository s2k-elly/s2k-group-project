package services;

import models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for managing the list of videogames.
 * This version includes internal stub model classes and a driver main()
 * so it can be run on its own.
 */
public class GameService {

    // =======================
    // FIELDS
    // =======================
    private final List<Videogame> games = new ArrayList<>();

    // =======================
    // CONSTRUCTOR WITH TEMPLATE GAMES (FOR TESTING AND PROOF OF FUNCTION)
    // =======================

    public GameService() {
        games.add(new Videogame("Star Quest", Videogame.Genre.ACTION, "Space RPG adventure", 49.99, 10));
        games.add(new Videogame("Farm Days", Videogame.Genre.SIMULATION, "Farming game", 19.99, 5));
        games.add(new Videogame("Puzzle Master", Videogame.Genre.PUZZLE, "Puzzle challenges", 9.99, 20));
    }

    // =======================
    // SERVICE METHODS
    // =======================

    public void addGame(User user, Videogame vg) {
        games.add(vg);
        System.out.println("Game added: " + vg.getTitle());
    }

    public void removeGame(User user, Videogame vg) {
        if (games.remove(vg)) {
            System.out.println("Game removed: " + vg.getTitle());
        } else {
            System.out.println("Game not found in list.");
        }
    }

    public void updatePrice(User user, int gameID, double newPrice) {
        Videogame game = findByID(gameID);
        if (game == null) {
            System.out.println("Game not found (ID = " + gameID + ")");
            return;
        }
        game.setPrice(newPrice);
        System.out.println("Price updated for '" + game.getTitle() + "' -> " + newPrice);
    }

    public void updateStock(User user, int gameID, int newStock) {
        if (isOwner(user)) {
            System.out.println("Only owners can update stock.");
            return;
        }
        Videogame game = findByID(gameID);
        if (game == null) {
            System.out.println("Game not found (ID = " + gameID + ")");
            return;
        }
        game.setStock(newStock);
        System.out.println("Stock updated for '" + game.getTitle() + "' -> " + newStock);
    }

    // These "find/search" are void in the UML, so here they just print results.

    public Videogame findByID(int vgID) {
        for (Videogame vg : games) {
            if (vg.getID() == vgID) return vg;
        }
        return null;
    }

    public List<Videogame> findByTitle(String vgTitle) {
        List<Videogame> result = new ArrayList<>();
        for (Videogame vg : games) {
            if (vg.getTitle().equalsIgnoreCase(vgTitle)) {
                result.add(vg);
            }
        }
        return result;
    }

    public void searchByGenre(Videogame.Genre genre) {
        System.out.println("searchByGenre: " + genre);
        boolean found = false;
        for (Videogame vg : games) {
            if (vg.getGenre() == genre) {
                System.out.println("  -> " + vg);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No games found in this genre.");
        }
    }

    public void showDetail(Videogame vg) {
        System.out.println("=== Game Details ===");
        System.out.println(vg);
        System.out.println("===================");
    }

    private boolean isOwner(User user) {
        return user == null || user.getRole() != User.Role.OWNER;
    }

    public List<Videogame> listAll() { // ADDED POST-UML
        return new ArrayList<>(games);
    }

    public Optional<Videogame> optionalID(int vgID) { // ADDED POST-UML
        for (Videogame vg : games) {
            if (vg.getID() == vgID) return Optional.of(vg);
        }
        return Optional.empty();
    }

}

