package org.lazywizard.localmp.controllers.joypad.buttonmaps;

import com.fs.starfarer.api.Global;
import org.apache.log4j.Level;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class Xbox360
{
    public static final int BUTTON_A = 0;
    public static final int BUTTON_B = 1;
    public static final int BUTTON_X = 2;
    public static final int BUTTON_Y = 3;
    public static final int BUTTON_LB = 4;
    public static final int BUTTON_RB = 5;
    public static final int BUTTON_BACK = 6;
    public static final int BUTTON_START = 7;
    public static final int BUTTON_LSTICK = 8;
    public static final int BUTTON_RSTICK = 9;

    public static final int AXIS_LEFT_Y = 0;
    public static final int AXIS_LEFT_X = 1;
    public static final int AXIS_RIGHT_Y = 2;
    public static final int AXIS_RIGHT_X = 3;
    public static final int AXIS_TRIGGER = 4;

    public static Controller findValid360Controller()
    {
        if (!Controllers.isCreated())
        {
            try
            {
                Global.getLogger(Xbox360.class).log(Level.INFO,
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
        // Needs 10 buttons and 3 axes to be compatible
        for (int x = 0; x < Controllers.getControllerCount(); x++)
        {
            Controller tmp = Controllers.getController(x);
            if (tmp.getName().contains("360"))
            {
                Global.getLogger(Xbox360.class).log(Level.INFO,
                        "Found valid 360 controller: " + tmp.getName());

                // Set controller deadzones
                for (int y = 0; y < tmp.getAxisCount(); y++)
                {
                    tmp.setDeadZone(y, .1f);
                }

                return tmp;
            }
        }

        // No controller found
        Global.getLogger(Xbox360.class).log(Level.ERROR,
                "Failed to find a compatible 360 controller!");
        return null;
    }

    private Xbox360()
    {
    }
}
