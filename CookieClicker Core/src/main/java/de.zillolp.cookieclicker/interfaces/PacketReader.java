package de.zillolp.cookieclicker.interfaces;

import org.bukkit.entity.Player;

public interface PacketReader {

    void inject(Player player);

    void unInject(Player player);
}
