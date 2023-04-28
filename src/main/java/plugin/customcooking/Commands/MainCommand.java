package plugin.customcooking.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.customcooking.Manager.CookingManager;
import plugin.customcooking.Util.AdventureUtil;
import plugin.customcooking.Util.ConfigUtil;
import plugin.customcooking.Util.InventoryUtil;

import java.util.Arrays;

public class MainCommand implements CommandExecutor {

    private final CookingManager cookingManager;
    private final InventoryUtil inventoryUtil;

    public MainCommand() {
        this.cookingManager = new CookingManager();
        this.inventoryUtil = new InventoryUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            AdventureUtil.sendMessage(sender, "<gold><bold>CustomCooking</bold><grey> version 1.0.0");
            AdventureUtil.sendMessage(sender, "<grey>Created by <gold>SnowyOwl217");
            AdventureUtil.sendMessage(sender, "<gold>/cook cook <recipe> <player> [auto]");
            AdventureUtil.sendMessage(sender, "<gold>/cook reload");
            return false;
        }
        String subcommand = args[0];
        String[] subargs = Arrays.copyOfRange(args, 1, args.length);

        if (subcommand.equalsIgnoreCase("cook")) {
            if (subargs.length < 2) {
                AdventureUtil.sendMessage(sender, "<grey>[<red><bold>!</bold><grey>]<red>/cooking cook <recipe> <player> <auto>");
                return true;
            }
            String recipe = subargs[0];
            String username = subargs[1];
            if (subargs.length == 2) {
                Cook(recipe, username, false);
            } else if (subargs.length == 3 && subargs[2].equalsIgnoreCase("auto")){
                Cook(recipe, username, true);
            } else {
                AdventureUtil.sendMessage(sender, "<grey>[<red><bold>!</bold><grey>]<red>/cooking cook <recipe> <player> <auto>");
            } return true;
        } else if (subcommand.equalsIgnoreCase("reload")) {
            Reload(sender);
            return true;
        } else {
            // Unknown subcommand
            AdventureUtil.sendMessage(sender, "<grey>[<red><bold>!</bold><grey>]<red> Unknown subcommand: " + subcommand);
            return false;
        }
    }


    public void Cook(String recipe, String username, Boolean auto) {
        Player player = Bukkit.getPlayer(username);
        if (player != null) {
            inventoryUtil.ingredientCheck(player, recipe, auto);
        } else {
            AdventureUtil.consoleMessage("<Red> [!] Player " + username + " not found.");
        }
    }

    public void Reload(CommandSender sender) {
        long time1 = System.currentTimeMillis();
        ConfigUtil.reload();
        AdventureUtil.sendMessage(sender, "<gray>[CustomCooking] Reloaded plugin in <green>" + (System.currentTimeMillis() - time1) + " <gray>seconds");
    }
}
