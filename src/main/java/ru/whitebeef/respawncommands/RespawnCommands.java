package ru.whitebeef.respawncommands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.whitebeef.beeflibrary.BeefLibrary;
import ru.whitebeef.beeflibrary.commands.AbstractCommand;
import ru.whitebeef.beeflibrary.commands.SimpleCommand;
import ru.whitebeef.beeflibrary.placeholderapi.PAPIUtils;

import java.util.ArrayList;
import java.util.List;

public final class RespawnCommands extends JavaPlugin implements Listener {

    private final List<Command> commands = new ArrayList<>();

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
        ConfigurationSection section = getConfig().getConfigurationSection("commands");
        for (String namespace : section.getKeys(false)) {
            if (section.getBoolean(namespace + ".enable")) {
                commands.add(new Command(namespace, section.getString(namespace + "command"), section.getString(namespace + "permission")));
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getRespawnReason() != PlayerRespawnEvent.RespawnReason.DEATH) {
            return;
        }
        Player player = event.getPlayer();
        commands.stream().filter(command -> player.hasPermission(command.getPermission())).forEach(command ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PAPIUtils.setPlaceholders(player, command.getCommand())));
    }

}
