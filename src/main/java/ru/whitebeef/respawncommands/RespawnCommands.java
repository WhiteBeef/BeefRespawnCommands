package ru.whitebeef.respawncommands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.whitebeef.beeflibrary.BeefLibrary;
import ru.whitebeef.beeflibrary.commands.AbstractCommand;
import ru.whitebeef.beeflibrary.commands.SimpleCommand;
import ru.whitebeef.beeflibrary.placeholderapi.PAPIUtils;
import ru.whitebeef.beeflibrary.utils.ScheduleUtils;

import java.util.ArrayList;
import java.util.List;

public final class RespawnCommands extends JavaPlugin implements Listener {

    private final List<Command> commands = new ArrayList<>();
    private final List<String> firstJoinCommands = new ArrayList<>();

    @Override
    public void onEnable() {
        BeefLibrary.loadConfig(this);

        BeefLibrary.registerListeners(this, this);

        AbstractCommand.builder("respawncommands", SimpleCommand.class)
                .setPermission("respawncommands")
                .setMinArgsCount(1)
                .addSubCommand(AbstractCommand.builder("reload", SimpleCommand.class)
                        .setOnCommand((sender, strings) -> reload())
                        .build())
                .build().register(this);

        reload();
    }

    public void reload() {
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
