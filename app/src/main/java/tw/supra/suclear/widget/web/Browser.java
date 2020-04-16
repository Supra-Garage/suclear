package tw.supra.suclear.widget.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tw.supra.lib.supower.util.Logger;
import tw.supra.suclear.MainWebChromeClient;
import tw.supra.suclear.MainWebView;
import tw.supra.suclear.MainWebViewClient;
import tw.supra.suclear.MainWebViewHost;
import tw.supra.suclear.PermissionsRequestCode;
import tw.supra.suclear.R;
import tw.supra.suclear.WebSuclearClient;
import tw.supra.suclear.utils.typedbox.AppUtil;
import tw.supra.suclear.widget.Agency;
import tw.supra.suclear.widget.Widget;

public abstract class Browser<HosT extends Activity> extends Widget<HosT> implements DownloadListener, MainWebViewHost, PermissionsRequestCode {
    private MainWebView mWebView;

    public Browser(@NonNull Agency<HosT> agency) {
        super(agency);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getHost());
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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected boolean onInit() {
        if (hasValidHost()) {
            HosT host = getHost();
            mWebView = host.findViewById(R.id.webview);
            mWebView.setHost(this);
            mWebView.setWebViewClient(new MainWebViewClient());
            mWebView.setWebChromeClient(new MainWebChromeClient());
            mWebView.setDownloadListener(this);
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);

            return true;
        }
        return false;
    }

    @Nullable
    @Override
    protected HostCallback createHostCallback() {
        return null;
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
    public void clearUrl(WebView view, String url) {

    }

    private void downloadBySystem(String url, String contentDisposition, String mimetype) {

// Here, thisActivity is the current activity
        if (getHost().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (getHost().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                getHost().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
        final DownloadManager downloadManager = getHost().getSystemService(DownloadManager.class);
        if (null == downloadManager) {
            AppUtil.toast(getHost(), "can not find download manager");
            return;
        }
        // Start download
        long downloadId = downloadManager.enqueue(request);
        Log.d("downloadId:{}", "" + downloadId);
    }

    public MainWebView curWebView() {
        return mWebView;
    }


    public boolean goForward() {
        boolean canGoForward = curWebView().canGoForward();
        if (canGoForward) {
            curWebView().goForward();
        }
        return canGoForward;
    }

    public boolean goBack() {
        boolean canGoBack = curWebView().canGoBack();
        if (canGoBack) {
            curWebView().goBack();
        }
        return canGoBack;
    }

    public void reload() {
        curWebView().reload();
    }

    public void loadUrl(String url) {
        curWebView().loadUrl(url);
    }
}
