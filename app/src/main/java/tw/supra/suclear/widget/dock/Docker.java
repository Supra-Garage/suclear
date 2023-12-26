package tw.supra.suclear.widget.dock;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.HashMap;
import java.util.Map;

import tw.supra.suclear.GlideApp;
import tw.supra.suclear.GlideRequest;
import tw.supra.suclear.R;
import tw.supra.suclear.utils.typedbox.AppUtil;
import tw.supra.suclear.widget.Agency;
import tw.supra.suclear.widget.HostCallbackWrapper;
import tw.supra.suclear.widget.Widget;

public abstract class Docker<HosT extends Activity> extends Widget<HosT>
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextView.OnEditorActionListener {
    private static final String SP_BOOL_LOCKER = "DOCKER_LOCKED";
    private static Handler sHandler = new Handler();
    private ImageView mViewIcon;
    private CompoundButton mLocker;
    private View mPanel;
    private View mController;
    private ProgressBar mProgress;
    private EditText mUrlEditor;
    private TextView mTitle;
    private final Map<String, String> mTouchIconMap = new HashMap<>();
    private final Runnable mHideControllerTask = () -> hideController();
    private final Runnable mProgressWatcher = () -> mProgress.setIndeterminate(true);

    public Docker(@NonNull Agency<HosT> agency) {
        super(agency);
    }

    protected abstract void onReload();

    protected abstract void onLaunch(CharSequence action);

    @Nullable
    @Override
    protected HostCallback createHostCallback() {
        return new HostCallbackWrapper() {
            @Override
            public void onResume() {
                updateUiController();
                hideControllerDelayed();
            }

            @Override
            public void onDestroy() {
                sHandler.removeCallbacks(mProgressWatcher);
            }
        };
    }

    @Override
    protected boolean onInit() {
        if (hasValidHost()) {
            HosT host = getHost();
            mController = host.findViewById(R.id.controller);
            mPanel = host.findViewById(R.id.panel);

            mViewIcon = host.findViewById(R.id.icon);
            mUrlEditor = host.findViewById(R.id.url_editor);
            mUrlEditor.setOnEditorActionListener(this);
            mProgress = host.findViewById(R.id.progress);
            mTitle = host.findViewById(R.id.title);
            mTitle.setOnClickListener(this);
            //        findViewById(R.id.forward).setOnClickListener(this);
            host.findViewById(R.id.reload).setOnClickListener(this);
            host.findViewById(R.id.more).setOnClickListener(this);

            mLocker = host.findViewById(R.id.lock);
            mLocker.setChecked(lock(getHost().getPreferences(Context.MODE_PRIVATE).getBoolean(SP_BOOL_LOCKER, false)));
            mLocker.setOnCheckedChangeListener(this);
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        lock(isChecked);
        getHost().getPreferences(Context.MODE_PRIVATE).edit().putBoolean(SP_BOOL_LOCKER, isChecked).apply();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.title) {
            edit();
        } else if (id == R.id.reload) {
            onReload();
        } else if (id == R.id.more) {
            getHost().openOptionsMenu();
        } else {
            AppUtil.toast(getHost(), "not implement yet !");
        }


        //            case R.id.forward:
//                goForward();
//                break;
//            case R.id.hide:
//                hideController();
//                break;

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.i(this.getClass().getSimpleName(), String.format("onEditorAction(%d) ===\n keyEvent(%s)", actionId, event));
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
            case EditorInfo.IME_ACTION_SEARCH:
            case EditorInfo.IME_ACTION_GO:
                hideEditor();
                updateUiController();
                onLaunch(v.getText());
                return true;
            default:
                return false;
        }
    }

    private void hideControllerDelayed() {
        sHandler.removeCallbacks(mHideControllerTask);
        sHandler.postDelayed(mHideControllerTask, 5000);
    }

    private void clearHideControllerTask() {
        sHandler.removeCallbacks(mHideControllerTask);
    }

    private void updateUiController() {
        updateUiController(getHost().getResources().getConfiguration());
    }

    public void updateUiController(Configuration config) {
        switch (config.orientation) {
            case Configuration.ORIENTATION_UNDEFINED:
            case Configuration.ORIENTATION_PORTRAIT:
                showController();
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                if (!isEditorVisible()) {
                    hideController();
                }
                break;
            default:
                break;
        }
    }

    public void updateProgress(int newProgress) {
        sHandler.removeCallbacks(mProgressWatcher);
        mProgress.setIndeterminate(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mProgress.setProgress(newProgress);
        } else {
            mProgress.setProgress(newProgress, true);
        }
        if (newProgress < 95) {
            sHandler.postDelayed(mProgressWatcher, 2000);
        }
    }

    private boolean showEditor() {
        return toggleEditor(true);
    }

    public boolean hideEditor() {
        return toggleEditor(false);
    }

    private boolean toggleEditor(boolean visible) {
        boolean toggle = visible ^ isEditorVisible();
        if (toggle) {
            toggleEditor();
        }
        return toggle;
    }


    private boolean hideController() {
        return toggleController(false);
    }

    private boolean toggleController(boolean visible) {
        return visible ^ isControllerVisible() && toggleController();
    }

    private boolean toggleController() {
        boolean visible = !isControllerVisible();
        if (!visible && mLocker.isChecked()) {
            return false;
        }
        mController.setVisibility(visible ? View.VISIBLE : View.GONE);
        return true;
    }


    private boolean isEditorVisible() {
        return mUrlEditor.getVisibility() == View.VISIBLE;
    }

    private boolean isControllerVisible() {
        return mController.getVisibility() == View.VISIBLE;
    }

    private void edit() {
        showEditor();
    }


    private void toggleEditor() {
        boolean visible = !isEditorVisible();
        mUrlEditor.setVisibility(visible ? View.VISIBLE : View.GONE);
        mPanel.setVisibility(visible ? View.GONE : View.VISIBLE);
        InputMethodManager imm = getHost().getSystemService(InputMethodManager.class);
        if (visible) {
            clearHideControllerTask();
            mUrlEditor.setSelection(0, mUrlEditor.getText().length());
            mUrlEditor.requestFocus();
            imm.showSoftInput(mUrlEditor, InputMethodManager.SHOW_IMPLICIT);
        } else {
            hideControllerDelayed();
            imm.hideSoftInputFromWindow(mUrlEditor.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    public boolean showController() {
        if (toggleController(true)) {
            hideControllerDelayed();
            return true;
        }
        return false;
    }

    public void setIconWithPage(String pageUrl, Bitmap icon) {
        if (!setIcon(mTouchIconMap.get(pageUrl)) && null != icon) {
            setIcon(GlideApp.with(getHost()).load(icon));
        }
    }

    public void setTouchIconUrlWithPage(String pageUrl, String imgUrl) {
        if (!TextUtils.isEmpty(pageUrl) && !TextUtils.isEmpty(imgUrl)) {
            mTouchIconMap.put(pageUrl, imgUrl);
            setIcon(imgUrl);
        }
    }

    private boolean setIcon(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            setIcon(GlideApp.with(getHost()).load(imgUrl));
            return true;
        }
        return false;
    }

    private <TranscodeType> void setIcon(@NonNull GlideRequest<TranscodeType> glideRequest) {
        if (ensureInitialized()) {
            glideRequest.centerCrop().transform(new RoundedCorners(10)).into(mViewIcon);
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title.trim());
    }

    public void setUrl(String url) {
        mUrlEditor.setText(url);
        setTitleHint(url);
    }

    public void setTitleHint(String hint) {
        mTitle.setHint(hint);
    }

    private boolean lock(boolean locked) {
        if (locked) {
            clearHideControllerTask();
        } else {
            hideControllerDelayed();
        }
        return locked;
    }

}
