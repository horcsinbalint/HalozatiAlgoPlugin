package hu.horcsin.halozatiAlgoPlugin;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class HalozatiAlgoPlugin extends JavaPlugin implements CommandExecutor {

    FileConfiguration config;

    @Override
    public void onEnable() {
        getLogger().info("HalozatiAlgoPlugin is enabled!");
        config = this.getConfig();
        config.addDefault("X1", 0);
        config.addDefault("Y1", 0);
        config.addDefault("Z1", 0);
        config.addDefault("X2", 0);
        config.addDefault("Y2", 0);
        config.addDefault("Z2", 0);
    }

    @Override
    public void onDisable() {
        getLogger().info("HalozatiAlgoPlugin is disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("set1") || command.getName().equalsIgnoreCase("set2")) {
            if(sender instanceof Player) {
                int index = Integer.parseInt(command.getName().substring("set".length()));
                config.set("X" + index, ((Player) sender).getLocation().getBlock().getX());
                config.set("Y" + index, ((Player) sender).getLocation().getBlock().getY());
                config.set("Z" + index, ((Player) sender).getLocation().getBlock().getZ());
                this.saveConfig();
                sender.sendMessage("Saved coords for loc" + index);
                sender.sendMessage("Values: " + config.get("X" + index) + " " + config.get("Y" + index) + " " + config.get("Z" + index));
                return true;
            } else {
                sender.sendMessage(NamedTextColor.RED + "You must be a player to use this command!");
                return false;
            }
        }
        if(command.getName().equalsIgnoreCase("test")) {
            int x1 = config.getInt("X1");
            int x2 = config.getInt("X2");
            int y1 = config.getInt("Y1");
            int y2 = config.getInt("Y2");
            int z1 = config.getInt("Z1");
            int z2 = config.getInt("Z2");
            if(x1 > x2){
                int tmp = x2;
                x2 = x1;
                x1 = tmp;
            }
            if(y1 > y2){
                int tmp = y2;
                y2 = y1;
                y1 = tmp;
            }
            if(z1 > z2){
                int tmp = z2;
                z2 = z1;
                z1 = tmp;
            }
            World world = Bukkit.getWorld("world");
            //boolean[][][] results = new boolean[x2-x1+1][y2-y1+1][z2-z1+1];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{\"map\":");
            stringBuilder.append('[');
            int startX = 0;
            int startY = 0;
            int startZ = 0;
            for(int i=x1;i<=x2;i++){
                stringBuilder.append('[');
                for(int j=y1;j<=y2;j++){
                    stringBuilder.append('[');
                    for(int k=z1;k<=z2;k++){
                        //results[i-x1][j-y1][k-z1] = world.getBlockAt(i,j,k).getType() != Material.AIR;
                        stringBuilder.append(world.getBlockAt(i,j,k).getType() != Material.AIR ? "true" : "false");
                        if(world.getBlockAt(i,j,k).getType() == Material.DIAMOND_BLOCK){
                            startX = i-x1;
                            startY = j-y1;
                            startZ = k-z1;
                        }
                        if(k != z2) stringBuilder.append(',');
                    }
                    stringBuilder.append(']');
                    if(j != y2) stringBuilder.append(',');
                }
                stringBuilder.append(']');
                if(i != x2) stringBuilder.append(',');
            }
            stringBuilder.append(']');
            stringBuilder.append(",\"start\":{\"x\": ").append(startX).append(", \"y\": ").append(startY).append(", \"z\": ").append(startZ).append("}");
            stringBuilder.append("}");
            sender.sendMessage(stringBuilder.toString());
            sender.sendMessage(ChatColor.YELLOW + "Saving 3D model to file");
            try {
                this.getDataFolder().mkdir();
                File f = new File(this.getDataFolder(), "export.json");
                FileWriter myWriter = new FileWriter(f);
                myWriter.write(stringBuilder.toString());
                myWriter.close();
                sender.sendMessage(ChatColor.GREEN + "Saved 3D model to file");
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Could not save 3D model to file");
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
