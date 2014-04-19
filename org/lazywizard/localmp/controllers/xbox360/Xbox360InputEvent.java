package org.lazywizard.localmp.controllers.xbox360;

import org.lazywizard.localmp.controllers.JoypadInputEvent;

public abstract class Xbox360InputEvent implements JoypadInputEvent
{
    abstract boolean isValidEvent();

    @Override
    public String toString()
    {
        StringBuilder toString = new StringBuilder();
        if (this.isAxisEvent())
        {
            if (this.isLAxisEvent())
            {
                toString.append("LAxis: ");
            }
            if (this.isRAxisEvent())
            {
                toString.append("RAxis: ");
            }
            if (this.isLZAxisEvent())
            {
                toString.append("LZAxis: ");
            }
            if (this.isRZAxisEvent())
            {
                toString.append("RZAxis: ");
            }

            toString.append(this.getAxisX()).append(", ").append(this.getAxisY())
                    .append(", ").append(this.getAxisZ()).append("\n")
                    .append("Deadzone: ").append(this.isDeadZoneEvent()).append("\n");
        }

        if (this.isDPadEvent())
        {
            toString.append("DPad ").append(this.getDPadX()).append(", ").append(this.getDPadY()).append("\n");
        }

        if (this.isButtonEvent())
        {
            toString.append("Button ").append(this.getButton()).append(this.isButtonDownEvent() ? " down" : " up").append("\n");
        }

        toString.append("Nanotime: ").append(this.getEventNanoTime()).append("\n")
                .append("Consumed: ").append(this.isConsumed());

        return toString.toString();
    }
}
