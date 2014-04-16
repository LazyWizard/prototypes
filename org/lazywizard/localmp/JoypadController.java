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
import org.lazywizard.localmp.buttonmaps.Xbox360;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

// TODO: Rewrite to use new (LWJGL 2.9.1) input events system in Controllers
// TODO: Add support for d-pad (controller.getPOVX/Y())
public class JoypadController implements ShipAIPlugin
{
    private static final Map<Integer, Enum> BINDINGS = new HashMap<>();
    private static boolean USE_VIRTUAL_MOUSE = false;
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
        BINDINGS.put(Xbox360.BUTTON_LB, ExtendedShipCommand.SELECT_GROUP_PREVIOUS);
        BINDINGS.put(Xbox360.BUTTON_RB, ExtendedShipCommand.SELECT_GROUP_NEXT);
        BINDINGS.put(Xbox360.BUTTON_BACK, ExtendedShipCommand.TOGGLE_VIRTUAL_MOUSE);
        //BINDINGS.put(Xbox360.BUTTON_LB, ShipCommand.TURN_LEFT);
        //BINDINGS.put(Xbox360.BUTTON_RB, ShipCommand.TURN_RIGHT);
        BINDINGS.put(Xbox360.BUTTON_LSTICK, ShipCommand.DECELERATE);
        BINDINGS.put(Xbox360.BUTTON_RSTICK, ExtendedShipCommand.RESET_VIRTUAL_MOUSE);
    }

    private static Controller findValidController()
    {
        if (!Controllers.isCreated())
        {
            try
            {
                Global.getLogger(JoypadController.class).log(Level.INFO,
                        "Initializing controllers...");
                Controllers.create();
            }
            catch (LWJGLException ex)
            {
                throw new RuntimeException("Failed to initiate controllers!", ex);
            }
        }

        // Search for Xbox 360 controllers
        // TODO: Extend this to all valid joypad controllers
        // Needs: 10 buttons and 3 axis to be compatible
        for (int x = 0; x < Controllers.getControllerCount(); x++)
        {
            Controller tmp = Controllers.getController(x);
            if (tmp.getName().contains("360"))
            {
                Global.getLogger(JoypadController.class).log(Level.INFO,
                        "Found valid controller: " + tmp.getName());
                return tmp;
            }
        }

        // No controller found
        Global.getLogger(JoypadController.class).log(Level.ERROR,
                "Failed to find a compatible controller!");
        return null;
    }

    public JoypadController(ShipAPI ship)
    {
        this.ship = ship;
        mouseLoc = new Vector2f(ship.getLocation());
        numWeaponGroups = ship.getVariant().getWeaponGroups().size();
        maxMouseDistance = ship.getCollisionRadius() + 500f;
        controller = findValidController();

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
                    Global.getLogger(JoypadController.class).log(Level.ERROR,
                            "Support for command " + command.toString()
                            + " is not implemented yet.");
            }
        }
        else
        {
            Global.getLogger(JoypadController.class).log(Level.ERROR,
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

        // Update camera to focus on both ships
        Vector2f shipLoc = ship.getLocation();
        ViewportAPI view = engine.getViewport();
        Vector2f cameraLoc = (ship == engine.getPlayerShip() ? shipLoc
                : MathUtils.getMidpoint(shipLoc, engine.getPlayerShip().getLocation()));
        int camX = (int) view.convertWorldXtoScreenX(cameraLoc.x),
                camY = (int) view.convertWorldYtoScreenY(cameraLoc.y);
        //System.out.println("Camera loc: " + cameraLoc);
        //System.out.println("New mouse coords: " + camX + ", " + camY);
        Mouse.setCursorPosition(camX, camY);

        // Update controller input
        controller.poll();

        // Axis handling, Y-axis will be flipped
        float lx = controller.getXAxisValue(),
                ly = -controller.getYAxisValue(),
                rx = controller.getRXAxisValue(),
                ry = -controller.getRYAxisValue();
        if (Math.abs(lx) > controller.getXAxisDeadZone())
        {
            ship.giveCommand((lx > 0 ? ShipCommand.TURN_RIGHT
                    : ShipCommand.TURN_LEFT), mouseLoc, 0);
        }
        if (Math.abs(ly) > controller.getYAxisDeadZone())
        {
            ship.giveCommand((ly > 0 ? ShipCommand.ACCELERATE
                    : ShipCommand.ACCELERATE_BACKWARDS), mouseLoc, 0);
        }

        if (USE_VIRTUAL_MOUSE)
        {
            // Keep virtual mouse centered around ship
            mouseLoc.x += ship.getVelocity().x * amount;
            mouseLoc.y += ship.getVelocity().y * amount;

            // Virtual mouse pointer controls
            if (Math.abs(rx) > controller.getRXAxisDeadZone())
            {
                mouseLoc.x += rx * 250f * amount;
            }
            if (Math.abs(ry) > controller.getRYAxisDeadZone())
            {
                mouseLoc.y += ry * 250f * amount;
            }
        }
        else
        {
            // FIXME: This isn't actually a circle!
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
