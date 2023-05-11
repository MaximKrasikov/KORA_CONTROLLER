package rimp.rild.com.android.android_joystick_controler_test;

import static android.view.MotionEvent.AXIS_X;
import static android.view.MotionEvent.AXIS_Y;

import static rimp.rild.com.android.android_joystick_controler_test.util.RealJoystickController.getGameControllerIds;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;


import androidx.annotation.Nullable;

import java.util.ArrayList;

import rimp.rild.com.android.android_joystick_controler_test.entity.Dpad;
import rimp.rild.com.android.android_joystick_controler_test.entity.Joystick;

//Fragment

//https://developer.android.com/develop/ui/views/touch-and-input/game-controllers/controller-input
//https://github.com/taayers/BluetoothControllerTesting/blob/b1b157a703ea4acfc2f2ac934baeb2d749f4a02c/MainActivity.java
public class BluetoothJoystickControlsFragment extends JoystickView {
    Dpad mDpad = new Dpad();
    Joystick mJstick = new Joystick();

    public BluetoothJoystickControlsFragment(Context context) {
        super(context);
    }

    public BluetoothJoystickControlsFragment(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BluetoothJoystickControlsFragment(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BluetoothJoystickControlsFragment(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis) :
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    //    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    // Handle gamepad and D-pad button presses to
                    // navigate the ship

                    default:
                        // Update the ship object to fire lasers
                        ArrayList<Integer> gameControllerIds = getGameControllerIds();

                        handled = true;
                        break;
                }
            }
            if (handled) {
                return true;
            }
        }
//        return super.onKeyDown(keyCode, event);
        return true;
    }

    //    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Check if this event if from a D-pad and process accordingly.
        int action = event.getAction();
        float axisValueX = event.getAxisValue(AXIS_X);
        float axisValueY = event.getAxisValue(AXIS_Y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int actionButton = event.getActionButton();
            int buttonState = event.getButtonState();
        }

        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        } else if (Dpad.isDpadDevice(event)) {

            int press = mDpad.getDirectionPressed(event);
            switch (press) {
                case Dpad.LEFT:
                    // Do something for LEFT direction press
                    return true;
                case Dpad.RIGHT:
                    // Do something for RIGHT direction press
                    return true;
                case Dpad.UP:
                    // Do something for UP direction press
                    return true;
                case Dpad.DOWN:
                    // Do something for UP direction press
                    return true;
            }
            return false;
        }

        // Check if this event is from a joystick movement and process accordingly.
//        return super.onGenericMotionEvent(event);
        return true;
    }

    private void processJoystickInput(MotionEvent event, int i) {

    }
}
