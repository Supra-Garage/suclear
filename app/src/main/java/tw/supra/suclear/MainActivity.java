package tw.supra.suclear;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import tw.supra.lib.supower.util.Logger;

public class MainActivity extends Activity implements WebView.FindListener, MainWebViewHost, View.OnClickListener,
        KeyboardWatcherFrameLayout.OnSoftKeyboardShownListener, TextView.OnEditorActionListener,
        CompoundButton.OnCheckedChangeListener {
    private static final String SCHEME_HTTP = "http";
    private static final int MSG_EXIT = android.R.id.closeButton;
    private static Handler sHandler = new Handler();
    private final Map<String, String> mTouchIconMap = new HashMap<>();
    private EditText mUrlEditor;
    private TextView mTitle;
    private View mPanel;
    private View mController;
    private ImageView mIcon;
    private MainWebView mWebView;
    private ProgressBar mProgress;
    private TextView mFindCount;
    private EditText mFindKey;
    private final Runnable mProgressWatcher = new Runnable() {
        @Override
        public void run() {
            mProgress.setIndeterminate(true);
        }
    };
    private CompoundButton mLocker;
    private final Runnable mHideControllerTask = new Runnable() {
        @Override
        public void run() {
            hideController();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KeyboardWatcherFrameLayout container = findViewById(R.id.container);
        container.setListener(this);

        mWebView = findViewById(R.id.webview);
        mWebView.setHost(this);
        mWebView.setWebViewClient(new MainWebViewClient());
        mWebView.setWebChromeClient(new MainWebChromeClient());

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        mUrlEditor = findViewById(R.id.url_editor);
        mUrlEditor.setOnEditorActionListener(this);

        mLocker = findViewById(R.id.lock);
        mLocker.setOnCheckedChangeListener(this);

        mProgress = findViewById(R.id.progress);

        mController = findViewById(R.id.controller);
        mPanel = findViewById(R.id.panel);
        mTitle = findViewById(R.id.title);
        mIcon = findViewById(R.id.icon);
        mTitle.setOnClickListener(this);

//        findViewById(R.id.forward).setOnClickListener(this);
        findViewById(R.id.reload).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);

        setupFind();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        go("baidu.com");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiController();
        hideControllerDelayed();
    }

    @Override
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        mFindCount.setText(activeMatchOrdinal + "/" + numberOfMatches);
    }

    @Nullable
    @Override
    public WebViewClient getWebViewClient() {
        return null;
    }

    @Nullable
    @Override
    public WebChromeClient getWebChromeClient() {
        return null;
    }

    @Nullable
    @Override
    public WebSuclearClient getWebSuclearClient() {
        return null;
    }

    private void updateUiController() {
        updateUiController(getResources().getConfiguration());
    }

    private void updateUiController(Configuration config) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sHandler.removeCallbacks(mProgressWatcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.forward:
//                goForward();
//                break;
//            case R.id.hide:
//                hideController();
//                break;
            case R.id.title:
                edit();
                break;
            case R.id.reload:
                mWebView.reload();
                break;
            case R.id.find:
                findViewById(R.id.find_container).setVisibility(View.VISIBLE);
                break;
            case R.id.btn_find_cancel:
                findViewById(R.id.find_container).setVisibility(View.GONE);
                mFindKey.setText("");
                break;
            case R.id.btn_find_privous:
                mWebView.findNext(false);
                break;
            case R.id.btn_find_next:
                mWebView.findNext(true);
                break;
            default:
                toast("not implement yet !");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            clearHideControllerTask();
        } else {
            hideControllerDelayed();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        Log.i(this.getClass().getSimpleName(), String.format("onEditorAction(%d) ===\n keyEvent(%s)", i, keyEvent));
        switch (i) {
            case EditorInfo.IME_ACTION_DONE:
            case EditorInfo.IME_ACTION_SEARCH:
            case EditorInfo.IME_ACTION_GO:
                hideEditor();
                updateUiController();
                go(textView.getText());
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (showController() || hideEditor() || goBack() || preBack()) {
            return;
        }
        super.onBackPressed();
    }

    private void setupFind() {
        mWebView.setFindListener(this);

        mFindCount = findViewById(R.id.tv_find_count);
        mFindKey = findViewById(R.id.et_find_key);
        
        findViewById(R.id.find).setOnClickListener(this);
        findViewById(R.id.btn_find_cancel).setOnClickListener(this);
        findViewById(R.id.btn_find_next).setOnClickListener(this);
        findViewById(R.id.btn_find_privous).setOnClickListener(this);

        mFindKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mWebView.findAllAsync(s.toString());
            }
        });
    }

    private boolean preBack() {
        if (sHandler.hasMessages(MSG_EXIT)) {
            return false;
        }
        toast("press back again for exit");
        sHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
        return true;
    }

    @Override
    public void setTitle(WebView view, String title) {
        mTitle.setText(title.trim());
    }

    @Override
    public void setUrl(WebView view, String url) {
        mUrlEditor.setText(url);
        mTitle.setHint(url);
    }

    @Override
    public void clearUrl(WebView view, String url) {
    }

    @Override
    public void setIcon(WebView view, Bitmap icon) {
        Log.i(Logger.getStackTag("icon is null? "), String.valueOf(null == icon));

        if (null != icon) {
            Log.i(Logger.getStackTag("icon size"), String.valueOf(icon.getByteCount()));
        }

        if (!setIcon(mTouchIconMap.get(view.getUrl())) && null != icon) {
            Log.i(Logger.getStackTag("icon size"), String.valueOf(icon.getByteCount()));
            mIcon.setImageBitmap(icon);
        }
    }

    @Override
    public void setTouchIconUrl(WebView view, String url, boolean precomposed) {
        setIcon(view, url);
    }

    public boolean setIcon(WebView view, String url) {
        if (null == view) {
            return false;
        }
        return setIcon(view.getUrl(), url);
    }

    public boolean setIcon(String viewUrl, String imgUrl) {
        Log.i(Logger.getStackTag("viewUrl"), viewUrl);
        Log.i(Logger.getStackTag("imgUrl"), imgUrl);
        if (TextUtils.isEmpty(viewUrl) || TextUtils.isEmpty(imgUrl)) {
            return false;
        }
        mTouchIconMap.put(viewUrl, imgUrl);
        return setIcon(imgUrl);
    }

    public boolean setIcon(String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return false;
        }
        Log.i(Logger.getStackTag("imgUrl"), imgUrl);
        GlideApp.with(this).load(imgUrl).centerCrop().into(mIcon);
        return true;
    }

    @Override
    public void changeProgress(WebView view, int newProgress) {
        updateProgress(newProgress);
    }

    @Override
    public void onSoftKeyboardShown(boolean isShowing) {
        if (!isShowing) {
            hideEditor();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateUiController(newConfig);
    }

    private void updateProgress(int newProgress) {
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

    private boolean hideEditor() {
        return toggleEditor(false);
    }

    private boolean toggleEditor(boolean visible) {
        boolean toggle = visible ^ isEditorVisible();
        if (toggle) {
            toggleEditor();
        }
        return toggle;
    }

    private void toggleEditor() {
        boolean visible = !isEditorVisible();
        mUrlEditor.setVisibility(visible ? View.VISIBLE : View.GONE);
        mPanel.setVisibility(visible ? View.GONE : View.VISIBLE);
        InputMethodManager imm = getSystemService(InputMethodManager.class);
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

    private boolean showController() {
        if (toggleController(true)) {
            hideControllerDelayed();
            return true;
        }
        return false;
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

    private void hideControllerDelayed() {
        sHandler.removeCallbacks(mHideControllerTask);
        sHandler.postDelayed(mHideControllerTask, 3000);
    }

    private void clearHideControllerTask() {
        sHandler.removeCallbacks(mHideControllerTask);
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

    private boolean go(CharSequence query) {
        return loadUrl(query) || openUri(query) || search(query);
    }

    private boolean loadUrl(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            toast("empty url");
            Log.i(Logger.getStackTag(), "empty url");
            return false;
        }
        String s = target.toString();
        // 原始正则 ^([^\s/.@#?:]+\.)+[^\s/.@#?:]+\/?\??#?\S*$
        if (Pattern.matches("^([^\\s/.@#?:]+\\.)+[^\\s/.@#?:]+\\/?\\??#?\\S*$", s)) {
            s = SCHEME_HTTP + "://" + s;
        }
        return loadUrl(HttpUrl.parse(s));
    }

    private boolean loadUrl(HttpUrl url) {
        if (null == url) {
            return false;
        }
        HttpUrl.Builder builder = url.newBuilder();

        String scheme = url.scheme();

        Log.i(Logger.getStackTag("scheme"), scheme);
        Log.i(Logger.getStackTag("host"), url.host());

        if (TextUtils.isEmpty(scheme)) {
            builder.scheme(SCHEME_HTTP);
        }

        String urlStr = builder.build().toString();
        Log.i(Logger.getStackTag("urlStr"), urlStr);
        if (!URLUtil.isValidUrl(urlStr)) {
            Log.i(Logger.getStackTag("urlStr"), "isNotValidUrl");
            return false;
        }
        mUrlEditor.setText(urlStr);
        mTitle.setHint(urlStr);
        Log.i(Logger.getStackTag("load url"), urlStr);
        mWebView.loadUrl(urlStr);
        return true;
    }

    private boolean openUri(CharSequence target) {

        if (TextUtils.isEmpty(target)) {
            toast("empty url");
            return false;
        }
        String uriStr = target.toString();
        Log.i(Logger.getStackTag("target"), uriStr);

        return openUri(Uri.parse(uriStr));
    }

    private boolean openUri(Uri uri) {

        if (null == uri) {
            return false;
        }

        String scheme = uri.getScheme();

        if (TextUtils.isEmpty(uri.getScheme())) {
            return false;
        }
        Log.i(Logger.getStackTag("scheme"), scheme);

        Intent intent;
        try {
            intent = Intent.parseUri(uri.toString(), Intent.URI_ANDROID_APP_SCHEME | Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        if (null == intent) {
            return false;
        }

        if (null == intent.resolveActivity(getPackageManager())) {
            return false;
        }

        startActivity(intent);

        return true;
    }

    private boolean search(CharSequence target) {

        if (TextUtils.isEmpty(target)) {
            toast("empty query");
            return false;
        }
        String query = target.toString();
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTP)
                .host("baidu.com")
                .addPathSegment("s")
                .addQueryParameter("wd", query)
                .build();
        return loadUrl(url);
    }

    private boolean goForward() {
        boolean canGoForward = mWebView.canGoForward();
        if (canGoForward) {
            mWebView.goForward();
        }
        return canGoForward;
    }

    private boolean goBack() {
        boolean canGoBack = mWebView.canGoBack();
        if (canGoBack) {
            mWebView.goBack();
        }
        return canGoBack;
    }

    private void toast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void toast(@StringRes int res) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }
}
