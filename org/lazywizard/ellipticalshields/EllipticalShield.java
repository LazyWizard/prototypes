package org.lazywizard.ellipticalshields;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import java.awt.Color;
import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.CollectionUtils.CollectionFilter;
import org.lazywizard.lazylib.opengl.DrawUtils;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

// TODO: Add shield state variables
// TODO: Add raise/lower animation
// TODO: Support shield arcs
// TODO: Add sound effects
// TODO: Block beams (doesn't seem possible at the moment)
// TODO: Bounce ships and asteroids away
// TODO: Implement flux usage stats
// TODO: Rewrite to use shield textures
class EllipticalShield
{
    final ShipAPI ship;
    final ShieldFilter shieldFilter = new ShieldFilter();
    final float width, height, widthSquared, heightSquared;
    final int numSegments;
    float cos, sin;

    EllipticalShield(ShipAPI ship)
    {
        this.ship = ship;

        // TODO: Calculate these automatically based on bounds and shield radius
        width = ship.getShield().getRadius();
        height = width * .6f;
        widthSquared = width * width;
        heightSquared = height * height;
        numSegments = 64;
    }

    void advance(CombatEngineAPI engine)
    {
        double adjustment = Math.toRadians(-ship.getFacing());
        cos = (float) Math.cos(adjustment);
        sin = (float) Math.sin(adjustment);

        for (DamagingProjectileAPI proj : CollectionUtils.filter(
                engine.getProjectiles(), shieldFilter))
        {
            // TEMPORARY
            engine.spawnExplosion(proj.getLocation(), new Vector2f(0f, 0f),
                    Color.RED, 5f, 1f);
            engine.removeEntity(proj);
        }

        render(engine.getViewport());
    }

    private void render(ViewportAPI view)
    {
        final float x = view.convertWorldXtoScreenX(ship.getLocation().x),
                y = view.convertWorldYtoScreenY(ship.getLocation().y),
                w = width / view.getViewMult(),
                h = height / view.getViewMult(),
                facing = ship.getFacing();

        // Draw inside of shield
        glColor4f(0f, 1f, 1f, .5f);
        DrawUtils.drawEllipse(x, y, w, h, facing, numSegments, true);

        // Draw outline of shield
        glColor4f(0f, 1f, 1f, .75f);
        DrawUtils.drawEllipse(x, y, w, h, facing, numSegments, false);
    }

    private class ShieldFilter implements CollectionFilter<DamagingProjectileAPI>
    {
        @Override
        public boolean accept(DamagingProjectileAPI proj)
        {
            // Exclude own projectiles/missiles and those outside of the collision radius
            if (proj.getSource() == ship) // || !MathUtils.isWithinRange(proj, ship, 0f))
            {
                return false;
            }

            // In-ellipse test
            Vector2f origin = Vector2f.sub(proj.getLocation(), ship.getLocation(), null);
            origin.set((origin.x * cos) - (origin.y * sin),
                    (origin.x * sin) + (origin.y * cos));
            final float x = (origin.x * origin.x) / widthSquared,
                    y = (origin.y * origin.y) / heightSquared;
            return ((x + y) <= 1.0001f);
        }
    }
}
