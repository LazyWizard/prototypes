package org.lazywizard.localmp;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAIPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ViewportAPI;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.localmp.controllers.xbox360.Xbox360;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

// TODO: Rewrite to use new (LWJGL 2.9.1) input events system in Controllers
// TODO: Add support for custom bindings
// TODO: Use button down/up events instead of per-frame isButtonDown() checks
// TODO: Add support for d-pad (controller.getPOVX/Y())
public class JoypadControls implements ShipAIPlugin
{
    private static final float VIRTUAL_POINTER_SPEED = 500f;
    private static final boolean USE_TANK_CONTROLS = false;
    private static boolean USE_VIRTUAL_MOUSE = false;
    private static final Map<Integer, Enum> BINDINGS = new HashMap<>();
    private final ShipAPI ship;
    private final Controller controller;
    private final Vector2f mouseLoc;
    private final float maxMouseDistance;
    private final int numWeaponGroups;
    private int currentWeaponGroup = 0;

    static
    {
        BINDINGS.put(Xbox360.BUTTON_A, ShipCommand.USE_SELECTED_GROUP);
        BINDINGS.put(Xbox360.BUTTON_B, ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK);
        BINDINGS.put(Xbox360.BUTTON_X, ShipCommand.VENT_FLUX);
        BINDINGS.put(Xbox360.BUTTON_Y, ShipCommand.USE_SYSTEM);
        BINDINGS.put(Xbox360.BUTTON_LB, ShipCommand.STRAFE_LEFT);
        BINDINGS.put(Xbox360.BUTTON_RB, ShipCommand.STRAFE_RIGHT);
        //BINDINGS.put(Xbox360.BUTTON_LB, ExtendedShipCommand.SELECT_GROUP_PREVIOUS);
        //BINDINGS.put(Xbox360.BUTTON_RB, ExtendedShipCommand.SELECT_GROUP_NEXT);
        BINDINGS.put(Xbox360.BUTTON_BACK, ExtendedShipCommand.TOGGLE_VIRTUAL_MOUSE);
        //BINDINGS.put(Xbox360.BUTTON_LB, ShipCommand.TURN_LEFT);
        //BINDINGS.put(Xbox360.BUTTON_RB, ShipCommand.TURN_RIGHT);
        BINDINGS.put(Xbox360.BUTTON_LSTICK, ShipCommand.DECELERATE);
        BINDINGS.put(Xbox360.BUTTON_RSTICK, ExtendedShipCommand.RESET_VIRTUAL_MOUSE);
    }

    public JoypadControls(ShipAPI ship)
    {
        this.ship = ship;
        mouseLoc = new Vector2f(ship.getLocation());
        numWeaponGroups = ship.getVariant().getWeaponGroups().size();
        maxMouseDistance = ship.getCollisionRadius() + 500f;
        controller = Xbox360.findValid360Controller();

        if (controller != null)
        {
            for (int x = 0; x < numWeaponGroups; x++)
            {
                if (ship.getVariant().getWeaponGroups().get(x).isAutofireOnByDefault())
                {
                    ship.giveCommand(ShipCommand.TOGGLE_AUTOFIRE, mouseLoc, x);
                }
            }
        }
    }

    private void runCommand(Enum command)
    {
        if (command instanceof ShipCommand)
        {
            ship.giveCommand((ShipCommand) command, mouseLoc, currentWeaponGroup);
        }
        else if (command instanceof ExtendedShipCommand)
        {
            switch ((ExtendedShipCommand) command)
            {
                case TOGGLE_VIRTUAL_MOUSE:
                    USE_VIRTUAL_MOUSE = !USE_VIRTUAL_MOUSE;
                    break;
                case RESET_VIRTUAL_MOUSE:
                    mouseLoc.set(ship.getLocation());
                    break;
                case SELECT_GROUP_NEXT:
                    currentWeaponGroup++;
                    if (currentWeaponGroup >= numWeaponGroups)
                    {
                        currentWeaponGroup = 0;
                    }

                    System.out.println("Current group: " + currentWeaponGroup);
                    ship.giveCommand(ShipCommand.SELECT_GROUP, mouseLoc, currentWeaponGroup);
                    break;
                case SELECT_GROUP_PREVIOUS:
                    currentWeaponGroup--;
                    if (currentWeaponGroup < 0)
                    {
                        currentWeaponGroup = numWeaponGroups;
                    }
                    System.out.println("Current group: " + currentWeaponGroup);
                    ship.giveCommand(ShipCommand.SELECT_GROUP, mouseLoc, currentWeaponGroup);
                    break;
                default:
                    Global.getLogger(JoypadControls.class).log(Level.ERROR,
                            "Support for command " + command.toString()
                            + " is not implemented yet.");
            }
        }
        else
        {
            Global.getLogger(JoypadControls.class).log(Level.ERROR,
                    "Unsommorted command type " + command.getClass().getSimpleName()
                    + ":" + command.toString() + ".");
        }
    }

