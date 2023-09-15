package ru.whitebeef.respawncommands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.whitebeef.beeflibrary.BeefLibrary;
import ru.whitebeef.beeflibrary.placeholderapi.PAPIUtils;
import ru.whitebeef.beeflibrary.plugin.BeefPlugin;
import ru.whitebeef.beeflibrary.utils.ScheduleUtils;

import java.util.ArrayList;
import java.util.List;

public final class RespawnCommands extends BeefPlugin implements Listener {

    private final List<Command> commands = new ArrayList<>();
    private final List<String> firstJoinCommands = new ArrayList<>();


    public void reload() {
        super.reload();

        BeefLibrary.registerListeners(this, this);

        commands.clear();
        firstJoinCommands.clear();
        ConfigurationSection section = getConfig().getConfigurationSection("commands");
        for (String namespace : section.getKeys(false)) {
            if (section.getBoolean(namespace + ".enable")) {
                commands.add(new Command(namespace, section.getString(namespace + ".command"),
                        section.getString(namespace + ".permission")));
            }
        }
        firstJoinCommands.addAll(getConfig().getStringList("firstJoinCommands"));
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getRespawnReason() != PlayerRespawnEvent.RespawnReason.DEATH && event.getRespawnReason() != PlayerRespawnEvent.RespawnReason.PLUGIN) {
            return;
        }
        Player player = event.getPlayer();
        ScheduleUtils.runTaskLater(this, () -> commands.stream().filter(command -> command.hasPermission(player)).forEach(command ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PAPIUtils.setPlaceholders(player, command.getCommand()))), 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            firstJoinCommands.forEach(command ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PAPIUtils.setPlaceholders(player, command)));
        }

    }

}
