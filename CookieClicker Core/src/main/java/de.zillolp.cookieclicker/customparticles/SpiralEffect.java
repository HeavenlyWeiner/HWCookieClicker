package de.zillolp.cookieclicker.customparticles;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class SpiralEffect extends CustomParticleEffect {
    private double t = 0;

    public SpiralEffect(CookieClicker plugin, Player player, Location location, boolean coloredDust, Particle particle, Color color) {
        super(plugin, player, location, coloredDust, particle, color, 1);
    }

    @Override
    public void sendEffect() {
        if (t > 36) {
            t = 0;
            stop();
            return;
        }

        double x = 0.7 * Math.cos(t);
        double y = t * 0.1;
        double z = 0.7 * Math.sin(t);

        sendParticle(location.clone().add(x, y, z), particle, 0, 0, 0, 0, 0);
        t += 0.2;
    }
}
