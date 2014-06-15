package org.lazywizard.localmp.controllers.joypad;

import org.lwjgl.input.Controller;

class JoypadButtonHeldEvent extends BaseJoypadInputEvent
{
    private final Controller controller;
    private final long nanoTime;
    private final int buttonIndex;
    private boolean isConsumed = false;

    JoypadButtonHeldEvent(BaseJoypadInputEvent orig)
    {
        this.controller = orig.getController();
        this.nanoTime = orig.getEventNanoTime();
        this.buttonIndex = orig.getButton();
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
    public boolean isLZAxisEvent()
    {
        return false;
    }

    @Override
    public boolean isRZAxisEvent()
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
        return false;
    }

    @Override
    public boolean isButtonHeldEvent()
    {
        return true;
    }

    @Override
    public boolean isButtonUpEvent()
    {
        return false;
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
        return true;
    }
}
