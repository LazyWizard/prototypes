package test;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.util.List;
import org.lazywizard.lazylib.opengl.DrawUtils;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

public class EllipticalShields implements EveryFrameCombatPlugin
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
}
