package org.lazywizard.localmp.controllers.joypad;

import com.fs.starfarer.api.Global;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lazywizard.localmp.controllers.joypad.JoypadAxisEvent.AxisType;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class JoypadMaster
{
    private static Map<Controller, List<JoypadInputEvent>> allEvents;
    private static float lastPoll = -1f;

    public static Map<Controller, List<JoypadInputEvent>> pollEvents()
    {
        // Allow all EveryFrameCombatPlugins to get the same input list each frame
        if (Global.getCombatEngine() != null)
        {
            float curTime = Global.getCombatEngine().getTotalElapsedTime(true);
            if (lastPoll == curTime)
            {
                return allEvents;
            }

            lastPoll = curTime;
        }

        allEvents = new HashMap<>();

        Controllers.clearEvents();
        Controllers.poll();

        // Button event handling
        while (Controllers.next())
        {
            // The constructor handles gathering the event details
            BaseJoypadInputEvent tmp = new JoypadButtonEvent();
            if (!tmp.isValidEvent())
            {
                continue;
            }

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

            events.add(tmp);
        }

        // Axis event handling
        for (int x = 0; x < Controllers.getControllerCount(); x++)
        {
            Controller controller = Controllers.getController(x);

            List<JoypadInputEvent> events;
            if (!allEvents.containsKey(controller))
            {
                events = new ArrayList<>();
                allEvents.put(controller, events);
            }
            else
            {
                events = allEvents.get(controller);
            }

            // Axis event handling is terrible in LWJGL so we'll do it ourselves
            for (AxisType axis : AxisType.values())
            {
                BaseJoypadInputEvent tmp = new JoypadAxisEvent(controller, axis);
                if (tmp.isValidEvent())
                {
                    events.add(tmp);
                }
            }
        }

        return allEvents;
    }
}
