package tw.supra.suclear.widget;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import tw.supra.suclear.feature.AbsFeature;
import tw.supra.suclear.utils.typedbox.TypedCallback;

public final class Agency<HosT extends Activity> extends AbsFeature<HosT> implements IWidget.HostCallback {

    private final Set<Widget<HosT>> mWidgets = new HashSet<>();

    public Agency(@NonNull HosT host) {
        super(host);
    }

    @Override
    public void onTrimMemory(int level) {
        forEachWidget(msg -> msg.onTrimMemory(level));

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        forEachWidget(msg -> msg.onConfigurationChanged(newConfig));
    }

    @Override
    public void onLowMemory() {
        forEachWidget(ComponentCallbacks::onLowMemory);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO: 20-4-7 @supra impl
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {  // TODO: 20-4-7 @supra impl
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {  // TODO: 20-4-7 @supra impl
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {  // TODO: 20-4-7 @supra impl
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(
            @Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;  // TODO: 20-4-7 @supra impl
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null; // TODO: 20-4-7 @supra impl
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        forEachWidget(msg -> msg.onCreateContextMenu(menu, v, menuInfo));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Nullable
    @Override
    public View onCreatePanelView(int featureId) {
        return null; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean onMenuOpened(int featureId, @NonNull Menu menu) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        forEachWidget(widget -> widget.onWindowAttributesChanged(attrs));
    }

    @Override
    public void onContentChanged() {
        forEachWidget(Window.Callback::onContentChanged);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        forEachWidget(widget -> widget.onWindowFocusChanged(hasFocus));
    }

    @Override
    public void onAttachedToWindow() {
        forEachWidget(Window.Callback::onAttachedToWindow);
    }

    @Override
    public void onDetachedFromWindow() {
        forEachWidget(Window.Callback::onDetachedFromWindow);
    }

    @Override
    public void onPanelClosed(int featureId, @NonNull Menu menu) {
        forEachWidget(msg -> msg.onPanelClosed(featureId, menu));
    }

    @Override
    public boolean onSearchRequested() {
        return false;  // TODO: 20-4-7 @supra impl
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return false;  // TODO: 20-4-7 @supra impl
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;  // TODO: 20-4-7 @supra impl
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return null;  // TODO: 20-4-7 @supra impl
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        forEachWidget(msg -> msg.onActionModeStarted(mode));
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        forEachWidget(msg -> msg.onActionModeFinished(mode));
    }

    @Override
    protected boolean onInit() {
        return false;   // TODO: 20-4-7 @supra impl
    }

    @Override
    protected boolean hasValidHost() {
        return false; // TODO: 20-4-7 @supra impl
    }

    @Override
    public void onResume() {
        forEachWidget(IWidget.HostCallback::onResume);
    }

    @Override
    public void onDestroy() {
        forEachWidget(IWidget.HostCallback::onDestroy);
    }

    void reg(Widget<HosT> widget) {
        mWidgets.add(widget);
    }

    public void forEachWidget(TypedCallback<IWidget.HostCallback> callback) {
        for (Widget widget : mWidgets) {
            IWidget.HostCallback dest;
            if (null != widget && widget.valid() && null != (dest = widget.getHostCallback())) {
                callback.onCallback(dest);
            }
        }
    }
}
