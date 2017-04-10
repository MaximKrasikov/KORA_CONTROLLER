package rimp.rild.com.android.android_joystick_controler_test;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class JoyStick {
    public static final int STICK_NONE = 0;
    public static final int STICK_UP = 1;
    public static final int STICK_UPRIGHT = 2;
    public static final int STICK_RIGHT = 3;
    public static final int STICK_DOWNRIGHT = 4;
    public static final int STICK_DOWN = 5;
    public static final int STICK_DOWNLEFT = 6;
    public static final int STICK_LEFT = 7;
    public static final int STICK_UPLEFT = 8;

    private int STICK_ALPHA = 200;
    private int LAYOUT_ALPHA = 200;
    private int OFFSET = 0;
    private int OFFSET_SHADOW = 0; // addition by rild

    private Context mContext;
    private ViewGroup mLayout;
    private LayoutParams params;
    private int stick_width, stick_height;
    private int shadow_width, shadow_height; // addition by rild

    private int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;

    private DrawCanvas draw;
    private Paint paint;
    private Bitmap stick;
    private Bitmap stickShadow; // addition by rild

//    private Bitmap[] signalImgs = new Bitmap[4];
//
    private ImageView signalLayer;

    private boolean touch_state = false;

    // addition by rild from here

    public void init() {
        setLayoutSize(500, 500);
        setStickSize(220, 220);
        setLayoutAlpha(150);
        setStickAlpha(180);
        setOffset(180);
        setMinimumDistance(50);

        setStickShadow();
        setShadowSize(250, 250);

        // show stick
        draw();
    }

    public void setStickShadow() {
        stickShadow = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.joystick_shadow);
    }

    public void setShadowSize(int width, int height) {
        if (stickShadow == null) return;
        stickShadow = Bitmap.createScaledBitmap(stickShadow, width, height, false);
        this.shadow_width = width;
        this.shadow_height = height;
    }

    public void setShadowOffset(int offset) {
        OFFSET_SHADOW = offset;
    }

    public int getShadowOffset() {
        return OFFSET_SHADOW;
    }

    // end

    public JoyStick(Context context, ViewGroup layout, int stick_res_id) {
        mContext = context;

        stick = BitmapFactory.decodeResource(mContext.getResources(),
                stick_res_id);

        stick_width = stick.getWidth();
        stick_height = stick.getHeight();

        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();
        draw = new DrawCanvas(mContext);

        signalLayer = (ImageView) mLayout.findViewById(R.id.signal_layer);

//        Resources res = context.getResources();
//        signalImgs[0] = BitmapFactory.decodeResource(res, R.drawable.signal_up);
//        signalImgs[1] = BitmapFactory.decodeResource(res, R.drawable.signal_right);
//        signalImgs[2] = BitmapFactory.decodeResource(res, R.drawable.signal_down);
//        signalImgs[3] = BitmapFactory.decodeResource(res, R.drawable.signal_left);
        // out of memory
    }

    public void drawStick(MotionEvent arg1) {
        position_x = (int) (arg1.getX() - (params.width / 2));
        position_y = (int) (arg1.getY() - (params.height / 2));
        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle = (float) cal_angle(position_x, position_y);

        if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
            if (distance <= (params.width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
                touch_state = true;
            }
        } else if (arg1.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            if (distance <= (params.width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
            } else if (distance > (params.width / 2) - OFFSET) {
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) * ((params.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) * ((params.height / 2) - OFFSET));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                draw();
            } else {
                mLayout.removeView(draw);
            }
        } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
            mLayout.removeView(draw);
//            if (signalLayer != null) signalLayer.setBackgroundResource(0);
//            mLayout.setBackgroundResource(R.drawable.joystick_base);
//            mLayout.getBackground().setAlpha(LAYOUT_ALPHA);
            touch_state = false;
        }
        // reset stick
        draw();
    }

    public int[] getPosition() {
        if (distance > min_distance && touch_state) {
            return new int[]{position_x, position_y};
        }
        return new int[]{0, 0};
    }

    public int getX() {
        if (distance > min_distance && touch_state) {
            return position_x;
        }
        return 0;
    }

    public int getY() {
        if (distance > min_distance && touch_state) {
            return position_y;
        }
        return 0;
    }

    public float getAngle() {
        if (distance > min_distance && touch_state) {
            return angle;
        }
        return 0;
    }

    public float getDistance() {
        if (distance > min_distance && touch_state) {
            return distance;
        }
        return 0;
    }

    public void setMinimumDistance(int minDistance) {
        min_distance = minDistance;
    }

    public int getMinimumDistance() {
        return min_distance;
    }

    public int get8Direction() {
        if (distance > min_distance && touch_state) {
            if (angle >= 247.5 && angle < 292.5) {
//                mLayout.setBackgroundResource(R.drawable.joystick_up);
//                mLayout.getBackground().setAlpha(LAYOUT_ALPHA);

                if (signalLayer != null) signalLayer.setBackgroundResource(R.drawable.signal_up);

//                if (signalLayer != null) signalLayer.setImageBitmap(signalImgs[0]);
                return STICK_UP;
            } else if (angle >= 292.5 && angle < 337.5) {
                return STICK_UPRIGHT;
            } else if (angle >= 337.5 || angle < 22.5) {
//                mLayout.setBackgroundResource(R.drawable.joystick_right);
//                mLayout.getBackground().setAlpha(LAYOUT_ALPHA);

//                if (signalLayer != null) signalLayer.setBackgroundResource(R.drawable.signal_right);

//                if (signalLayer != null) signalLayer.setImageBitmap(signalImgs[1]);
                return STICK_RIGHT;
            } else if (angle >= 22.5 && angle < 67.5) {
                return STICK_DOWNRIGHT;
            } else if (angle >= 67.5 && angle < 112.5) {
//                mLayout.setBackgroundResource(R.drawable.joystick_down);
//                mLayout.getBackground().setAlpha(LAYOUT_ALPHA);

//                if (signalLayer != null) signalLayer.setBackgroundResource(R.drawable.signal_down);

//                if (signalLayer != null) signalLayer.setImageBitmap(signalImgs[2]);
                return STICK_DOWN;
            } else if (angle >= 112.5 && angle < 157.5) {
                return STICK_DOWNLEFT;
            } else if (angle >= 157.5 && angle < 202.5) {
//                mLayout.setBackgroundResource(R.drawable.joystick_left);
//                mLayout.getBackground().setAlpha(LAYOUT_ALPHA);

//                if (signalLayer != null) signalLayer.setBackgroundResource(R.drawable.signal_left);

//                if (signalLayer != null) signalLayer.setImageBitmap(signalImgs[3]);
                return STICK_LEFT;
            } else if (angle >= 202.5 && angle < 247.5) {
                return STICK_UPLEFT;
            }
        } else if (distance <= min_distance && touch_state) {
//            mLayout.setBackgroundResource(R.drawable.joystick_base);
//            mLayout.getBackground().setAlpha(LAYOUT_ALPHA);
//            if (signalLayer != null) signalLayer.setBackgroundResource(0);
            return STICK_NONE;
        }
        return 0;
    }

    public int get4Direction() {
        if (distance > min_distance && touch_state) {
            if (angle >= 225 && angle < 315) {
                return STICK_UP;
            } else if (angle >= 315 || angle < 45) {
                return STICK_RIGHT;
            } else if (angle >= 45 && angle < 135) {
                return STICK_DOWN;
            } else if (angle >= 135 && angle < 225) {
                return STICK_LEFT;
            }
        } else if (distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }

    public void setOffset(int offset) {
        OFFSET = offset;
    }

    public int getOffset() {
        return OFFSET;
    }

    public void setStickAlpha(int alpha) {
        STICK_ALPHA = alpha;
        paint.setAlpha(alpha);
    }

    public int getStickAlpha() {
        return STICK_ALPHA;
    }

    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        mLayout.getBackground().setAlpha(alpha);
    }

    public int getLayoutAlpha() {
        return LAYOUT_ALPHA;
    }

    public void setStickSize(int width, int height) {
        stick = Bitmap.createScaledBitmap(stick, width, height, false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
    }

    public void setStickWidth(int width) {
        stick = Bitmap.createScaledBitmap(stick, width, stick_height, false);
        stick_width = stick.getWidth();
    }

    public void setStickHeight(int height) {
        stick = Bitmap.createScaledBitmap(stick, stick_width, height, false);
        stick_height = stick.getHeight();
    }

    public int getStickWidth() {
        return stick_width;
    }

    public int getStickHeight() {
        return stick_height;
    }

    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;

        // addition
        draw.center_x = params.width / 2;
        draw.center_y = params.height / 2;
        // end

//        resizeSignalImgs(width, height);
    }

