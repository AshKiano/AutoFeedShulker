package com.ashkiano.autofeedshulker;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;

public class AutoFeedShulkerCommand implements CommandExecutor {

    private final String SPECIAL_LORE = "Special Shulker for auto-feeding";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack shulkerBox = new ItemStack(Material.SHULKER_BOX);
            BlockStateMeta meta = (BlockStateMeta) shulkerBox.getItemMeta();

            // Creating lore
            ArrayList<String> lore = new ArrayList<>();
            lore.add(SPECIAL_LORE);
            meta.setLore(lore);

            shulkerBox.setItemMeta(meta);
            player.getInventory().addItem(shulkerBox);
            player.sendMessage("You received a special shulker box!");
        } else {
            sender.sendMessage("This command can only be run by a player.");
        }
        return true;
    }
}
