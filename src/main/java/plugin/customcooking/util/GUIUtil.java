package plugin.customcooking.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import plugin.customcooking.manager.configs.ConfigManager;
import plugin.customcooking.manager.configs.MasteryManager;

import java.util.List;

import static plugin.customcooking.util.InventoryUtil.playerHasIngredient;

public class GUIUtil {

    public static void appendMastery(List<String> lore, Player player, String recipe, Boolean hasMastery) {

        Integer masteryCount = MasteryManager.getMasteryCount(player, recipe);
        Integer requiredMastery = MasteryManager.getRequiredMastery(recipe);
        String[] masteryInfo;

        lore.add(" ");
        if (Boolean.TRUE.equals(hasMastery)) {
            lore.add(ConfigManager.masteryLine.replace("{mastery}",  (masteryCount + "/" + requiredMastery)));
            masteryInfo = ConfigManager.masteryInfoTrue.split("/");
        } else {
            lore.add(ConfigManager.masteryLine.replace("{mastery}", (masteryCount + "/" + requiredMastery)));
            lore.add(ConfigManager.masteryBar.replace("{bar}", GUIUtil.appendProgressBar(masteryCount/requiredMastery)));
            masteryInfo = ConfigManager.masteryInfoFalse.split("/");
        }
        lore.add(masteryInfo[0]);
        lore.add(masteryInfo[1]);
    }

    public static String appendProgressBar(double percentage) {
        int length = 10; // Length of the progress bar
        int completedLength = (int) (length * percentage);
        StringBuilder progressBar = new StringBuilder();

        for (int i = 0; i < length; i++) {
            if (i < completedLength) {
                progressBar.append("<#fcac32>■"); // color code for completed blocks
            } else if (i == completedLength) {
                progressBar.append("<#ffcc33>■"); // color code for current value block
            } else {
                progressBar.append("<#ffcc99>■"); // color code for remaining blocks
            }
        }
        return progressBar.toString();
    }

    public static void appendIngredients(List<String> lore, Player player, List<String> ingredients) {
        lore.add(" ");
        lore.add(ConfigManager.ingredientsLine);

        for (String ingredient : ingredients) {
            if (ingredient.contains("/")) {
                handleOptions(lore, player, ingredient);
            } else {
                String[] parts = ingredient.split(":");
                String ingredientFormatted = formatString(parts[0]);

                if (InventoryUtil.playerHasIngredient(player.getInventory(), parts[0])) {
                    lore.add("<green><!italic>- (x" + parts[1] + ") " + ingredientFormatted);
                } else {
                    lore.add("<red><!italic>- (x" + parts[1] + ") " + ingredientFormatted);
                }
            }
        }
    }

    private static void handleOptions(List<String> lore, Player player, String ingredient) {
        String[] options = ingredient.split("/");
        for (String option : options) {
            String[] parts = option.split(":");
            String ingredientFormatted = formatString(parts[0]);

            if (InventoryUtil.playerHasIngredient(player.getInventory(), parts[0])) {
                lore.add("<green><!italic>- (x" + parts[1] + ") " + ingredientFormatted);
                return; // Exit the method after handling one option
            }
        }
        // If none of the options were found, add the red lore for the ingredient
        lore.add("<red><!italic>- (x" + options[0].split(":")[1] + ") " + formatString(options[0].split(":")[0]));
    }



    public static String formatString(String input) {
        String[] words = input.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase().replace("*", "");
                result.append(capitalizedWord).append(" ");
            }
        }
        return result.toString().trim();
    }
}