package tw.supra.suclear.widget;

import android.app.Activity;

import androidx.annotation.NonNull;

public abstract class AbsWidget<HosT extends Activity> implements Widget<HosT> {
    private final @NonNull
    HosT mHost;
    private boolean mHasInitialized = false;

    public AbsWidget(@NonNull HosT host) {
        mHost = host;
    }

    protected abstract boolean onInit();

    public boolean initIfNecessary() {
        if (valid()) {
            return true;
        }
        if (!mHasInitialized) {
            mHasInitialized = onInit();
            return valid();
        }
        return false;
    }

    @Override
    public HosT getHost() {
        return mHost;
    }

    @Override
    public boolean valid() {
        return mHasInitialized && hasValidHost();
    }

    protected boolean hasValidHost() {
        return !mHost.isDestroyed() && !mHost.isFinishing();
    }

}
