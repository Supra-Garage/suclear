package tw.supra.suclear;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
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
import tw.supra.suclear.demo.DemoActivity;
import tw.supra.suclear.server.SuServer;

public class MainActivity extends Activity implements PermissionsRequestCode, WebView.FindListener, MainWebViewHost,
        View.OnClickListener, KeyboardWatcherFrameLayout.OnSoftKeyboardShownListener,
        TextView.OnEditorActionListener, CompoundButton.OnCheckedChangeListener, DownloadListener {


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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SuServer.initIfNecessary(this);
        setContentView(R.layout.activity_main);
        KeyboardWatcherFrameLayout container = findViewById(R.id.container);
        container.setListener(this);

        mWebView = findViewById(R.id.webview);
        mWebView.setHost(this);
        mWebView.setWebViewClient(new MainWebViewClient());
        mWebView.setWebChromeClient(new MainWebChromeClient());
        mWebView.setDownloadListener(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_demo:
                startActivity(new Intent(this, DemoActivity.class));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
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

    @Override
    public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimetype, long contentLength) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.download);
        builder.setMessage(contentDisposition + " " + url);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadBySystem(url, contentDisposition, mimetype);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setCancelable(true);
        builder.show();
    }

    private void downloadBySystem(String url, String contentDisposition, String mimetype) {

// Here, thisActivity is the current activity
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PRC_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimetype);

        // When downloading music and videos they will be listed in the player
        // (Seems to be available since Honeycomb only)
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
        request.setDescription(contentDisposition);
        // 允许在计费流量下下载
//        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
//        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
//        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        Log.d(Logger.getStackTag(), "fileName:" + fileName);
        // This put the download in the same Download dir the browser uses
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = getSystemService(DownloadManager.class);
        if (null == downloadManager) {
            toast("can not find download manager");
            return;
        }
        // Start download
        long downloadId = downloadManager.enqueue(request);
        Log.d("downloadId:{}", "" + downloadId);
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
            case R.id.more:
                more(view);
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
        sHandler.postDelayed(mHideControllerTask, 5000);
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

    private void more(View anchor) {
        openOptionsMenu();
//        PopupMenu popup = new PopupMenu(this, anchor);
//        Menu menu = popup.getMenu();
//        menu.add("supra");
//        menu.add("brz");
//        menu.add("wrx");
//        popup.setGravity(Gravity.CENTER);
//        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(MainActivity.this, "onDismiss", Toast.LENGTH_LONG ).show();
//            }
//        });
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(MainActivity.this, "onMenuItemClick : "+item.getTitle(), Toast.LENGTH_LONG ).show();
//                return true;
//            }
//        });
//        popup.show();
    }

    private void toast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void toast(@StringRes int res) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }
}
