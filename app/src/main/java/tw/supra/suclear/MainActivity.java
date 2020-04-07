package tw.supra.suclear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.webkit.WebView;

import java.net.URISyntaxException;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import tw.supra.lib.supower.util.Logger;
import tw.supra.suclear.demo.DemoActivity;
import tw.supra.suclear.utils.typedbox.AppUtil;
import tw.supra.suclear.widget.Agency;
import tw.supra.suclear.widget.dock.Docker;
import tw.supra.suclear.server.SuServer;
import tw.supra.suclear.widget.find.Finder;
import tw.supra.suclear.widget.web.Browser;

public class MainActivity extends AbsActivity implements KeyboardWatcherFrameLayout.OnSoftKeyboardShownListener {

    private static final String SCHEME_HTTP = "http";
    private static final int MSG_EXIT = android.R.id.closeButton;
    private static Handler sHandler = new Handler();

    private final Agency<MainActivity> mAgency = new Agency<>(this);
    private final Browser<MainActivity> mBrowser = new Browser<MainActivity>(mAgency) {
        @Override
        public void setTitle(WebView view, String title) {
            mDocker.setTitle(title);
        }

        @Override
        public void setUrl(WebView view, String url) {
            mDocker.setUrl(url);
        }

        @Override
        public void setIcon(WebView view, Bitmap icon) {
            mDocker.setIconWithPage(view.getUrl(), icon);
        }

        @Override
        public void setTouchIconUrl(WebView view, String url, boolean precomposed) {
            mDocker.setTouchIconUrlWithPage(view.getUrl(), url);
        }

        @Override
        public void changeProgress(WebView view, int newProgress) {
            mDocker.updateProgress(newProgress);
        }
    };
    private final Finder<MainActivity> mFinder = new Finder<>(mAgency);
    private final Docker<MainActivity> mDocker = new Docker<MainActivity>(mAgency) {
        @Override
        protected void onReload() {
            mBrowser.reload();
        }

        @Override
        protected void onLaunch(CharSequence action) {
            go(action);
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

        mBrowser.ensureInitialized();
        mDocker.ensureInitialized();
        mFinder.ensureInitialized();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        go("baidu.com");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAgency.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_demo:
                startActivity(new Intent(this, DemoActivity.class));
                return true;
            case R.id.menu_item_find:
                mFinder.toggleFind(mBrowser.curWebView());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgency.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDocker.showController() || mDocker.hideEditor() || mBrowser.goBack() || preBack()) {
            return;
        }
        super.onBackPressed();
    }


    private boolean preBack() {
        if (sHandler.hasMessages(MSG_EXIT)) {
            return false;
        }
        AppUtil.toast(this, "press back again for exit");
        sHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
        return true;
    }

    @Override
    public void onSoftKeyboardShown(boolean isShowing) {
        if (!isShowing) {
            mDocker.hideEditor();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDocker.updateUiController(newConfig);
    }

    private boolean go(CharSequence query) {
        return loadUrl(query) || openUri(query) || search(query);
    }

    private boolean loadUrl(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            AppUtil.toast(this, "empty url");
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
        mDocker.setUrl(urlStr);

        Log.i(Logger.getStackTag("load url"), urlStr);
        mBrowser.loadUrl(urlStr);
        return true;
    }

    private boolean openUri(CharSequence target) {

        if (TextUtils.isEmpty(target)) {
            AppUtil.toast(this, "empty url");
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
            AppUtil.toast(this, "empty query");
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


}
