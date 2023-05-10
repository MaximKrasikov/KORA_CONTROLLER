package rimp.rild.com.android.android_joystick_controler_test.entity;

import android.view.InputDevice;
import android.view.MotionEvent;

public class Joystick {
    final static int UP       = 0;
    final static int LEFT     = 1;
    final static int RIGHT    = 2;
    final static int DOWN     = 3;
    final static int CENTER   = 4;
    private static final String TAG = Joystick.class.getName();
    int directionPressed = -1;

    public static boolean isJoystickDevice(MotionEvent event){
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        else {
            return false;
        }

    }

    public int getCenteredAxisAlt(MotionEvent event,
                                  InputDevice device, int axis) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        if (range != null) {
            final float flat = range.getFlat();
            final float value = event.getAxisValue(axis);
            if (axis == MotionEvent.AXIS_Z  ||
                    axis == MotionEvent.AXIS_X) {
                if (Float.compare(value, -1.0f) == 0) {
                    directionPressed = Joystick.LEFT;
                } else if (Float.compare(value, 1.0f) == 0) {
                    directionPressed = Joystick.RIGHT;
                }
            }
            if(axis == MotionEvent.AXIS_RZ ||
                    axis == MotionEvent.AXIS_Y) {
                if (Float.compare(value, -1.0f) == 0) {
                    directionPressed = Joystick.UP;
                } else if (Float.compare(value, 1.0f) == 0) {
                    directionPressed = Joystick.DOWN;
                }
            }

            if (Math.abs(value) > flat) {
                return directionPressed;
            }
        }
        return -1;
    }

    public int[] processJoystickInputAlt(MotionEvent event) {
        int[] directions = new int[2];
        InputDevice mInputDevice = event.getDevice();

        int x = getCenteredAxisAlt(event, mInputDevice,
                MotionEvent.AXIS_X);
        if (x < 0) {
            x = getCenteredAxisAlt(event, mInputDevice,
                    MotionEvent.AXIS_Z);
        }
        directions[0] = x;

        int y = getCenteredAxisAlt(event, mInputDevice,
                MotionEvent.AXIS_Y);
        if (y < 0) {
            y = getCenteredAxisAlt(event, mInputDevice,
                    MotionEvent.AXIS_RZ);
        }
        directions[1] = y;

        return directions;
    }

    public static float getCenteredAxis(MotionEvent event,
                                        InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {

        InputDevice mInputDevice = event.getDevice();

        float x = getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }
        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        }

        float y = getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
        }
    }
}
