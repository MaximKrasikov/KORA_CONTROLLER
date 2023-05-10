package rimp.rild.com.android.android_joystick_controler_test;

import android.content.Context;
import android.util.AttributeSet;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickViewImpl extends JoystickView {
    public static final float SAFE_AREA_STICK = (float) 0.010;
    private int mCenterX = 0;
    private int mCenterY = 0;
    private int mBorderRadius = (int) (4 / 2 * 0.75f);

    public JoystickViewImpl(Context context) {
        super(context);
    }

    public JoystickViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JoystickViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnMoveListener(OnMoveListener l) {
        super.setOnMoveListener(l);
    }

    public float getAngle(float axisX, float axisY) {
        float angle = (float) Math.toDegrees(Math.atan2(mCenterY - axisY, axisX - mCenterX));
        return angle < 0 ? angle + 360 : angle;
    }

    public int getStrength(float axisX, float axisY) {
        return (int) (100 * Math.sqrt((axisX - mCenterX)
                * (axisX - mCenterX) + (axisY - mCenterY)
                * (axisY - mCenterY)) / mBorderRadius);
    }

}
