package com.machine.coding.models;

/**
 * Ingredient class holds the name and quantity available.
 */
public class Ingredient implements Item{
    String name;
    int quantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Ingredient(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public boolean isBeverage() {
        return false;
    }
}
