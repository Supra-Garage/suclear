package tw.supra.suclear;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;

import javax.xml.validation.Schema;

/**
 * Created by supra on 17-7-23.
 */

public class MainWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        String url = request.getUrl().toString();
        if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
            // This is web site, so do not override; let my WebView load the page
            return super.shouldOverrideUrlLoading(view, request);
        }


        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent i = new Intent(Intent.ACTION_VIEW, request.getUrl());

        Context context = view.getContext();
        ComponentName componentName = i.resolveActivity(context.getPackageManager());
        if (null == componentName) {
            return false;
        }
        new AlertDialog.Builder(context)
                .setMessage(componentName.toString())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> context.startActivity(i))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        return true;
    }

    @Override
    public void onPageStarted(WebView view, final String url, final Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        HostInvoker.invoke(new HostInvoker<Void, Void>() {
            @Override
            public Void invoke(MainWebViewHost host, MainWebView view, Void... args) {
                host.setIcon(view, favicon);
                host.setUrl(view, url);
                return null;
            }
        }, view);
    }

    @Override
    public void onPageFinished(WebView view, final String url) {
        super.onPageFinished(view, url);
        HostInvoker.invoke(new HostInvoker<Void, Void>() {
            @Override
            Void invoke(MainWebViewHost host, MainWebView view, Void... args) {
                host.clearUrl(view, url);
                return null;
            }
        }, view);
    }
}
