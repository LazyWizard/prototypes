package org.lazywizard.advancedweapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

// All code in here is a temporary test
public class GrapplingHookPlugin implements EveryFrameCombatPlugin
{
    private static final float GRAPPLE_ROPE_RANGE = 1000;
    private static final float GRAPPLE_FORCE = 1000f;
    private static final float GRAPPLE_BREAK_POINT = 1.5f;

    private static void advanceGrapple(float amount, ShipAPI player, ShipAPI target)
    {
        final float distance = MathUtils.getDistance(player, target),
                pullMod = (distance / GRAPPLE_ROPE_RANGE);

        // TODO: Scale force by current velocity towards target (don't overpull)
        if (pullMod <= 1f)
        {
            return;
        }

        if (pullMod >= GRAPPLE_BREAK_POINT)
        {
            System.out.println("Grapple would have broken now!");
        }

        final float playerMass = player.getMass(),
                targetMass = target.getMass(),
                totalMass = playerMass + targetMass;
        final float pullStrengthPlayer = targetMass / totalMass,
                pullStrengthTarget = playerMass / totalMass;
        final float velChangePlayer = GRAPPLE_FORCE * pullStrengthPlayer * pullMod * amount,
                velChangeTarget = GRAPPLE_FORCE * pullStrengthTarget * pullMod * amount;

        System.out.println("Distance: " + distance
                + "\nPull strength mod: " + pullMod
                +"\nTime since last frame: " + amount
                + "\nRelative pull strength (player): " + pullStrengthPlayer
                + "\nRelative pull strength (target): " + pullStrengthTarget
                + "\nVelocity change (player): " + velChangePlayer
                + "\nVelocity change (target): " + velChangeTarget);

        // Implement player velocity change
        Vector2f tmp = VectorUtils.getDirectionalVector(
                player.getLocation(), target.getLocation());
        tmp.scale(velChangePlayer);
        player.getVelocity().translate(tmp.x, tmp.y);

        // Implement target velocity change
        tmp.set(-tmp.x, -tmp.y);
        tmp.normalise();
        tmp.scale(velChangeTarget);
        target.getVelocity().translate(tmp.x, tmp.y);
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events)
    {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null || engine.isPaused())
        {
            return;
        }

        ShipAPI player = engine.getPlayerShip();
        if (player == null || !engine.isEntityInPlay(player))
        {
            return;
        }

        ShipAPI target = player.getShipTarget();
        if (target == null || !engine.isEntityInPlay(target))
        {
            return;
        }

        advanceGrapple(amount, player, target);
    }

    @Override
    public void init(CombatEngineAPI engine)
    {
    }

    public static void main(String[] args)
    {
        FakeShip player = new FakeShip(new Vector2f(0f, -1500f), 0f, 500f);
        FakeShip enemy = new FakeShip(new Vector2f(0f, 0f), 0f, 1500f);
        advanceGrapple(1f, player, enemy);
    }
}
