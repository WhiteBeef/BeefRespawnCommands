package ru.whitebeef.respawncommands;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
                commands.add(new Command(namespace, section.getString(namespace + ".command"),
                        section.getString(namespace + ".permission")));
            }
        }
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        BeefLibrary.jedisSet(this, player.getUniqueId().toString(), String.valueOf(System.currentTimeMillis()));
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event) {
        if (BeefLibrary.jedisGet(this, event.getPlayer().getUniqueId().toString()) == null) {
            return;
        }
        Player player = event.getPlayer();
        BeefLibrary.jedisDel(this, player.getUniqueId().toString());
        commands.stream().filter(command -> command.hasPermission(player)).forEach(command ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PAPIUtils.setPlaceholders(player, command.getCommand())));
    }

}
