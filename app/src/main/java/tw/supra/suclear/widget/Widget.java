package tw.supra.suclear.widget;

import android.content.Context;

public interface Widget<HosT extends Context> {
    HosT getHost();

    boolean valid();
}
