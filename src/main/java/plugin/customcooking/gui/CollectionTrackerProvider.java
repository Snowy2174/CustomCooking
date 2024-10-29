package plugin.customcooking.gui;

import dev.lone.itemsadder.api.CustomStack;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.customcooking.CustomCooking;
import plugin.customcooking.manager.CookingManager;
import plugin.customcooking.manager.configs.ConfigManager;
import plugin.customcooking.manager.configs.MessageManager;
import plugin.customcooking.util.AdventureUtil;
import plugin.customcooking.util.GUIUtil;

import java.util.ArrayList;
import java.util.List;

import static dev.lone.itemsadder.api.ItemsAdder.getAllItems;
import static plugin.customcooking.gui.GuiManager.*;
import static plugin.customcooking.manager.configs.RecipeManager.RECIPES;
import static plugin.customcooking.util.InventoryUtil.build;

public class CollectionTrackerProvider implements InventoryProvider {
    private final CookingManager cookingManager;
    CollectionTrackerProvider() {
        this.cookingManager = CustomCooking.getCookingManager();
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // Doesn't do anything yet, eventually will update ingredients?
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[collectionItems.size()];

        for(int i = 0; i < items.length; i++)
            items[i] = ClickableItem.empty(collectionItems.get(i));
        pagination.setItems(items);
        pagination.setItemsPerPage(7);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));

        contents.set(2, 3, ClickableItem.of(new ItemStack(Material.ARROW),
                e -> PROGRESSION_MENU.open(player, pagination.previous().getPage())));
        contents.set(2, 5, ClickableItem.of(new ItemStack(Material.ARROW),
                e -> PROGRESSION_MENU.open(player, pagination.next().getPage())));
        // Insert logic to check if the player has collected item
    }

    private ItemStack buildRecipeItem(String recipe, Player player, boolean hasMastery){
        CustomStack customStack = CustomStack.getInstance(recipe);
        if (hasMastery) {customStack = CustomStack.getInstance(recipe + ConfigManager.perfectItemSuffix);}
        if (customStack == null) {
            return null;
        } else {
            ItemStack stack = customStack.getItemStack();
            modifyLore(stack, player, recipe, hasMastery);
            return stack;
        }
    }
    private void modifyLore(ItemStack itemStack, Player player, String recipe, Boolean hasMastery) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
            itemStack.setItemMeta(itemMeta);
        }
        List<String> lore = itemMeta.getLore();

        if (!itemMeta.hasLore()) {
            lore = new ArrayList<>();
            lore.add("This item does not have lore! Configure it correctly in ItemsAdder!");
        }

        GUIUtil.appendMastery(lore, player, recipe, hasMastery);

        if (RECIPES.get(recipe).getIngredients() != null) {
            GUIUtil.appendIngredients(lore, player, RECIPES.get(recipe).getIngredients());
        }

            lore.add(" ");
            if (Boolean.TRUE.equals(hasMastery)) {
                lore.add(ConfigManager.cookLineRight);
                lore.add(ConfigManager.cookLineLeft);
                lore.add(ConfigManager.cookLineShift);
            } else {
                lore.add(ConfigManager.cookLine);
            }

        // Create a new list to store parsed lore
        List<Component> parsedLore = new ArrayList<>();

        // Parse each lore line and add it to the parsedLore list
        for (String line : lore) {
            parsedLore.add(AdventureUtil.getComponentFromMiniMessage(line));
        }

        itemMeta.lore(parsedLore);
        itemStack.setItemMeta(itemMeta);
    }

    private void handleItemClick(InventoryClickEvent event, Player player, String recipe, boolean hasRecipe, boolean hasMastery) {
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null && clickedItem.getType() != Material.AIR) {

            if (!hasRecipe) {
                AdventureUtil.playerMessage(player, MessageManager.infoNegative + MessageManager.recipeUnknown);
                return;
            }
            if (hasMastery) {
                if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                    // Shift click handling for cooking 15 Recipes
                    cookingManager.handleAutocooking(recipe, player, 15);
                } else if (event.isRightClick()) {
                    // Right click handling for cooking the recipe
                } else if (event.isLeftClick()) {
                    // Left-click handling logic for autocooking the recipe
                    cookingManager.handleAutocooking(recipe, player, 1);
                }
            } else {
            }
            event.setCancelled(true);
        }
    }


}
