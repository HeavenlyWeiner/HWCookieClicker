package de.zillolp.cookieclicker.customparticles;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class MagicalWhirlwindEffect extends CustomParticleEffect {
    private final Particle particle1;
    private double t = 0;

    public MagicalWhirlwindEffect(CookieClicker plugin, Player player, Location location, boolean coloredDust, Particle particle, Particle particle1, Color color) {
        super(plugin, player, location, coloredDust, particle, color, 2);
        this.particle1 = particle1;
    }

    @Override
    public void sendEffect() {
        if (t > 4) {
            t = 0;
            stop();
            return;
        }

        double angle = t * 2;  // Geschwindigkeit der Rotation
        double radius = 1 + Math.sin(t * 0.5);  // Radius verändert sich mit der Zeit für einen dynamischen Effekt
        double y = Math.sin(t * 0.5) * 0.5;  // Vertikale Bewegung (auf und ab)

        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;

        sendParticle(location.clone().add(x, y, z), particle, 1, 0, 0, 0, 0);
        sendParticle(location.clone().add(-x, y, -z), particle, 1, 0, 0, 0, 0);

        sendParticle(location.clone().add(x * 0.5, y * 1.5, z * 0.5), particle1, 1, 0, 0, 0, 0);
        sendParticle(location.clone().add(-x * 0.5, y * 1.5, -z * 0.5), particle1, 1, 0, 0, 0, 0);

        t += 0.1;
    }
}
