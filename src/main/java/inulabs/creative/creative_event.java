package inulabs.creative;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;


public class creative_event implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player p ){
            if(p.getLocation().getWorld() == Bukkit.getWorld("creative_world")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPortalCreate (PortalCreateEvent event){
       if( event.getWorld() == Bukkit.getWorld("creative_world")){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerportal(PlayerPortalEvent event){
        if( event.getPlayer().getWorld() == Bukkit.getWorld("creative_world")){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer().getWorld() == Bukkit.getWorld("creative_world")) {
            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND)) {
                event.getPlayer().sendMessage(creative_core.Format("{RED}you can't use this command in this world"));

                event.setCancelled(true);

            }
        }
        if(event.getTo().getWorld() == Bukkit.getWorld("creative_world") && !event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)){
            event.getPlayer().sendMessage(creative_core.Format("{YELLOW}Use (/c or /creative) to teleport to creative world !!"));
            event.setCancelled(true);
        }
        if(event.getFrom().getWorld().equals("creative_world") && !event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)){
            event.getPlayer().sendMessage(creative_core.Format("{YELLOW}Use (/c or /creative) to teleport to outside Creative world !!"));
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void enity(EntityTeleportEvent event){
        if(event.getFrom().getWorld() == Bukkit.getWorld("creative_world") && !event.getEntity().getType().equals(EntityType.PLAYER)){
            event.setCancelled(true);
        }
        if(event.getTo().getWorld() == Bukkit.getWorld("creative_world") && !event.getEntity().getType().equals(EntityType.PLAYER)){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void interact(PlayerInteractEvent e){
        if(e.getPlayer().getWorld() == Bukkit.getWorld("creative_world")){
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK ) {
             e.getPlayer().sendMessage("BLOCK : " + e.getClickedBlock().getType());
             if (e.getClickedBlock().getType() == Material.RESPAWN_ANCHOR || e.getClickedBlock().getType() == Material.ENDER_CHEST){
                 e.setCancelled(true);
             }
             switch (e.getClickedBlock().getType()){
                 case BLACK_BED -> {e.setCancelled(true);}
                 case BROWN_BED -> {e.setCancelled(true);}
                 case RED_BED -> {e.setCancelled(true);}
                 case WHITE_BED -> {e.setCancelled(true);}
                 case GRAY_BED -> {e.setCancelled(true);}
                 case LIGHT_GRAY_BED -> {e.setCancelled(true);}
                 case ORANGE_BED -> {e.setCancelled(true);}
                 case YELLOW_BED -> {e.setCancelled(true);}
                 case LIME_BED -> {e.setCancelled(true);}
                 case GREEN_BED -> {e.setCancelled(true);}
                 case CYAN_BED -> {e.setCancelled(true);}
                 case LIGHT_BLUE_BED -> {e.setCancelled(true);}
                 case BLUE_BED -> {e.setCancelled(true);}
                 case PURPLE_BED -> {e.setCancelled(true);}
                 case MAGENTA_BED -> {e.setCancelled(true);}
                 case PINK_BED -> {e.setCancelled(true);}

             }
          }
        }
    }
 @EventHandler
 public void portalentity(EntityPortalEvent e){
     if (e.getFrom().getWorld() == Bukkit.getWorld("creative_world") ){
         e.setCancelled(true);
     }
 }
 @EventHandler
    public void onAdvanment(PlayerAdvancementDoneEvent event){
        if (event.getPlayer().getWorld() == Bukkit.getWorld("creative_world")){
            Advancement advancement = event.getAdvancement();
            for (String c : advancement.getCriteria())
               event.getPlayer().getAdvancementProgress(advancement).revokeCriteria(c);


        }
    }
}
