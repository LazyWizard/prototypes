package org.lazywizard.localmp.controllers.xbox360;

import org.lwjgl.input.Controller;

// Purpose of this class: LWJGL event system doesn't work with non-L axis,
// so we need to calculate axis events manually if we want the other 2 + dpad
// TODO: Throw exception when acting on consumed events
public class Xbox360AxisEvent extends Xbox360InputEvent
{
    private final Controller controller;
    private final AxisType type;
    private final long nanoTime;
    private final boolean isDeadZoneEvent;
    private final float axisX, axisY, axisZ, dpadX, dpadY;
    private boolean isConsumed = false;

    public enum AxisType
    {
        LAXIS,
        RAXIS,
        ZAXIS,
        DPAD
    }

    public Xbox360AxisEvent(Controller controller, AxisType axis)
    {
        this.controller = controller;
        this.nanoTime = System.nanoTime();
        this.type = axis;

        switch (axis)
        {
            case LAXIS:
                axisX = controller.getXAxisValue();
                axisY = controller.getYAxisValue();
                axisZ = 0f;
                isDeadZoneEvent = (Math.abs(axisX) <= controller.getXAxisDeadZone())
                        && (Math.abs(axisY) <= controller.getYAxisDeadZone());
                dpadX = 0;
                dpadY = 0;
                break;
            case RAXIS:
                axisX = controller.getRXAxisValue();
                axisY = controller.getRYAxisValue();
                axisZ = 0f;
                isDeadZoneEvent = (Math.abs(axisX) <= controller.getRXAxisDeadZone())
                        && (Math.abs(axisY) <= controller.getRYAxisDeadZone());
                dpadX = 0;
                dpadY = 0;
                break;
            case ZAXIS:
                axisX = 0f;
                axisY = 0f;
                axisZ = controller.getZAxisValue();
                dpadX = 0f;
                dpadY = 0f;
                isDeadZoneEvent = (Math.abs(axisZ) <= controller.getZAxisDeadZone());
                break;
            case DPAD:
                axisX = 0f;
                axisY = 0f;
                axisZ = 0f;
                dpadX = controller.getPovX();
                dpadY = controller.getPovY();
                isDeadZoneEvent = (dpadX == 0f) && (dpadY == 0f);
                break;
            default:
                axisX = 0f;
                axisY = 0f;
                axisZ = 0f;
                dpadX = 0f;
                dpadY = 0f;
                isDeadZoneEvent = true;
        }
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
        return !isDPadEvent();
    }

    @Override
    public boolean isLAxisEvent()
    {
        return (type == AxisType.LAXIS);
    }

    @Override
    public boolean isRAxisEvent()
    {
        return (type == AxisType.RAXIS);
    }

    @Override
    public boolean isZAxisEvent()
    {
        return (type == AxisType.ZAXIS);
    }

    @Override
    public boolean isDeadZoneEvent()
    {
        return isDeadZoneEvent;
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
    public float getAxisZ()
    {
        return axisZ;
    }

    @Override
    public boolean isDPadEvent()
    {
        return (type == AxisType.DPAD);
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
        return false;
    }

    @Override
    public boolean isButtonDownEvent()
    {
        return false;
    }

    @Override
    public boolean isButtonUpEvent()
    {
        return false;
    }

    @Override
    public int getButton()
    {
        return -1;
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
