package de.zillolp.cookieclicker.customparticles;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class CircleRingsEffect extends CustomParticleEffect {
    private int ring = 0;

    public CircleRingsEffect(CookieClicker plugin, Player player, Location location, boolean coloredDust, Particle particle, Color color) {
        super(plugin, player, location, coloredDust, particle, color, 5);
    }

    @Override
    public void sendEffect() {
        if (ring > 3) {
            ring = 0;
            stop();
            return;
        }
        for (double angle = 0; angle < Math.PI * 2; angle += 0.3) {
            double x = Math.cos(angle) * (0.6 + ring * 0.3);
            double z = Math.sin(angle) * (0.6 + ring * 0.3);
            double y = ring * 0.5;

            sendParticle(location.clone().add(x, y, z), particle, 1, 0, 0, 0, 0);
        }
        ring++;
    }
}
