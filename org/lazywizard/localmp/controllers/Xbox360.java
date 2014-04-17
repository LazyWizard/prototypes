package org.lazywizard.localmp.controllers;

import com.fs.starfarer.api.Global;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // Needs 10 buttons and 3 axis to be compatible
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

    public static Map<Controller, List<JoypadInputEvent>> pollEvents()
    {
        Map<Controller, List<JoypadInputEvent>> allEvents = new HashMap<>();

        Controllers.clearEvents();
        Controllers.poll();

        while (Controllers.next())
        {
            Controller source = Controllers.getEventSource();
            List<JoypadInputEvent> events;
            if (!allEvents.containsKey(source))
            {
                events = new ArrayList<>();
                allEvents.put(source, events);
            }
            else
            {
                events = allEvents.get(source);
            }

            // The constructor handles gathering the event details
            events.add(new Xbox360InputEvent());
        }

        return allEvents;
    }

    private Xbox360()
    {
    }

    private static class Xbox360InputEvent implements JoypadInputEvent
    {
        private final Controller controller;
        private final long nanoDuration;
        private final boolean isAxisEvent, isDPadEvent, isButtonEvent, buttonState;
        private final int axisIndex, buttonIndex;
        private final float axisX, axisY, dpadX, dpadY;

        private Xbox360InputEvent()
        {
            this.controller = Controllers.getEventSource();
            this.nanoDuration = Controllers.getEventNanoseconds();
            this.isAxisEvent = Controllers.isEventAxis();
            this.isDPadEvent = Controllers.isEventPovX() || Controllers.isEventPovY();
            this.isButtonEvent = Controllers.isEventButton();
            this.buttonState = Controllers.getEventButtonState();
            this.axisIndex = (isAxisEvent ? Controllers.getEventControlIndex() : -1);
            this.buttonIndex = (isButtonEvent ? Controllers.getEventControlIndex() : -1);
            this.axisX = (isAxisEvent ? Controllers.getEventXAxisValue() : 0f);
            this.axisY = (isAxisEvent ? Controllers.getEventYAxisValue() : 0f);
            this.dpadX = (isDPadEvent ? Controllers.getEventXAxisValue() : 0f);
            this.dpadY = (isDPadEvent ? Controllers.getEventYAxisValue() : 0f);
        }

        @Override
        public Controller getController()
        {
            return controller;
        }

        @Override
        public long getEventNanoDuration()
        {
            return nanoDuration;
        }

        @Override
        public boolean isAxisEvent()
        {
            return isAxisEvent;
        }

        @Override
        public int getAxisNumber()
        {
            return axisIndex;
        }

        @Override
        public float getAxisX()
        {
            return axisX;
        }

        @Override
        public float getAxisY()
        {
            return axisY;
        }

        @Override
        public boolean isDPadEvent()
        {
            return isDPadEvent;
        }

        @Override
        public float getDPadX()
        {
            return dpadX;
        }

        @Override
        public float getDPadY()
        {
            return dpadY;
        }

        @Override
        public boolean isButtonEvent()
        {
            return isButtonEvent;
        }

        @Override
        public boolean isButtonDownEvent()
        {
            return buttonState;
        }

        @Override
        public boolean isButtonUpEvent()
        {
            return !buttonState;
        }

        @Override
        public int getButton()
        {
            return buttonIndex;
        }
    }

    // Used to fine-tune button mappings and deadzones
    public static void main(String[] args)
    {
        try
        {
            Controllers.create();
            Controllers.clearEvents();
        }
        catch (LWJGLException ex)
        {
            System.out.println("Failed to create Controllers!");
            ex.printStackTrace();
            return;
        }

        Controller controller = findValid360Controller();
        if (controller != null)
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
                    if (Math.abs(controller.getAxisValue(x)) > controller.getDeadZone(x))
                    {
                        System.out.println("Axis: " + x + " )"
                                + controller.getAxisName(x) + ") = "
                                + controller.getAxisValue(x));
                    }
                }

                float povX = controller.getPovX(), povY = controller.getPovY();
                if (povX != 0f || povY != 0f)
                {
                    System.out.println("D-pad: " + povX + ", " + povY);
                }

                //System.out.println(controller.getPovX() + ", " + controller.getPovY());
                try
                {
                    Thread.sleep(1000l / 8l);
                }
                catch (InterruptedException ex)
                {
                }
            }
        }
    }
}
