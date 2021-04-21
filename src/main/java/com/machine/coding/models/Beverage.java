package com.machine.coding.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Beverage class holds the state of name and ingredients (with the exact quantity) required to prepare the beverage.
 */
public class Beverage implements Item{
    String name;
    Map<Item, Integer> ingredients;

    public Beverage(String name) {
        this.name = name;
        this.ingredients = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Item, Integer> getIngredients() {
        return ingredients;
    }

    public void updateIngredients(Map<Item, Integer> ingredients) {
        this.ingredients.putAll(ingredients);
    }

    @Override
    public boolean isBeverage() {
        return true;
    }
}
