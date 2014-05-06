package org.lazywizard.localmp.controllers.joypad;

import org.lwjgl.input.Controller;

// Purpose of this class: LWJGL event system doesn't work with non-L axis,
// so we need to calculate axis events manually if we want the R/LZ/RZ/dpad
// TODO: Throw exception when acting on consumed events
class JoypadAxisEvent extends BaseJoypadInputEvent
{
    private final Controller controller;
    private final AxisType type;
    private final long nanoTime;
    private final boolean isDeadZoneEvent;
    private final float axisX, axisY, axisZ, dpadX, dpadY;
    private final boolean isValid;
    private boolean isConsumed = false;

    enum AxisType
    {
        LAXIS,
        RAXIS,
        LZAXIS,
        RZAXIS,
        DPAD
    }

    JoypadAxisEvent(Controller controller, AxisType axis)
    {
        this.controller = controller;
        this.nanoTime = System.nanoTime();
        this.type = axis;

        switch (axis)
        {
            case LAXIS:
                isValid = controller.getAxisCount() >= 1;
                axisX = controller.getXAxisValue();
                axisY = controller.getYAxisValue();
                axisZ = 0f;
                isDeadZoneEvent = (Math.abs(axisX) <= controller.getXAxisDeadZone())
                        && (Math.abs(axisY) <= controller.getYAxisDeadZone());
                dpadX = 0;
                dpadY = 0;
                break;
            case RAXIS:
                isValid = controller.getAxisCount() >= 3;
                axisX = controller.getRXAxisValue();
                axisY = controller.getRYAxisValue();
                axisZ = 0f;
                isDeadZoneEvent = (Math.abs(axisX) <= controller.getRXAxisDeadZone())
                        && (Math.abs(axisY) <= controller.getRYAxisDeadZone());
                dpadX = 0;
                dpadY = 0;
                break;
            case LZAXIS:
                isValid = controller.getAxisCount() >= 4;
                axisX = 0f;
                axisY = 0f;
                axisZ = controller.getZAxisValue();
                dpadX = 0f;
                dpadY = 0f;
                isDeadZoneEvent = (Math.abs(axisZ) <= controller.getZAxisDeadZone());
                break;
            case RZAXIS:
                isValid = controller.getAxisCount() >= 5;
                axisX = 0f;
                axisY = 0f;
                axisZ = controller.getRZAxisValue();
                dpadX = 0f;
                dpadY = 0f;
                isDeadZoneEvent = (Math.abs(axisZ) <= controller.getRZAxisDeadZone());
                break;
            case DPAD:
                isValid = true;
                axisX = 0f;
                axisY = 0f;
                axisZ = 0f;
                dpadX = controller.getPovX();
                dpadY = controller.getPovY();
                isDeadZoneEvent = (dpadX == 0f) && (dpadY == 0f);
                break;
            default:
                isValid = false;
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
    public boolean isLZAxisEvent()
    {
        return (type == AxisType.LZAXIS);
    }

    @Override
    public boolean isRZAxisEvent()
    {
        return (type == AxisType.RZAXIS);
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
        return isValid;
    }
}
