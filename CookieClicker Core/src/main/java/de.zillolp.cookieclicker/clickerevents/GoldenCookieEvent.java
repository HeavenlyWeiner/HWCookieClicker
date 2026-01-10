package de.zillolp.cookieclicker.clickerevents;

import de.zillolp.cookieclicker.enums.ClickerEventType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GoldenCookieEvent extends ClickerEvent {
    private final Block clickedBlock;

    public GoldenCookieEvent(Player player, ClickerEventType clickerEventType, double time, Block clickedBlock) {
        super(player, clickerEventType, time);
        this.clickedBlock = clickedBlock;
    }

    public Block getClickedBlock() {
        return clickedBlock;
    }
}
