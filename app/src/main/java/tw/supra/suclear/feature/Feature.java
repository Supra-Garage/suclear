package tw.supra.suclear.feature;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

public interface Feature<HosT extends Context> {
    @NotNull
    HosT getHost();

    boolean valid();
}
