package models;

/*
  Videogame model class.
  This holds all info the store needs (title, genre, price, stock).
  ID system follows the same logic as users auto increment and simple.
 */

import java.io.Serializable;

public class Videogame implements Serializable {
// [Katramados] ChatGPT assisted, auto-increment game ID, Finalized Nov. 30th
    private static int counter = 1;

    private final int id;
    private String title;
    private Genre genre;
    private String description;
    private double price;
    private int stock;

    public enum Genre {
        ACTION,
        SIMULATION,
        RPG,
        STRATEGY,
        PUZZLE,
        SPORTS,
        MMO,
        SANDBOX
    }
// [Katramados] Constructor sets all fields (no validation here), Finalized Nov. 30th
    public Videogame(String title, Genre genre, String description, double price, int stock) {
        this.id = counter++;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }
// [Katramados] Returns unique game ID, Finalized Nov. 30th
    public int getID() { return id; }
// [Katramados] Basic getters/setters for fields, Finalized Nov. 30th
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
// [Katramados] String output for catalog listings, Finalized Nov. 30th
    @Override
    public String toString() {
        return "'" + title + "' (ID: " + id + "). Price: " + price + " || In Stock: " + stock;
    }
}
