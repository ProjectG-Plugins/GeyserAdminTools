package com.alysaa.geyseradmintools.commands;

import com.alysaa.geyseradmintools.Gat;
import com.alysaa.geyseradmintools.database.BanDatabaseSetup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class BanCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "The console cannot use this command");
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("gban") && player.hasPermission("geyseradmintools.gban")) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            String day = args[1];
            String time = LocalDate.now().plusDays(Long.parseLong(day)).toString();
            String reason = args[2];
            if (target == null) {
                player.sendMessage(ChatColor.RED + "[GeyserAdminTools] Could not find player!");
                player.sendMessage(ChatColor.RED + "[GeyserAdminTools] Perhaps wrong usage ? /gban <username> <amount of days> <reason>");
                return true;
            }
            try {
                String sql = "(UUID,REASON,USERNAME,ENDDATE) VALUES (?,?,?,?)";
                PreparedStatement insert = BanDatabaseSetup.getConnection().prepareStatement("INSERT INTO " + BanDatabaseSetup.Bantable
                        + sql);
                insert.setString(1, target.getUniqueId().toString());
                insert.setString(2, reason);
                insert.setString(3, target.getName());
                insert.setString(4, time);
                insert.executeUpdate();
                // Player inserted now
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            target.kickPlayer("you where banned for: " + reason);
            player.sendMessage("[GeyserAdminTools] Player " + target.getName() + " is banned");
            Gat.logger.info("Player " + player.getName() + " has banned " + target.getName() + " till: " + time + " for reason: " + reason);
            //end
        }
        return true;
    }
}
