package com.machine.coding.services;

import com.machine.coding.models.Beverage;
import com.machine.coding.models.Ingredient;
import com.machine.coding.models.Item;
import com.machine.coding.models.VendingMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class VendingMachineService {
    private final VendingMachine vendingMachine;
    private static int unusedOutlets;

    VendingMachineService(int numberOfOutlets, List<Beverage> beverageList){
        vendingMachine = new VendingMachine(numberOfOutlets, beverageList);
        unusedOutlets = vendingMachine.getNumberOfOutlets();
    }

    /**
     * Receive set of beverage names to processed at an instant of time. (Other requests a second later which would be processed in the next request to the API)
     * Each request is processed asynchronously and clubbed together to get all the results and then returned.
     *
     * @param beveragesRequested
     * @return list of success or failure messages
     */
    public List<String> processRequest(List<Beverage> beveragesRequested){
        List<String> results = new ArrayList<>();
        try {
            List<CompletableFuture<String>> completableFutures = beveragesRequested.stream().map(this::prepareBeverage).collect(Collectors.toList());
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
            CompletableFuture<List<String>> allCompletableFuture = allFutures.thenApply(future -> completableFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
            results.addAll(allCompletableFuture.get());
        }catch(InterruptedException | ExecutionException ex){
            System.err.println("Exception occurred while preparing beverage");
        }
        return results;
    }

    /**
     * This method creates a asynchronous operation to execute processing of a beverage.
     *
     * @param beverage
     * @return CompletableFuture object
     */
    private CompletableFuture<String> prepareBeverage(Beverage beverage) {
        return CompletableFuture.supplyAsync(() -> {
            String vendingResponse = executeVendingSynchronously(beverage);
            return vendingResponse;
        });
    }

    /**
     * Check for outlet availability and also the ingredient availability and process the request synchronously.
     *
     * @param beverage
     * @return preparationResponse string that contains the status of the vending process.
     */
    private synchronized String executeVendingSynchronously(Beverage beverage) {
        String preparationResponse;
        List<String> dataForPreparation;
        dataForPreparation = checkIfBeverageCanBePreparedAndUpdateIngredients(beverage);
        if ( dataForPreparation == null || dataForPreparation.isEmpty()){
            if (unusedOutlets>0) {
                unusedOutlets--;
                updateQuantity(beverage);
                preparationResponse = new StringBuilder(beverage.getName()).append(" is prepared").toString();
            } else {
                preparationResponse = new StringBuilder(beverage.getName()).append(" is not prepared since outlet is not available").toString();
            }
        } else {
            preparationResponse = new StringBuilder(beverage.getName()).append(" is not prepared because ")
                    .append(String.join(", ", dataForPreparation))
                    .append(".").toString();
        }
        return preparationResponse;
    }

    /**
     * This method checks if the beverage can be prepared with the available ingredients at that point of time.
     *
     * @param beverage
     * @return ingredient that is not available.
     * @return null if every ingredient is available.
     * @return beverageName itself if the beverage cannot be made out of this vending machine.
     */
    private List<String> checkIfBeverageCanBePreparedAndUpdateIngredients(Beverage beverage){
        List<String> itemsNotSatisfyingRequirement = new ArrayList<>();
        for(Map.Entry<Item, Integer> item : beverage.getIngredients().entrySet()){
            if(!item.getKey().isBeverage()) {
                Ingredient ingredient = (Ingredient) item.getKey();
                if(ingredient.getQuantity() < item.getValue()) {
                    if (ingredient.getQuantity() == 0)
                        itemsNotSatisfyingRequirement.add(ingredient.getName() + " is not available");
                    else
                        itemsNotSatisfyingRequirement.add(ingredient.getName() + " is not sufficient");
                }
            }
            else{
                List<String> beverageNotSatisfyingList = checkIfBeverageCanBePreparedAndUpdateIngredients((Beverage) item.getKey());
                if(beverageNotSatisfyingList != null && !beverageNotSatisfyingList.isEmpty())
                    itemsNotSatisfyingRequirement.addAll(beverageNotSatisfyingList);
            }
        }

        return itemsNotSatisfyingRequirement;
    }

    /**
     * This method updates the quantity of the ingredient
     *
     * @param beverage
     */
    private void updateQuantity(Beverage beverage){
        for(Map.Entry<Item, Integer> item : beverage.getIngredients().entrySet()){
            if(!item.getKey().isBeverage()) {
                Ingredient ingredient = (Ingredient) item.getKey();
                ingredient.setQuantity(ingredient.getQuantity()-item.getValue());

            }
            else{
                updateQuantity((Beverage) item.getKey());
            }
        }
    }
}
