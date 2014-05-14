package test;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import java.util.List;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class GetFleetMember implements EveryFrameCombatPlugin
{
    private static final boolean ENABLED = false;
    private float elapsedTime;
    private CombatEngineAPI engine;

    @Override
    public void advance(float amount, List<InputEventAPI> events)
    {
        if (!ENABLED)
        {
            return;
        }

        // Temp fix for .6.2a bug
        if (engine != Global.getCombatEngine())
        {
            return;
        }

        // Main menu check
        if (engine.isPaused() || engine.getPlayerShip() == null
                || !engine.isEntityInPlay(engine.getPlayerShip()))
        {
            return;
        }

        elapsedTime += amount;
        if (elapsedTime > 1f)
        {
            elapsedTime = 0f;
            System.out.println("\n=========\nAll fleetmembers:");
            //List<FleetMemberAPI> members = new ArrayList<>();
            for (ShipAPI ship : engine.getShips())
            {
                FleetMemberAPI tmp = CombatUtils.getFleetMember(ship);
                if (tmp == null)
                {
                    System.out.println("Null (" + ship.getVariant().getHullVariantId() + "(");
                }
                else
                {
                    //System.out.println(tmp);
                }
            }
            System.out.println("=========\n");

            System.out.println("Attempting spawn...");
            elapsedTime = -99999f;
            CombatUtils.spawnShipOrWingDirectly("onslaught_Elite", FleetMemberType.SHIP,
                    FleetSide.PLAYER, 9999f, Vector2f.add(engine.getPlayerShip().getLocation(),
                            new Vector2f(-1000f, 0f), null), 180f);
            CombatUtils.spawnShipOrWingDirectly("paragon_Elite", FleetMemberType.SHIP,
                    FleetSide.ENEMY, .6f, Vector2f.add(engine.getPlayerShip().getLocation(),
                            new Vector2f(1000f, 0f), null), 0f);
        }
    }

    @Override
    public void init(CombatEngineAPI engine)
    {
        this.engine = engine;
        elapsedTime = 0f;
    }
}
