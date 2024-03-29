package inulabs.creative.commands;

import inulabs.creative.creative_core;
import inulabs.creative.creative_data;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class creative_teleport  implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof  Player p){
            if (isWorldExists()) {

                if (p.getLocation().getWorld() != Bukkit.getWorld("creative_world")) {
                    //---------GET DATA------------
                    Double x = p.getLocation().getX();
                    Double y = p.getLocation().getY();
                    Double z = p.getLocation().getZ();
                    float yaw = p.getLocation().getYaw();
                    String world = Objects.requireNonNull(p.getLocation().getWorld()).getName();
                    //---------------------------------------------------------------------------

                    //Player DataContainer Storage--------------------------------------
                    p.getPersistentDataContainer().set(creative_data.KEY_C_X, PersistentDataType.DOUBLE, x);
                    p.getPersistentDataContainer().set(creative_data.KEY_C_Y, PersistentDataType.DOUBLE, y);
                    p.getPersistentDataContainer().set(creative_data.KEY_C_Z, PersistentDataType.DOUBLE, z);
                    p.getPersistentDataContainer().set(creative_data.KEY_C_YAW,PersistentDataType.FLOAT,yaw);
                    p.getPersistentDataContainer().set(creative_data.KEY_C_WORLD, PersistentDataType.STRING, world);
                    p.getPersistentDataContainer().set(creative_data.KEY_C_HEATH,PersistentDataType.DOUBLE,p.getHealth());
                    p.getPersistentDataContainer().set(creative_data.KEY_C_GAMEMODE,PersistentDataType.INTEGER,p.getGameMode().getValue());
                    p.getPersistentDataContainer().set(creative_data.KEY_C_SATURATION,PersistentDataType.FLOAT,p.getSaturation());
                    p.getPersistentDataContainer().set(creative_data.KEY_C_EXP,PersistentDataType.FLOAT,p.getExp());
                    p.getPersistentDataContainer().set(creative_data.KEY_C_LEVEL,PersistentDataType.INTEGER,p.getLevel());
                    
                    //---------------------------------------------------------------------
                    Location loc = Bukkit.getServer().getWorld("creative_world").getSpawnLocation();
                    p.teleport(loc);
                    p.setGameMode(GameMode.getByValue(1));
                    saveInventoryToDataContainer(p);
                    savePotionEffects(p);
                    p.setHealth(20.0f);
                    p.setLevel(0);
                    p.setExp(0.0f);
                    p.getInventory().clear();
                    p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));

                    p.sendMessage(creative_core.Format("{GREEN}Teleport to Creative World !!"));
                } else {

                    creative_core.leavecreativeworld(p);
                }


            }else {
                p.sendMessage(creative_core.Format("{YELLOW} Creative world is not loaded!!"));
            }
        }

        return false;
    }
    private boolean isWorldExists() {
        World world = getServer().getWorld(creative_modify.worldname);
        return world != null;
    }
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }




    public void saveInventoryToDataContainer(Player player) {
        Inventory inventory = player.getInventory();
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        // Clear previous inventory data from the data container
        dataContainer.remove(creative_data.KEY_C_INVENTORY);

        // Serialize the inventory to a string
        String serializedInventory = serializeInventoryToString(inventory);

        // Save the serialized inventory data to the data container
        dataContainer.set(creative_data.KEY_C_INVENTORY, PersistentDataType.STRING, serializedInventory);
    }

    private String serializeInventoryToString(Inventory inventory) {
        YamlConfiguration config = new YamlConfiguration();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                config.set("item." + i, item.serialize());
            }
        }

        return config.saveToString();
    }


    public void savePotionEffects(Player player) {
        StringBuilder potionData = new StringBuilder();

        // Serialize each potion effect to a string representation
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            PotionEffectType type = potionEffect.getType();
            int duration = potionEffect.getDuration();
            int amplifier = potionEffect.getAmplifier();

            // Append the potion effect data to the string
            potionData.append(type.getName()).append(",").append(duration).append(",").append(amplifier).append(";");
        }

        // Store the serialized potion data in the player's PersistentDataContainer
        player.getPersistentDataContainer().set(creative_data.KEY_C_POTION, PersistentDataType.STRING, potionData.toString());
    }

}
