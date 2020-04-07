package tw.supra.suclear.widget;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tw.supra.suclear.feature.AbsFeature;

public abstract class Widget<HosT extends Activity> extends AbsFeature<HosT> implements IWidget {
    private  HostCallback mHostCallback;

    public Widget(@NonNull Agency<HosT> agency) {
        super(agency.getHost());
        agency.reg(this);
    }

    @Nullable
    protected abstract HostCallback createHostCallback();

    @Override
    protected boolean hasValidHost() {
        return !getHost().isDestroyed() && !getHost().isFinishing();
    }

    @Override
    public HostCallback getHostCallback() {
        if (null == mHostCallback) {
            mHostCallback = createHostCallback();
        }
        return mHostCallback;
    }

}
