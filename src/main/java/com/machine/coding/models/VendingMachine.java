package com.machine.coding.models;

import java.util.List;

/**
 *
 */
public class VendingMachine {
    int numberOfOutlets;
    int timeForProcessing;
    List<Beverage> beverages;

    public VendingMachine(int numberOfOutlets, List<Beverage> beverages) {
        this.numberOfOutlets = numberOfOutlets;
        this.beverages = beverages;
    }

    public int getNumberOfOutlets() {
        return numberOfOutlets;
    }

    public void setNumberOfOutlets(int numberOfOutlets) {
        this.numberOfOutlets = numberOfOutlets;
    }

    public List<Beverage> getBeverages() {
        return beverages;
    }

    public void setBeverages(List<Beverage> beverages) {
        this.beverages = beverages;
    }
}