//    public void resizeSignalImgs(int width, int height) {
//        for (int i = 0; i < signalImgs.length; i++) {
//            signalImgs[i] = Bitmap.createScaledBitmap(signalImgs[i], stick_width, height, false);
//        }
//    }

    public int getLayoutWidth() {
        return params.width;
    }

    public int getLayoutHeight() {
        return params.height;
    }

    private double cal_angle(float x, float y) {
        if (x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if (x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if (x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if (x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
    }

    private void draw() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) {
        }
        mLayout.addView(draw);
    }

    private class DrawCanvas extends View {
        float x, y;
        float s_x, s_y;
        float center_x, center_y;

        private DrawCanvas(Context mContext) {
            super(mContext);
        }

        @Override
        public void onDraw(Canvas canvas) {
            // check center
//            Paint pointPaint = new Paint();
//            pointPaint.setColor(Color.RED);
//            canvas.drawCircle(center_x, center_y, 15, pointPaint);
            if (touch_state) {
                canvas.drawBitmap(stickShadow, s_x, s_y, paint);
                canvas.drawBitmap(stick, x, y, paint);
            } else {
                int stick_tall = 15;// make user feel sticky
                canvas.drawBitmap(stickShadow, center_x - (shadow_width / 2), center_y - (shadow_height / 2) + stick_tall, paint);
                canvas.drawBitmap(stick, center_x - (stick_width / 2), center_y - (stick_height / 2) - stick_tall, paint);
            }
        }

        private void position(float pos_x, float pos_y) {
            x = pos_x - (stick_width / 2);
            y = pos_y - (stick_height / 2);

            // addition
            float vecPC_x = center_x - pos_x;
            float vecPC_y = center_y - pos_y;
            Log.d("Vec", "(" + vecPC_x + "," + vecPC_y + ")");
            s_x = pos_x - (shadow_width / 2) + vecPC_x / 3;
            s_y = pos_y - (shadow_height / 2)  + vecPC_y / 3;
            // end
        }
    }
}
