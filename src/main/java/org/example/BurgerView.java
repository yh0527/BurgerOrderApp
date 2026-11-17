package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class BurgerView {
    private BorderPane root;
    private Burger currentBurger;
    private Spinner<Integer> quantitySpinner;
    private Spinner<Integer> addOnQuantitySpinner;
    private ListView<Ingredient> currentIngredientsListView;
    private ListView<Ingredient> availableIngredientsListView;
    private Label lblTotalPrice, lblTotalCalories, lblFeedback;
    private TextArea txtOrderSummary;

    // 【只保留三种肉类及基础的蔬菜、芝士和酱料】
    private final ObservableList<Ingredient> availableMasterList = FXCollections.observableArrayList(
            // --- 加餐肉类 (仅保留牛肉、鸡肉、鱼肉) ---
            new Ingredient("Extra Beef Patty", 3.50, 250, "Meat", false),
            new Ingredient("Extra Chicken Patty", 3.20, 220, "Meat", false),
            new Ingredient("Extra Fish Patty", 3.40, 200, "Meat", false),
            // --- 蔬菜、芝士与酱料 ---
            new Ingredient("Lettuce", 0.50, 10, "Vegetable", false),
            new Ingredient("Tomato", 0.50, 15, "Vegetable", false),
            new Ingredient("Cheddar Cheese", 1.00, 110, "Cheese", false),
            new Ingredient("Pickles", 0.40, 5, "Vegetable", false),
            new Ingredient("BBQ Sauce", 0.60, 70, "Sauce", false),
            new Ingredient("Mayo Sauce", 0.60, 90, "Sauce", false),
            new Ingredient("Tartar Sauce", 0.70, 100, "Sauce", false)
    );

    public BurgerView() {
        createUI();
        loadStandardBurger("Beef Burger");
        updateUI();
    }

    public Pane getRoot() {
        return root;
    }

    private void createUI() {
        ComboBox<String> cmbBurgerType = new ComboBox<>();
        cmbBurgerType.getItems().addAll("Beef Burger", "Chicken Burger", "Fish Burger");
        cmbBurgerType.setValue("Beef Burger");
        cmbBurgerType.setOnAction(e -> {
            loadStandardBurger(cmbBurgerType.getValue());
            updateUI();
            showFeedback("Loaded standard " + cmbBurgerType.getValue(), false);
        });

        quantitySpinner = new Spinner<>(1, 5, 1);
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateUI());

        HBox selectionBox = new HBox(15, new Label("Select Burger:"), cmbBurgerType, new Label("Burger Qty:"), quantitySpinner);
        selectionBox.setPadding(new Insets(10));

        currentIngredientsListView = new ListView<>();
        availableIngredientsListView = new ListView<>();
        availableIngredientsListView.setItems(availableMasterList);

        addOnQuantitySpinner = new Spinner<>(1, 5, 1);
        addOnQuantitySpinner.setPrefWidth(70);

        Button btnAdd = new Button("<< Add");
        Button btnRemove = new Button("Remove >>");
        btnAdd.setOnAction(e -> handleAddIngredient());
        btnRemove.setOnAction(e -> handleRemoveIngredient());

        VBox centerButtons = new VBox(10, new Label("Add Qty:"), addOnQuantitySpinner, btnAdd, btnRemove);
        centerButtons.setPadding(new Insets(10));

        VBox currentBox = new VBox(5, new Label("Current Ingredients:"), currentIngredientsListView);
        VBox availableBox = new VBox(5, new Label("Available Add-ons (Select Meat/Toppings):"), availableIngredientsListView);
        HBox listsBox = new HBox(15, currentBox, centerButtons, availableBox);
        listsBox.setPadding(new Insets(10));

        lblTotalPrice = new Label("Total Price: $0.00");
        lblTotalCalories = new Label("Total Calories: 0 kcal");
        VBox metricsBox = new VBox(8, lblTotalPrice, lblTotalCalories);

        txtOrderSummary = new TextArea();
        txtOrderSummary.setEditable(false);
        txtOrderSummary.setPrefRowCount(5);

        lblFeedback = new Label("");
        Button btnClear = new Button("Clear Order");
        btnClear.setOnAction(e -> {
            loadStandardBurger(cmbBurgerType.getValue());
            quantitySpinner.getValueFactory().setValue(1);
            addOnQuantitySpinner.getValueFactory().setValue(1);
            showFeedback("Order cleared to default.", false);
        });

        VBox bottomBox = new VBox(10, metricsBox, new Label("Order Summary:"), txtOrderSummary, btnClear, lblFeedback);
        bottomBox.setPadding(new Insets(10));

        root = new BorderPane();
        root.setTop(selectionBox);
        root.setCenter(listsBox);
        root.setBottom(bottomBox);
        root.setPadding(new Insets(15));
    }

    private void loadStandardBurger(String type) {
        if ("Chicken Burger".equals(type)) currentBurger = Burger.createChickenBurger();
        else if ("Fish Burger".equals(type)) currentBurger = Burger.createFishBurger();
        else currentBurger = Burger.createBeefBurger();
    }

    private void handleAddIngredient() {
        Ingredient selected = availableIngredientsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select an item to add.", true);
            return;
        }

        int addCount = addOnQuantitySpinner.getValue();
        int addedSuccessfully = 0;

        for (int i = 0; i < addCount; i++) {
            if (!selected.isRequired()) {
                Ingredient customIng = new Ingredient(
                        selected.getName() + " (x1)",
                        selected.getPrice(),
                        selected.getCalories(),
                        selected.getCategory(),
                        false
                );
                currentBurger.addIngredient(customIng);
                addedSuccessfully++;
            } else {
                if (!currentBurger.hasIngredient(selected.getName())) {
                    currentBurger.addIngredient(selected);
                    addedSuccessfully++;
                }
            }
        }

        if (addedSuccessfully > 0) {
            showFeedback("Added " + addCount + "x " + selected.getName() + " successfully.", false);
        } else {
            showFeedback("Required component already exists.", true);
        }
        updateUI();
    }

    private void handleRemoveIngredient() {
        Ingredient selected = currentIngredientsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select an ingredient from the current burger to remove.", true);
            return;
        }
        if (selected.isRequired()) {
            showFeedback("Cannot remove base bun or main patty.", true);
            return;
        }
        currentBurger.removeIngredient(selected);
        showFeedback("Removed " + selected.getName() + " successfully.", false);
        updateUI();
    }

    private void updateUI() {
        currentIngredientsListView.setItems(FXCollections.observableArrayList(currentBurger.getIngredients()));
        int qty = quantitySpinner.getValue();
        double totalP = currentBurger.calculatePrice() * qty;
        int totalC = currentBurger.calculateCalories() * qty;

        lblTotalPrice.setText(String.format("Total Price: $%.2f", totalP));
        lblTotalCalories.setText("Total Calories: " + totalC + " kcal");

        StringBuilder sb = new StringBuilder();
        sb.append("Burger: ").append(currentBurger.getName()).append(" | Qty: ").append(qty).append("\n");
        for (Ingredient i : currentBurger.getIngredients()) {
            sb.append(" - ").append(i.getName()).append(" ($").append(String.format("%.2f", i.getPrice())).append(")\n");
        }
        sb.append(String.format("Total: $%.2f (%d kcal)", totalP, totalC));
        txtOrderSummary.setText(sb.toString());
    }

    private void showFeedback(String msg, boolean isErr) {
        lblFeedback.setText(msg);
        lblFeedback.setStyle(isErr ? "-fx-text-fill: red; -fx-font-weight: bold;" : "-fx-text-fill: green; -fx-font-weight: bold;");
    }
}