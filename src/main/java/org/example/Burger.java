package org.example;

import java.util.ArrayList;
import java.util.List;

public class Burger {
    private String name;
    private List<Ingredient> ingredients;

    public Burger(String name) {
        this.name = name;
        this.ingredients = new ArrayList<>();
    }

    public String getName() { return name; }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public boolean addIngredient(Ingredient ingredient) {
        if (ingredient == null) return false;

        // 如果是必须的组件（如基础面包、主肉饼），仍然保持唯一性不可重复添加
        if (ingredient.isRequired()) {
            for (Ingredient ing : ingredients) {
                if (ing.getName().equalsIgnoreCase(ingredient.getName())) {
                    return false;
                }
            }
        }

        // 如果是非必需的加料（如加肉、加芝士、加蔬菜、酱料），允许重复添加多份
        ingredients.add(ingredient);
        return true;
    }

    public boolean removeIngredient(Ingredient ingredient) {
        if (ingredient == null) return false;
        if (ingredient.isRequired()) {
            return false;
        }
        return ingredients.remove(ingredient);
    }

    public boolean hasIngredient(String ingredientName) {
        return ingredients.stream().anyMatch(i -> i.getName().equalsIgnoreCase(ingredientName));
    }

    public double calculatePrice() {
        return ingredients.stream().mapToDouble(Ingredient::getPrice).sum();
    }

    public int calculateCalories() {
        return ingredients.stream().mapToInt(Ingredient::getCalories).sum();
    }

    public static Burger createBeefBurger() {
        Burger burger = new Burger("Beef Burger");
        burger.addIngredient(new Ingredient("Burger Bun", 1.50, 150, "Base", true));
        burger.addIngredient(new Ingredient("Beef Patty", 3.50, 250, "Patty", true));
        burger.addIngredient(new Ingredient("Lettuce", 0.50, 10, "Vegetable", false));
        burger.addIngredient(new Ingredient("Tomato", 0.50, 15, "Vegetable", false));
        burger.addIngredient(new Ingredient("Cheddar Cheese", 1.00, 110, "Cheese", false));
        return burger;
    }

    public static Burger createChickenBurger() {
        Burger burger = new Burger("Chicken Burger");
        burger.addIngredient(new Ingredient("Burger Bun", 1.50, 150, "Base", true));
        burger.addIngredient(new Ingredient("Chicken Patty", 3.20, 220, "Patty", true));
        burger.addIngredient(new Ingredient("Lettuce", 0.50, 10, "Vegetable", false));
        burger.addIngredient(new Ingredient("Mayo Sauce", 0.60, 90, "Sauce", false));
        return burger;
    }

    public static Burger createFishBurger() {
        Burger burger = new Burger("Fish Burger");
        burger.addIngredient(new Ingredient("Burger Bun", 1.50, 150, "Base", true));
        burger.addIngredient(new Ingredient("Fish Patty", 3.40, 200, "Patty", true));
        burger.addIngredient(new Ingredient("Tartar Sauce", 0.70, 100, "Sauce", false));
        burger.addIngredient(new Ingredient("Cheese", 1.00, 110, "Cheese", false));
        return burger;
    }
}