package com.ashkiano.autofeedshulker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

//TODO zmenit barvu shulkeru
//TODO udelat barvu nastavitelnou v configu
//TODO udelat barvu nastavitelnou prikazem na givnutí
//TODO udelat prikaz na permisi
//TODO dodat donate hlášku
//TODO opravit feeding, protože krmí vždy při polovině hladu a spotřebuje celé jídlo a nedělá ten přesah, takže nevyužívá jídlo efektivně
public class AutoFeedShulker extends JavaPlugin implements Listener {

    private final String SPECIAL_LORE = "Special Shulker for auto-feeding";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("giveshulker").setExecutor(new AutoFeedShulkerCommand());

        // Create a config file if it doesn't exist
        this.saveDefaultConfig();

        Metrics metrics = new Metrics(this, 19418);

        this.getLogger().info("Thank you for using the AutoFeedShulker plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://paypal.me/josefvyskocil");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getFoodLevel() < 20) {
            for (ItemStack item : player.getInventory()) {
                if (item != null && item.getType() == Material.SHULKER_BOX) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof BlockStateMeta) {
                        BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                        if (meta.hasLore()) {
                            List<String> lore = meta.getLore();
                            if (lore.contains(SPECIAL_LORE)) {
                                if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                                    ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                                    for (ItemStack shulkerItem : shulkerBox.getInventory()) {
                                        if (shulkerItem != null && shulkerItem.getType().isEdible()) {
                                            int foodValue = getFoodValue(shulkerItem.getType());
                                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + foodValue));
                                            shulkerItem.setAmount(shulkerItem.getAmount() - 1);
                                            blockStateMeta.setBlockState(shulkerBox);  // Update the block state in the meta
                                            item.setItemMeta(blockStateMeta);  // Set the updated meta back to the item
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof ShulkerBox) {
            ItemStack item = event.getItemInHand();
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasLore() && meta.getLore().contains(SPECIAL_LORE)) {
                    // Save the location of the shulker box
                    String location = block.getLocation().getWorld().getName() + "," +
                            block.getLocation().getBlockX() + "," +
                            block.getLocation().getBlockY() + "," +
                            block.getLocation().getBlockZ();
                    List<String> shulkers = getConfig().getStringList("shulkers");
                    shulkers.add(location);
                    getConfig().set("shulkers", shulkers);
                    saveConfig();
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof ShulkerBox) {
            String location = block.getLocation().getWorld().getName() + "," +
                    block.getLocation().getBlockX() + "," +
                    block.getLocation().getBlockY() + "," +
                    block.getLocation().getBlockZ();
            List<String> shulkers = getConfig().getStringList("shulkers");
            if (shulkers.contains(location)) {
                shulkers.remove(location);
                getConfig().set("shulkers", shulkers);
                saveConfig();

                ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
                if (itemInHand != null && itemInHand.getType() == Material.AIR) {
                    ItemStack item = new ItemStack(Material.SHULKER_BOX);
                    BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                    ShulkerBox shulker = (ShulkerBox) block.getState();
                    meta.setBlockState(shulker);
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(SPECIAL_LORE);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    block.getWorld().dropItemNaturally(block.getLocation(), item);
                    event.setDropItems(false);
                }
            }
        }
    }

    private int getFoodValue(Material material) {
        switch (material) {
            case APPLE:
                return 4;
            case BAKED_POTATO:
                return 5;
            case BEEF:
                return 3;
            case BEETROOT:
                return 1;
            case BEETROOT_SOUP:
                return 6;
            case BREAD:
                return 5;
            case CAKE:
                return 2; // per slice
            case CARROT:
                return 3;
            case CHICKEN:
                return 2;
            case CHORUS_FRUIT:
                return 4;
            case COD:
                return 2;
            case COOKED_BEEF:
                return 8;
            case COOKED_CHICKEN:
                return 6;
            case COOKED_COD:
                return 5;
            case COOKED_MUTTON:
                return 6;
            case COOKED_PORKCHOP:
                return 8;
            case COOKED_RABBIT:
                return 5;
            case COOKED_SALMON:
                return 6;
            case COOKIE:
                return 2;
            case GOLDEN_APPLE:
                return 4;
            case GOLDEN_CARROT:
                return 6;
            case HONEY_BOTTLE:
                return 6;
            case MELON_SLICE:
                return 2;
            case MUSHROOM_STEW:
                return 6;
            case MUTTON:
                return 2;
            case POISONOUS_POTATO:
                return 2;
            case PORKCHOP:
                return 3;
            case POTATO:
                return 1;
            case PUFFERFISH:
                return 1;
            case PUMPKIN_PIE:
                return 8;
            case RABBIT:
                return 3;
            case RABBIT_STEW:
                return 10;
            case ROTTEN_FLESH:
                return 4;
            case SALMON:
                return 2;
            case SPIDER_EYE:
                return 2;
            case SUSPICIOUS_STEW:
                return 6; // Can vary based on mushroom used
            case SWEET_BERRIES:
                return 2;
            case TROPICAL_FISH:
                return 1;
            default:
                return 0;
        }
    }
}