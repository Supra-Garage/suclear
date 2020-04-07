package tw.supra.suclear.feature;

import android.content.Context;

import androidx.annotation.NonNull;

public abstract class AbsFeature<HosT extends Context> implements Feature<HosT> {
    private final @NonNull HosT mHost;
    private boolean mHasInitialized = false;

    public AbsFeature(@NonNull HosT host) {
        mHost = host;
    }

    protected abstract boolean onInit();
    protected abstract boolean hasValidHost();

    public boolean ensureInitialized() {
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


}
