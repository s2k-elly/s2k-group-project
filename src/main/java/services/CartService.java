package services;

import models.*;
import java.util.List;

/**
 * Service responsible for operations on a customer's ShoppingCart.
 * Includes internal stub model classes and a driver main().
 */

public class CartService {

    // [Skaraki] ChatGPT assisted. Accepts customer and videogame objects, adds videogame to respective customer's cart. Finalized Nov 30th.
    public void addCart(Customer customer, Videogame vg) {
        customer.getCart().getItems().add(vg);
        System.out.println("Added '" + vg.getTitle() + "' to " + customer.getUsername() + "'s cart.");
    }

    // [Skaraki] ChatGPT assisted. Accepts customer and videogame objects, removes videogame from respective customer's cart. Finalized Nov 30th.
    public void removeCart(Customer customer, Videogame vg) {
        if (vg == null) {
            System.out.println("[X] Game does not exist in cart.");
            return;
        }
        boolean removed = customer.getCart().getItems().remove(vg);
    }

    // [Skaraki] ChatGPT assisted. Accepts customer object, returns total price of videogames in customer's cart. Finalized Nov 30th.
    public double total(Customer customer) {
        double sum = 0.0;
        for (Videogame vg : customer.getCart().getItems()) {
            sum += vg.getPrice();
        }
        return sum;
    }

    // [Skaraki] ChatGPT assisted. Accepts customer object, prints out checkout and clears customer's cart after checkout. Finalized Nov 30th.
    public void checkout(Customer customer) {
        List<Videogame> items = customer.getCart().getItems();
        if (items.isEmpty()) {
            System.out.println("[X] Cart is empty.");
            return;
        }

        System.out.println("=== Checkout for " + customer.getUsername() + " ===");
        for (Videogame vg : items) {
            System.out.println("- " + vg.getTitle() + " ($" + vg.getPrice() + ")");
        }
        total(customer);
        System.out.println("[OK] Payment successful. Thank you!");

        items.clear(); // empty cart after checkout
    }
}
