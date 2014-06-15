package org.lazywizard.ellipticalshields;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.util.List;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

// TODO: Handle all ships on the battle map
// TODO: Replace existing shields
// TODO: Add shield toggle button
// TODO: Add public hooks to retrieve/modify the shields of specific ships
public class EllipticalShieldPlugin implements EveryFrameCombatPlugin
{
    private static final boolean ENABLED = false;

    @Override
    public void advance(float amount, List<InputEventAPI> events)
    {
        if (!ENABLED)
        {
            return;
        }

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

        // Set OpenGL flags
        glLoadIdentity();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 1, -1);

        // Advance shields (just the player for the early testing phase)
        EllipticalShield shield = new EllipticalShield(player);
        shield.advance(engine);
    }

    @Override
    public void init(CombatEngineAPI engine)
    {
    }
}
