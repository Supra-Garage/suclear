package tw.supra.suclear;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by wangjia on 17-6-21.
 */
public class KeyboardWatcherFrameLayout extends FrameLayout {
    private OnSoftKeyboardShownListener listener;

    public KeyboardWatcherFrameLayout(Context context) {
        super(context);
    }

    public KeyboardWatcherFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardWatcherFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnSoftKeyboardShownListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Activity activity = (Activity) getContext();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        int diff = (screenHeight - statusBarHeight) - height;
        boolean flag = diff > 128;
        if (listener != null && flag != mFlagKeyboardShowing) {
            mFlagKeyboardShowing = flag;
            listener.onSoftKeyboardShown(mFlagKeyboardShowing); // assume all soft keyboards are at least 128 pixels high
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean mFlagKeyboardShowing = false;

    public interface OnSoftKeyboardShownListener {
        void onSoftKeyboardShown(boolean isShowing);
    }
}
