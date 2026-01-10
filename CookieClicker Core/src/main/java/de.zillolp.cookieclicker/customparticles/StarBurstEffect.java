package de.zillolp.cookieclicker.customparticles;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class StarBurstEffect extends CustomParticleEffect {
    private int step = 0;

    public StarBurstEffect(CookieClicker plugin, Player player, Location location, boolean coloredDust, Particle particle, Color color) {
        super(plugin, player, location, coloredDust, particle, color, 3);
    }

    @Override
    public void sendEffect() {
        if (step > 20) {
            step = 0;
            stop();
            return;
        }

        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72 + step * 15);
            double x = Math.cos(angle) * (0.5 + step * 0.2);
            double z = Math.sin(angle) * (0.5 + step * 0.2);
            double y = step * 0.3;

            sendParticle(location.clone().add(x, y, z), particle, 1, 0, 0.02f, 0, 0);
        }
        step++;
    }
}