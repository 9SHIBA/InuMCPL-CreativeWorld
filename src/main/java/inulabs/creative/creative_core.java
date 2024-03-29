package inulabs.creative;

import inulabs.creative.commands.creative_modify;
import inulabs.creative.commands.creative_teleport;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;

public final class creative_core extends JavaPlugin {
    //Product of inu dev lab
    //Original API From Kimkung
    //inspiration by Tackle 4826
    @Override
    public void onEnable() {
        for (Player p : Bukkit.getOnlinePlayers()
        ) {
            p.sendMessage(Format("{YELLOW}Creative world plugin - Reloading"));
        }
        Objects.requireNonNull(this.getCommand("creative")).setExecutor(new creative_teleport());
        Objects.requireNonNull(this.getCommand("c")).setExecutor(new creative_teleport());
        Objects.requireNonNull(this.getCommand("creative-modify")).setExecutor(new creative_modify());
        this.getServer().getPluginManager().registerEvents(new creative_event(), this);
        World world = Bukkit.getWorld("creative_world");
        if (world == null) {
            // Load the world if it's not already loaded
            loadWorld("creative_world");
        }
        creativeworld_runtime();

    }

    private void loadWorld(String worldName) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        Bukkit.createWorld(worldCreator);
        getLogger().info("World '" + worldName + "' has been loaded.");
    }

    public static String Format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg.replace("{BLACK}", "&0").replace("{DARK_BLUE}", "&1").replace("{DARK_GREEN}", "&2").replace("{DARK_AQUA}", "&3").replace("{DARK_RED}", "&4").replace("{DARK_PURPLE}", "&5").replace("{GOLD}", "&6").replace("{GRAY}", "&7").replace("{DARK_GRAY}", "&8").replace("{BLUE}", "&9").replace("{GREEN}", "&a").replace("{AQUA}", "&b").replace("{RED}", "&c").replace("{LIGHT_PURPLE}", "&d").replace("{YELLOW}", "&e").replace("{WHITE}", "&f").replace("{BOLD}", "&l").replace("{B}", "&l").replace("{U}", "&n").replace("{I}", "&o").replace("{RESET}", "&r").replace("{N}", "\n").replace("{S}", " "));
    }

    public static boolean isWorldExists() {
        World world = Bukkit.getWorld(creative_modify.worldname);
        return world != null;
    }

    @Override
    public void onDisable() {
        if (isWorldExists()) {
            Bukkit.getWorld(creative_modify.worldname).save();
        }
        forcecreativeleave();
    }

    public static void forcecreativeleave() {
        if (isWorldExists()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Objects.requireNonNull(p.getLocation().getWorld()).getName().equals(creative_modify.worldname)) {
                    leavecreativeworld(p);
                }
            }
        }
    }

    public static void leavecreativeworld(Player p) {
        if (isWorldExists()) {
            Double X = p.getPersistentDataContainer().get(creative_data.KEY_C_X, PersistentDataType.DOUBLE);
            Double Y = p.getPersistentDataContainer().get(creative_data.KEY_C_Y, PersistentDataType.DOUBLE);
            Double Z = p.getPersistentDataContainer().get(creative_data.KEY_C_Z, PersistentDataType.DOUBLE);
            World world = Bukkit.getWorld(p.getPersistentDataContainer().get(creative_data.KEY_C_WORLD, PersistentDataType.STRING));

            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));

            p.teleport(new Location(world, X, Y, Z));

            p.getLocation().setYaw(p.getPersistentDataContainer().get(creative_data.KEY_C_YAW, PersistentDataType.FLOAT));
            p.setHealth(p.getPersistentDataContainer().get(creative_data.KEY_C_HEATH, PersistentDataType.DOUBLE));
            p.setGameMode(GameMode.getByValue(p.getPersistentDataContainer().get(creative_data.KEY_C_GAMEMODE, PersistentDataType.INTEGER)));
            p.setSaturation(p.getPersistentDataContainer().get(creative_data.KEY_C_SATURATION, PersistentDataType.FLOAT));
            p.setExp(p.getPersistentDataContainer().get(creative_data.KEY_C_EXP, PersistentDataType.FLOAT));
            p.setLevel(p.getPersistentDataContainer().get(creative_data.KEY_C_LEVEL, PersistentDataType.INTEGER));



            applySavedPotionEffects(p);
            restoreInventoryFromDataContainer(p);

            p.getPersistentDataContainer().set(creative_data.KEY_C_INVENTORY, PersistentDataType.STRING, "");

            p.sendMessage(Format("{GREEN}Teleport to Default world!!"));
        }

    }

    public static void restoreInventoryFromDataContainer(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        // Retrieve the serialized inventory data from the data container
        String serializedInventory = dataContainer.getOrDefault(creative_data.KEY_C_INVENTORY, PersistentDataType.STRING, "");

        // Deserialize the inventory from the string
        Inventory inventory = deserializeInventoryFromString(serializedInventory);

        // Clear player's current inventory
        player.getInventory().clear();

        // Set the retrieved inventory to the player
        player.getInventory().setContents(inventory.getContents());
    }

    private static Inventory deserializeInventoryFromString(String serializedInventory) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(serializedInventory);
            Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER);

            ConfigurationSection items = config.getConfigurationSection("item");
            if (items != null) {
                for (String key : items.getKeys(false)) {
                    int slot = Integer.parseInt(key);
                    ItemStack item = ItemStack.deserialize(items.getConfigurationSection(key).getValues(false));
                    inventory.setItem(slot, item);
                }
            }

            return inventory;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return Bukkit.createInventory(null, InventoryType.PLAYER); // Return empty inventory in case of error
        }
    }

    public static void applySavedPotionEffects(Player player) {
        // Retrieve the serialized potion data from the player's PersistentDataContainer
        String potionData = player.getPersistentDataContainer().get(creative_data.KEY_C_POTION, PersistentDataType.STRING);

        // Check if the potion data exists and is not empty
        if (potionData != null && !potionData.isEmpty()) {
            // Split the serialized data into individual potion effect strings
            String[] potionEffectStrings = potionData.split(";");

            // Iterate over each potion effect string
            for (String effectString : potionEffectStrings) {
                // Split the potion effect string into its components
                String[] parts = effectString.split(",");
                if (parts.length == 3) {
                    // Extract the potion effect type, duration, and amplifier
                    PotionEffectType type = PotionEffectType.getByName(parts[0]);
                    int duration = Integer.parseInt(parts[1]);
                    int amplifier = Integer.parseInt(parts[2]);

                    // Create the PotionEffect and apply it to the player
                    if (type != null) {
                        PotionEffect potionEffect = new PotionEffect(type, duration, amplifier);
                        player.addPotionEffect(potionEffect, true);
                    }
                }
            }
        }
    }

    public void creativeworld_runtime() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getLocation().getY() <= -200) {
                        p.teleport(new Location(p.getWorld(), p.getLocation().getX(), 200, p.getLocation().getZ()));
                        p.sendMessage(Format("{YELLOW}Don't fell down Honey!!"));


                    }
                }
            }
        }.runTaskTimer(this, 1, 1);
    }


}
