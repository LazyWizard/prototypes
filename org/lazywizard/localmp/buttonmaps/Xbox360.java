package org.lazywizard.localmp.buttonmaps;

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

    private Xbox360()
    {
    }

    public static void main(String[] args)
    {
        try
        {
            Controllers.create();
        }
        catch (LWJGLException ex)
        {
            System.out.println("Failed to create Controllers!");
            ex.printStackTrace();
            return;
        }

        if (Controllers.getControllerCount() == 0)
        {
            System.out.println("No controllers found!");
            return;
        }

        Controller controller = null;
        boolean foundController = false;
        for (int x = 0; x < Controllers.getControllerCount(); x++)
        {
            controller = Controllers.getController(x);
            String name = controller.getName();
            System.out.println("Controller " + x + ": " + name);
            if (name.contains("360"))
            {
                foundController = true;
                break;
            }
        }

        if (foundController)
        {
            System.out.println("\nFound Xbox 360 controller: "
                    + controller.getName());
            while (true)
            {
                controller.poll();

                // Detect button presses
                for (int x = 0; x < controller.getButtonCount(); x++)
                {
                    if (controller.isButtonPressed(x))
                    {
                        System.out.println("Button down: " + x + " )"
                                + controller.getButtonName(x) + ")");
                    }
                }

                // Detect axis usage
                for (int x = 0; x < controller.getAxisCount(); x++)
                {
                    if (controller.getAxisValue(x) > controller.getDeadZone(x))
                    {
                        System.out.println("Axis: " + x + " )"
                                + controller.getAxisName(x) + ") = "
                                + controller.getAxisValue(x));
                    }
                }

                //System.out.println(controller.getPovX() + ", " + controller.getPovY());

                try
                {
                    Thread.sleep(1000l / 4l);
                }
                catch (InterruptedException ex)
                {
                }
            }
        }
    }
}
