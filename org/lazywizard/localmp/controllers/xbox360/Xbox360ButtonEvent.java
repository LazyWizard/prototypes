package org.lazywizard.localmp.controllers.xbox360;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

// TODO: Throw exception when acting on consumed events
public class Xbox360ButtonEvent extends Xbox360InputEvent
{
    private final Controller controller;
    private final long nanoTime;
    private final boolean isButtonEvent, buttonState;
    private final int buttonIndex;
    private boolean isConsumed = false;

    public Xbox360ButtonEvent()
    {
        this.controller = Controllers.getEventSource();
        this.nanoTime = Controllers.getEventNanoseconds();

        this.isButtonEvent = Controllers.isEventButton();
        this.buttonIndex = Controllers.getEventControlIndex();
        this.buttonState = Controllers.getEventButtonState();
    }

    @Override
    public Controller getController()
    {
        return controller;
    }

    @Override
    public long getEventNanoTime()
    {
        return nanoTime;
    }

    @Override
    public boolean isAxisEvent()
    {
        return false;
    }

    @Override
    public boolean isLAxisEvent()
    {
        return false;
    }

    @Override
    public boolean isRAxisEvent()
    {
        return false;
    }

    @Override
    public boolean isZAxisEvent()
    {
        return false;
    }

    @Override
    public boolean isDeadZoneEvent()
    {
        return false;
    }

    @Override
    public float getAxisX()
    {
        return 0f;
    }

    @Override
    public float getAxisY()
    {
        return 0f;
    }

    @Override
    public float getAxisZ()
    {
        return 0f;
    }

    @Override
    public boolean isDPadEvent()
    {
        return false;
    }

    @Override
    public float getDPadX()
    {
        return 0f;
    }

    @Override
    public float getDPadY()
    {
        return 0f;
    }

    @Override
    public boolean isButtonEvent()
    {
        return true;
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

    @Override
    public void consume()
    {
        isConsumed = true;
    }

    @Override
    public boolean isConsumed()
    {
        return isConsumed;
    }

    @Override
    boolean isValidEvent()
    {
        return isButtonEvent; //!isDeadZoneEvent && (isAxisEvent || isButtonEvent || isDPadEvent);
    }
}
