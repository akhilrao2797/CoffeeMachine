package com.machine.coding.services;

import com.machine.coding.models.Beverage;
import com.machine.coding.models.Ingredient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VendingMachineServiceTest {
    VendingMachineService vendingMachineService;
    IngredientService ingredientService;
    Ingredient teaLeavesSyrup, gingerSyrup, sugarSyrup, elaichiSyrup, milk, water, coffeeSyrup;
    Beverage hotWater, hotMilk, gingerTea, elaichiTea, coffee;

    @Before
    public void setup(){
        List<Beverage> beverageList = new ArrayList<>();

        teaLeavesSyrup = new Ingredient("tea leaves syrup", 100);
        gingerSyrup = new Ingredient("ginger syrup", 100);
        sugarSyrup = new Ingredient("sugar syrup", 100);
        elaichiSyrup = new Ingredient("elaichi syrup", 100);
        coffeeSyrup = new Ingredient("coffee syrup", 30);
        milk = new Ingredient("milk", 100);
        water = new Ingredient("water", 100);

        hotWater = new Beverage("hot water");
        hotWater.updateIngredients(Collections.singletonMap(water, 50));
        beverageList.add(hotWater);
        hotMilk = new Beverage("hot milk");
        hotMilk.updateIngredients(Collections.singletonMap(milk, 50));
        beverageList.add(hotMilk);

        gingerTea = new Beverage("ginger tea");
        gingerTea.updateIngredients(Collections.singletonMap(hotWater, 50));
        gingerTea.updateIngredients(Collections.singletonMap(hotMilk, 10));
        gingerTea.updateIngredients(Collections.singletonMap(teaLeavesSyrup, 10));
        gingerTea.updateIngredients(Collections.singletonMap(gingerSyrup, 5));
        gingerTea.updateIngredients(Collections.singletonMap(sugarSyrup, 10));
        beverageList.add(gingerTea);

        elaichiTea = new Beverage("elaichi tea");
        elaichiTea.updateIngredients(Collections.singletonMap(hotWater, 50));
        elaichiTea.updateIngredients(Collections.singletonMap(hotMilk, 10));
        elaichiTea.updateIngredients(Collections.singletonMap(teaLeavesSyrup, 10));
        elaichiTea.updateIngredients(Collections.singletonMap(elaichiSyrup, 5));
        elaichiTea.updateIngredients(Collections.singletonMap(sugarSyrup, 10));
        beverageList.add(elaichiTea);

        coffee = new Beverage("coffee");
        coffee.updateIngredients(Collections.singletonMap(hotWater, 50));
        coffee.updateIngredients(Collections.singletonMap(hotMilk, 10));
        coffee.updateIngredients(Collections.singletonMap(coffeeSyrup, 10));
        coffee.updateIngredients(Collections.singletonMap(sugarSyrup, 10));
        beverageList.add(coffee);

        vendingMachineService = Mockito.spy(new VendingMachineService(3, beverageList));
    }

    /**
     * Check whether the positive flow without any error the beverages are prepared at that instant of time.
     */
    @Test
    public void testMachineWithPositiveFlow(){
        List<Beverage> requestBeverages = new ArrayList<>();
        requestBeverages.addAll(Arrays.asList(hotMilk, hotWater));
        List<String> responses = vendingMachineService.processRequest(requestBeverages);
        int times = 0;
        System.out.println("Testcase 1: testMachineWithPositiveFlow");
        for(String s: responses) {
            System.out.println(s);
            if(s.contains("is prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(2, times);
    }

    /**
     * Checks whether the beverages are prepared if the outlets are available and not prepared when outlets are not available.
     */
    @Test
    public void testMachineWithRequestedBeveragesMoreThanOutlets(){
        List<Beverage> requestBeverages = new ArrayList<>();
        requestBeverages.addAll(Arrays.asList(hotMilk, coffee, elaichiTea, hotMilk, coffee, elaichiTea));
        List<String> responses = vendingMachineService.processRequest(requestBeverages);
        int times = 0;
        System.out.println("Testcase 2: testMachineWithRequestedBeveragesMoreThanOutlets");
        for(String s: responses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(4, times);

    }

    /**
     * Check whether the beverages are prepared only when the required quantity is available.
     */
    @Test
    public void testMachineWithRequestedBeveragesMoreThanAvailableQuantity(){
        List<Beverage> requestBeverages = new ArrayList<>();
        requestBeverages.addAll(Arrays.asList(elaichiTea, elaichiTea, elaichiTea, elaichiTea));
        List<String> responses = vendingMachineService.processRequest(requestBeverages);
        int times = 0;
        System.out.println("Testcase 3: testMachineWithRequestedBeveragesMoreThanAvailableQuantity");
        for(String s: responses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(2, times);

    }

    /**
     * Check whether the beverages are prepared only when basic required quantity is available.
     * Here milk is not available in the required quantities to satisfy all the requests.
     */
    @Test
    public void testMachineWithRequestedBeveragesWhereMilkIsLessAvailableQuantity(){
        List<Beverage> requestBeverages = new ArrayList<>();
        requestBeverages.addAll(Arrays.asList(hotMilk, hotMilk, hotMilk, coffee, elaichiTea, gingerTea));
        List<String> responses = vendingMachineService.processRequest(requestBeverages);
        int times = 0;
        System.out.println("Testcase 4: testMachineWithRequestedBeveragesWhereMilkIsLessAvailableQuantity");
        for(String s: responses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(4, times);
    }

    /**
     * Check whether the beverages are prepared or not on the next consecutive request since the first request would have consumed some quantity from the machine.
     */
    @Test
    public void testMachineWithConsecutiveRequestedBeverages(){
        List<Beverage> requestBeverages = new ArrayList<>();
        requestBeverages.addAll(Arrays.asList(hotMilk, elaichiTea));
        List<String> firstResponses = vendingMachineService.processRequest(requestBeverages);
        List<String> secondResponses = vendingMachineService.processRequest(requestBeverages);
        int times = 0;
        System.out.println("Testcase 5: testMachineWithConsecutiveRequestedBeverages");
        for(String s: firstResponses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        for(String s: secondResponses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(2, times);
    }

    /**
     * Check whether the beverages are prepared or not on the next consecutive request since the first request would have consumed some quantity from the machine.
     * And also checks that if the consecutive request is satisfying the ingredient quantity and also the outlet is available, then the request would be served
     */
    @Test
    public void testMachineWithConsecutiveRequestedBeveragesAndLessOutletsAvailable(){
        List<String> firstResponses = vendingMachineService.processRequest(Arrays.asList(hotMilk, hotMilk, elaichiTea));
        List<String> secondResponses = vendingMachineService.processRequest(Arrays.asList(hotWater));
        int times = 0;
        System.out.println("Testcase 6: testMachineWithConsecutiveRequestedBeveragesAndLessOutletsAvailable");
        for(String s: firstResponses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        for(String s: secondResponses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(1, times);
    }

    /**
     * Check a scenario where some initial beverages are not prepared even if the outlet is available because the required quantity of ingredient is not available.
     * Hence the other requests down the order satisfying the availability of ingredients and outlets would be served.
     */
    @Test
    public void testMachineWithRequestedBeveragesLessAvailableQuantityAndMoreOutletsAvailableToServeOthers(){
        List<Beverage> requestBeverages = new ArrayList<>();
        IngredientService.updateIngredientQuantity(water, 300);
        IngredientService.updateIngredientQuantity(milk, 300);
        requestBeverages.addAll(Arrays.asList(coffee, coffee, coffee, coffee, elaichiTea, elaichiTea));
        List<String> responses = vendingMachineService.processRequest(requestBeverages);
        int times = 0;
        System.out.println("Testcase 7: testMachineWithRequestedBeveragesLessAvailableQuantityAndMoreOutletsAvailableToServeOthers");
        for(String s: responses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(3, times);
    }

    /**
     * Check a scenario where some ingredients are available but not sufficient to make up the order.
     */
    @Test
    public void testMachineWithRequestedBeveragesInSufficientQuantity(){
        List<Beverage> requestBeverages = new ArrayList<>();
        IngredientService.updateIngredientQuantity(water, 80);
        IngredientService.updateIngredientQuantity(milk, 330);
        requestBeverages.addAll(Arrays.asList(coffee, coffee, hotWater, hotWater, elaichiTea, elaichiTea));
        List<String> responses = vendingMachineService.processRequest(requestBeverages);
        int times = 0;
        System.out.println("Testcase 8: testMachineWithRequestedBeveragesInSufficientQuantity");
        for(String s: responses) {
            System.out.println(s);
            if(s.contains("is not prepared"))
                times++;
        }
        System.out.println();
        Assert.assertEquals(3, times);
    }
}