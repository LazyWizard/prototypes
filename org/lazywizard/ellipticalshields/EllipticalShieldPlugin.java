package org.lazywizard.ellipticalshields;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.CollectionUtils.CollectionFilter;
import org.lazywizard.lazylib.EllipseUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.opengl.DrawUtils;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

public class EllipticalShieldPlugin implements EveryFrameCombatPlugin
{
    @Override
    public void advance(float amount, List<InputEventAPI> events)
    {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null || engine.isPaused())
        {
            return;
        }

        ShipAPI player = engine.getPlayerShip();
        if (player == null || player.getShield() == null
                || !engine.isEntityInPlay(player))
        {
            return;
        }

        for (DamagingProjectileAPI proj : CollectionUtils.filter(
                engine.getProjectiles(), new EllipticalShieldFilter(player)))
        {
            engine.spawnExplosion(proj.getLocation(), new Vector2f(0f, 0f),
                    Color.RED, 5f, 1f);
            engine.removeEntity(proj);
        }

        Vector2f loc = player.getLocation();
        ViewportAPI view = engine.getViewport();
        float x = view.convertWorldXtoScreenX(loc.x);
        float y = view.convertWorldYtoScreenY(loc.y);
        float radius = player.getShield().getRadius() / view.getViewMult();
        float facing = player.getFacing();

        glLoadIdentity();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 1, -1);
        glColor4f(0f, 1f, 1f, .5f);
        DrawUtils.drawEllipse(x, y, radius, radius * .6f, facing, 64, true);
        glColor4f(0f, 1f, 1f, .75f);
        DrawUtils.drawEllipse(x, y, radius, radius * .6f, facing, 64, false);
    }

    @Override
    public void init(CombatEngineAPI engine)
    {
    }

    private class EllipticalShieldFilter implements CollectionFilter<DamagingProjectileAPI>
    {
        final ShipAPI ship;
        final float height, width, cos, sin;

        EllipticalShieldFilter(ShipAPI ship)
        {
            this.ship = ship;
            width = ship.getShield().getRadius();
            height = width * .6f;
            double adjustment = Math.toRadians(-ship.getFacing());
            cos = (float) Math.cos(adjustment);
            sin = (float) Math.sin(adjustment);
        }

        @Override
        public boolean accept(DamagingProjectileAPI proj)
        {
            // Exclude own projectiles/missiles and those outside of the collision radius
            if (proj.getSource() == ship || !MathUtils.isWithinRange(proj, ship, 0f))
            {
                return false;
            }

            // In-ellipse test
            Vector2f origin = Vector2f.sub(proj.getLocation(), ship.getLocation(), null);
            origin.set((origin.x * cos) - (origin.y * sin),
                    (origin.x * sin) + (origin.y * cos));
            final float x = (origin.x * origin.x) / (width * width),
                    y = (origin.y * origin.y) / (height * height);
            return ((x + y) <= 1.0001f);
        }
    }
}
