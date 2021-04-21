package com.machine.coding.services;

import com.machine.coding.models.Ingredient;

public class IngredientService {
    public static void updateIngredientQuantity(Ingredient ingredient, int addedQuantity){
        ingredient.setQuantity(ingredient.getQuantity() + addedQuantity);
    }
}
