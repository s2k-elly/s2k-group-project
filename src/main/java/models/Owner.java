package models;

/**
 * Owner/Admin class.
 * Pretty much the same as User but with OWNER role.
 * Gives access to all the inventory-related permissions.
 */

public class Owner extends User {

    public Owner(String username, String password) {
        super(username, password, Role.OWNER);
    }

    @Override
    public String toString() {
        return "Owner{id=" + getID() +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
