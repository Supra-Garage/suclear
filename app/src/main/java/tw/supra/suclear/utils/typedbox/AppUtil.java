package tw.supra.suclear.utils.typedbox;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

import org.jetbrains.annotations.NotNull;

public final class AppUtil {
    private AppUtil() {
    }


    public static void toast(@NotNull Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(@NotNull Context context, @StringRes int res) {
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }
}
