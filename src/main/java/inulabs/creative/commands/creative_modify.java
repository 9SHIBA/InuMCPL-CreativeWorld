package inulabs.creative.commands;

import inulabs.creative.creative_core;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class creative_modify implements TabExecutor {
    public static String worldname = "creative_world";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p){
                if(args.length >= 1){
                String Type = args[0];
                p.sendMessage("0" + Type);

                if(Type.equals("create")){
                //create
                if (!isWorldExists()) {
                    p.sendMessage("1");
                    if (args.length >= 2 && args[1].equals("flat")) {
                        p.sendMessage(creative_core.Format("{GREEN}Creative world - creating"));
                        generateFlatWorld();
                    }


                }else {
                    p.sendMessage(creative_core.Format("{RED}!!The creative world is already generated!!"));
                    }
                }

                if (Type.equals("delete")) {
                    if (isWorldExists()){
                        creative_core.forcecreativeleave();
                        destroyWorld(worldname,p);
                        p.sendMessage(creative_core.Format("{YELLOW}!!Reset World in process!!"));

                    }else {
                        p.sendMessage(creative_core.Format("{RED}!!The creative world is already Delete!!"));

                    }
                }
                if(Type.equals("gamerule")){

                }
                }


        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1){
            list.add("create");
            list.add("delete");

        }
        if (args.length == 2 && args[0].equals("create")){
            list.add("flat");

        }


        return list;
    }
    public void generateFlatWorld() {
        WorldCreator worldCreator = new WorldCreator(worldname);
        worldCreator.type(WorldType.FLAT);

        World world = getServer().createWorld(worldCreator);
        getLogger().info("Flat world '" + worldname + "' has been generated!");
    }
    private boolean isWorldExists() {
        World world = getServer().getWorld(worldname);
        return world != null;
    }


    private void destroyWorld(String worldName,Player p) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            // Unload the world
            if (Bukkit.unloadWorld(world, false)) {
                getLogger().info("World '" + worldName + "' unloaded successfully.");
                p.sendMessage(creative_core.Format("{GREEN}Reset world successfully!!"));
            } else {
                getLogger().warning("Failed to unload world '" + worldName + "'.");
            }

            // Delete the world files
            boolean deleted = deleteWorldFiles(world.getWorldFolder());
            if (deleted) {
                getLogger().info("World '" + worldName + "' deleted successfully.");
            } else {
                getLogger().warning("Failed to delete world '" + worldName + "' files.");
            }
        } else {
            getLogger().warning("World '" + worldName + "' does not exist.");
        }
    }

    private boolean deleteWorldFiles(java.io.File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFiles(file);
                    } else {
                        if (!file.delete()) {
                            return false;
                        }
                    }
                }
            }
        }
        return path.delete();
    }

}
