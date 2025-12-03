package models;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ShoppingCart implements Serializable {

    // ==========================
    // Fields
    // ==========================
// [Katramados] ChatGPT assisted, holds all games currently in the cart, Finalized Nov. 30th
    private final List<Videogame> items;

 // [Katramados] ChatGPT assisted, stores which Customer owns this cart, Finalized Nov. 30th
    private final Customer customer;

    // ==========================
    // Constructor
    // ==========================
// [Katramados], ensures the cart cannot exist without a Customer, returns initialized empty cart, Finalized Nov. 30th
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
// [Katramados], returns list of Videogame items currently in cart, Finalized Nov. 30th
    public List<Videogame> getItems() {
        return items;
    }
// [Katramados], returns owning Customer reference, Finalized Nov. 30th
    public Customer getCustomer() {
        return customer;
    }
    
    // ==========================
    // toString() 
    // ==========================
// [Katramados], builds a readable list of all games in the cart, returns formatted string, Finalized Nov. 30th
    @Override
    public String toString() {
        String toString = "";
        for (Videogame vg : items) {
            toString = toString + "\n" + vg.toString();
        }
        return toString;
    }

}
