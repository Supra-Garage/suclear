package tw.supra.suclear;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import javax.xml.validation.Schema;

import tw.supra.lib.supower.util.Logger;

/**
 * Created by supra on 17-7-23.
 */
public class MainWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, WebResourceRequest request) {

        final Uri uri = request.getUrl();
        String url = uri.toString();
        if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
            // This is web site, so do not override; let my WebView load the page
            return super.shouldOverrideUrlLoading(view, request);
        }

        Intent i = new Intent(Intent.ACTION_VIEW, request.getUrl());
        final Context context = view.getContext();
        ComponentName componentName = i.resolveActivity(context.getPackageManager());
        if (null == componentName) {
            return true;
        }

        String host = "";
        String curUrl = view.getUrl();
        if (!TextUtils.isEmpty(curUrl)) {
            Uri curUri = Uri.parse(curUrl);
            host = curUri.getHost();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setIcon(getPkgIcon(context, componentName));
        String name = getPkgLabel(context, componentName);
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        builder.setTitle(android.R.string.dialog_alert_title)
                .setMessage(host + " " + context.getText(R.string.ask_to_open) + " " + name)
//                .setMessage(componentName.toString())
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

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
//        new AlertDialog.Builder(view.getContext()).setTitle("onReceivedSslError")
//                .setMessage(error.toString())
//                .setPositiveButton(android.R.string.ok, null)
//                .show();
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
//        new AlertDialog.Builder(view.getContext()).setTitle("onReceivedHttpError")
//                .setMessage(toString(request) + toString(errorResponse))
//                .setPositiveButton(android.R.string.ok, null)
//                .show();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
//        new AlertDialog.Builder(view.getContext()).setTitle("onReceivedError")
//                .setMessage(toString(request) + toString(error))
//                .setPositiveButton(android.R.string.ok, null)
//                .show();
    }

    private Drawable getPkgIcon(Context context, ComponentName name) {
        try {
            return context.getPackageManager().getActivityIcon(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return context.getDrawable(R.mipmap.ic_logo);
        }
    }

    private String getPkgLabel(Context context, ComponentName name) {
        try {

            PackageInfo info = context.getPackageManager().getPackageInfo(
                    name.getPackageName(), PackageManager.GET_ACTIVITIES
                            | PackageManager.GET_CONFIGURATIONS
                            | PackageManager.GET_GIDS
                            | PackageManager.GET_INSTRUMENTATION
                            | PackageManager.GET_INTENT_FILTERS
                            | PackageManager.GET_META_DATA
                            | PackageManager.GET_PERMISSIONS
                            | PackageManager.GET_PROVIDERS
                            | PackageManager.GET_RECEIVERS
                            | PackageManager.GET_SERVICES
                            | PackageManager.GET_SHARED_LIBRARY_FILES
                            | PackageManager.GET_URI_PERMISSION_PATTERNS
                            | PackageManager.GET_DISABLED_COMPONENTS
                            | PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS
                            | PackageManager.GET_UNINSTALLED_PACKAGES
//                    | PackageManager.MATCH_UNINSTALLED_PACKAGES
//                    | PackageManager.MATCH_DISABLED_COMPONENTS
//                    | PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS
//                    | PackageManager.MATCH_SYSTEM_ONLY
//                    | PackageManager.GET_SIGNATURES
            );

            if (null != info) {
                return info.applicationInfo.name;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name.getPackageName();
    }


    private static String toString(WebResourceError error) {
        return String.format(Locale.getDefault(), "WebResourceError(%d) \n desc: %s", error.getErrorCode(), error.getDescription());
    }

    private static String toString(String tag, Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(String.format("%s :: { %s : %s }\n", tag, entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }

    private static String toString(WebResourceRequest request) {
        StringBuilder msg = new StringBuilder();
        msg.append(String.format("Url: %s \n Method(%s) \n", request.getUrl(), request.getMethod()));
        msg.append(" Request Headers: \n");
        msg.append(toString("  ", request.getRequestHeaders()));
        msg.append(String.format("Url: %s\n Method: %s\n", request.getUrl(), request.getMethod()));
        return msg.toString();
    }

    private static String toString(WebResourceResponse errorResponse) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(errorResponse.getData()));
        StringBuilder msg = new StringBuilder();
        msg.append(String.format(Locale.getDefault(),
                "StatusCode(%d) Mime(%s) Encodin(%s) \n ReasonPhrase: %s \n",
                errorResponse.getStatusCode(),
                errorResponse.getMimeType(), errorResponse.getEncoding(),
                errorResponse.getReasonPhrase()));

        msg.append(" Data: \n");
        String data;
        try {
            int i = 0;
            int limit = 100;
            while ((data = reader.readLine()) != null) {
                if (++i < limit) {
                    msg.append(data).append("\n");
                } else if (i == limit) {
                    msg.append("see log for more");
                } else {
                    msg.append(".");
                }
                Log.i(Logger.getStackTag("onReceivedHttpError"), "data:: " + data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            msg.append(e.toString());
        }

        msg.append(" Response Headers: \n");
        msg.append(toString("  ", errorResponse.getResponseHeaders()));
        return msg.toString();
    }


}
