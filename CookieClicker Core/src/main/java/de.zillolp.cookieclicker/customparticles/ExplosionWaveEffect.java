package de.zillolp.cookieclicker.customparticles;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ExplosionWaveEffect extends CustomParticleEffect {
    private double radius = 0.5;

    public ExplosionWaveEffect(CookieClicker plugin, Player player, Location location, boolean coloredDust, Particle particle, Color color) {
        super(plugin, player, location, coloredDust, particle, color, 3);
    }

    @Override
    public void sendEffect() {
        if (radius > 2) {
            radius = 0.5;
            stop();
            return;
        }
        for (double angle = 0; angle < Math.PI * 2; angle += 0.3) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            sendParticle(location.clone().add(x, 0, z), particle, 1, 0, 0, 0, 0);
        }
        radius += 0.2;
    }
}