    @Override
    public void advance(float amount)
    {
        CombatEngineAPI engine = Global.getCombatEngine();

        if (engine == null || controller == null || ship == null
                || !engine.isEntityInPlay(ship))
        {
            return;
        }

        boolean isPlayerOne = (ship == engine.getPlayerShip());

        // Update camera to focus on both ships
        Vector2f shipLoc = ship.getLocation();
        ViewportAPI view = engine.getViewport();
        Vector2f cameraLoc = (isPlayerOne ? shipLoc
                : MathUtils.getMidpoint(shipLoc, engine.getPlayerShip().getLocation()));
        float camX = view.convertWorldXtoScreenX(cameraLoc.x),
                camY = view.convertWorldYtoScreenY(cameraLoc.y);
        // TODO: Find way to set camera pos/zoom level directly (absolutely required for two-player!)
        if (isPlayerOne)
        {
            // Keep mouse centered on player
            Mouse.setCursorPosition((int) camX, (int) camY);
        }
        else
        {
            // Ensure stats are always displayed for the second player
            engine.getPlayerShip().setShipTarget(ship);
        }

        // Update controller input
        controller.poll();

        // Axis handling, Y-axis will be flipped
        float lx = controller.getXAxisValue(),
                ly = -controller.getYAxisValue(),
                rx = controller.getRXAxisValue(),
                ry = -controller.getRYAxisValue();
        // Tank controls, extremely awkward but supported
        if (USE_TANK_CONTROLS)
        {
            if (Math.abs(lx) > controller.getXAxisDeadZone())
            {
                runCommand((lx > 0 ? ShipCommand.TURN_RIGHT : ShipCommand.TURN_LEFT));
            }
            if (Math.abs(ly) > controller.getYAxisDeadZone())
            {
                runCommand((ly > 0 ? ShipCommand.ACCELERATE : ShipCommand.ACCELERATE_BACKWARDS));
            }
        }
        // Go towards point style controls
        else
        {
            Vector2f lAxis = new Vector2f(lx, ly);
            float intendedFacing = VectorUtils.getFacing(lAxis);

            // Turn towards point
            if (Math.abs(lx) > controller.getXAxisDeadZone()
                    || Math.abs(ly) > controller.getYAxisDeadZone())
            {
                // The difference in degrees between current facing and intended facing
                float turnAmount = MathUtils.getShortestRotation(ship.getFacing(),
                        intendedFacing);

                // Stop turning if turn velocity is more than remaining turn
                // TODO: This is written wrong, fix it
                if (Math.abs(turnAmount) > ship.getAngularVelocity())
                {
                    runCommand(turnAmount > 0f ? ShipCommand.TURN_LEFT : ShipCommand.TURN_RIGHT);
                }
            }

            float currentDrift = VectorUtils.getFacing(ship.getVelocity());
            // Not heading in the same direction as our facing
            if (Math.abs(MathUtils.getShortestRotation(currentDrift, intendedFacing))
                    > 90f)
            {
            }

            // TODO: Accellerate towards facing
            // Accelerate towards point (based on how far the stick is pushed)
            float accel = Math.min(1f, lAxis.length() * 1.1f);
            float curAccel = ship.getVelocity().length()
                    / ship.getMutableStats().getMaxSpeed().getModifiedValue();
            if (!MathUtils.equals(accel, curAccel))
            {
                runCommand(accel > curAccel ? ShipCommand.ACCELERATE : ShipCommand.DECELERATE);
            }
        }

        if (USE_VIRTUAL_MOUSE)
        {
            // Keep virtual mouse centered around ship
            mouseLoc.x += ship.getVelocity().x * amount;
            mouseLoc.y += ship.getVelocity().y * amount;

            // Virtual mouse pointer controls
            if (Math.abs(rx) > controller.getRXAxisDeadZone())
            {
                mouseLoc.x += rx * VIRTUAL_POINTER_SPEED * amount;
            }
            if (Math.abs(ry) > controller.getRYAxisDeadZone())
            {
                mouseLoc.y += ry * VIRTUAL_POINTER_SPEED * amount;
            }
        }
        else
        {
            mouseLoc.set(shipLoc.x + (rx * maxMouseDistance),
                    shipLoc.y + (ry * maxMouseDistance));
        }

        // Keep virtual mouse within a certain range of the ship
        if (!MathUtils.isWithinRange(mouseLoc, shipLoc, maxMouseDistance))
        {
            Vector2f tmp = Vector2f.sub(mouseLoc, shipLoc, null);
            tmp.normalise();
            tmp.scale(maxMouseDistance);
            tmp.set(Vector2f.add(tmp, shipLoc, null));
            mouseLoc.set(tmp);
        }

        // Show virtual mouse location on the screen
        ship.getMouseTarget().set(mouseLoc);
        engine.addSmoothParticle(mouseLoc, ship.getVelocity(),
                5f * view.getViewMult(), 1f, .25f, Color.CYAN);

        // Button handling
        for (Map.Entry<Integer, Enum> binding : BINDINGS.entrySet())
        {
            if (controller.isButtonPressed(binding.getKey()))
            {
                runCommand(binding.getValue());
            }
        }
    }

    @Override
    public void setDoNotFireDelay(float amount)
    {
    }

    @Override
    public void forceCircumstanceEvaluation()
    {
    }

    @Override
    public boolean needsRefit()
    {
        return false;
    }
}
