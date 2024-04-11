package bo.gob.ine.naci.epc.adaptadores;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by INE.
 */
public class SwipeDetector implements View.OnTouchListener {
    public static enum Action {
        LR,
        RL,
        None
    }

    private static final int MIN_DISTANCE = 100;
    private float downX, upX;
    private Action mSwipeDetected = Action.None;

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                mSwipeDetected = Action.None;
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();

                float deltaX = downX - upX;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX < 0) {
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                }

                return true;
            }
        }
        return false;
    }
}
