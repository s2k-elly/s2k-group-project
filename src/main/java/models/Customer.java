package models;

/**
 * Customer class.
 * Extends User and adds a ShoppingCart.
 * This is the user type that can actually use the cart + checkout.
 */



public class Customer extends User {

    private final ShoppingCart cart;

    public Customer(String username, String password) {
        super(username, password, Role.CUSTOMER);
        this.cart = new ShoppingCart(this);
    }

    public ShoppingCart getCart() {
        return cart;
    }

    @Override
    public String toString() {
        return "Customer{id=" + getID() +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
