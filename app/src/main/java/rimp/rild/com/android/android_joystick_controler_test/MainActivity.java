package rimp.rild.com.android.android_joystick_controler_test;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {
    TextView mTextViewState, mTextViewAngle, mTextViewDistance, mTextViewDirection, mTextViewPower;

    JoystickViewImpl joystickViewImpl;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick_view_activity);

        mTextViewAngle = (TextView) findViewById(R.id.angleTextView);
        mTextViewPower = (TextView) findViewById(R.id.powerTextView);
        mTextViewDirection= (TextView) findViewById(R.id.directionTextView);
        joystickViewImpl = (JoystickViewImpl) findViewById(R.id.main_joystick);


        joystickViewImpl.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                mTextViewAngle.setText(angle+" angle");
                mTextViewPower.setText(strength+ " strength");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public boolean onGenericMotionEvent(MotionEvent event){
        if ((event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK) == 0)
            return false;

        float axisX = event.getAxisValue(MotionEvent.AXIS_X);
        float axisY = event.getAxisValue(MotionEvent.AXIS_Y);

        if (axisX > -JoystickViewImpl.SAFE_AREA_STICK && axisX <  JoystickViewImpl.SAFE_AREA_STICK) {
            axisX = 0;
        }
        if (axisY > -JoystickViewImpl.SAFE_AREA_STICK && axisY <  JoystickViewImpl.SAFE_AREA_STICK) {
            axisY = 0;
        }
        float angleRealJoystick = joystickViewImpl.getAngle(axisX, axisY);
        int strengthRealJoystick =joystickViewImpl.getStrength(axisX,axisY);

        //String angleRealJoystickString = String.format("angle : %.3f", angleRealJoystick);
        String angleRealJoystickString = ""+Math.round(angleRealJoystick);
        String strengthRealJoystickString = ""+Math.round(strengthRealJoystick);

        mTextViewAngle.setText(angleRealJoystickString + "°");
        mTextViewPower.setText(" " + strengthRealJoystickString + "%");

       /* String x= String.format("axX : %.3f", axisX);
        String y= String.format("axY : %.3f", -axisY);
        mTextViewAngle.setText(x + "°");
        mTextViewPower.setText( y +"%");*/

        return true;
    }


}
