package org.lazywizard.localmp.controllers.joypad;

import org.lwjgl.input.Controller;

public interface JoypadInputEvent
{
    public Controller getController();
    public long getEventNanoTime();

    public boolean isAxisEvent();
    public boolean isLAxisEvent();
    public boolean isRAxisEvent();
    public boolean isLZAxisEvent();
    public boolean isRZAxisEvent();
    public boolean isDeadZoneEvent();
    public float getAxisX();
    public float getAxisY();
    public float getAxisZ();

    public boolean isDPadEvent();
    public float getDPadX();
    public float getDPadY();

    public boolean isButtonEvent();
    public boolean isButtonDownEvent();
    public boolean isButtonHeldEvent();
    public boolean isButtonUpEvent();
    public int getButton();

    public void consume();
    public boolean isConsumed();
}
