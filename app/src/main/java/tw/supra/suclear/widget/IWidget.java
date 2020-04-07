package tw.supra.suclear.widget;

import android.content.ComponentCallbacks2;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;

public interface IWidget {

    interface HostCallback extends LayoutInflater.Factory2, Window.Callback, KeyEvent.Callback,
            View.OnCreateContextMenuListener, ComponentCallbacks2 {
        void onResume();

        void onDestroy();
    }

    @Nullable
    HostCallback getHostCallback();

}
