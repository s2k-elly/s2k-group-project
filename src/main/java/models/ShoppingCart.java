package models;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ShoppingCart implements Serializable {

    // ==========================
    // Fields
    // ==========================

    // The list of games currently in the cart
    private final List<Videogame> items;

    // The customer who owns this cart
    private final Customer customer;

    // ==========================
    // Constructor
    // ==========================

    public ShoppingCart(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("ShoppingCart must belong to a Customer.");
        }
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    // ==========================
    // Getters
    // ==========================

    public List<Videogame> getItems() {
        return items;
    }

    public Customer getCustomer() {
        return customer;
    }
    
    // ==========================
    // toString() 
    // ==========================
    
    @Override
    public String toString() {
        String toString = "";
        for (Videogame vg : items) {
            toString = toString + "\n" + vg.toString();
        }
        return toString;
    }

}
