package org.lazywizard.localmp.controllers;

import org.lwjgl.input.Controller;

public interface JoypadInputEvent
{
    public Controller getController();
    public long getEventNanoDuration();

    public boolean isAxisEvent();
    public int getAxisNumber();
    public float getAxisX();
    public float getAxisY();

    public boolean isDPadEvent();
    public float getDPadX();
    public float getDPadY();

    public boolean isButtonEvent();
    public boolean isButtonDownEvent();
    public boolean isButtonUpEvent();
    public int getButton();
}
