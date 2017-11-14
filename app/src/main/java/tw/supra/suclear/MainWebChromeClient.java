package tw.supra.suclear;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by supra on 17-7-23.
 */

public class MainWebChromeClient extends WebChromeClient {
 
    @Override
    public void onReceivedTitle(WebView view, final String title) {
        super.onReceivedTitle(view, title);
        HostInvoker.invoke(new HostInvoker<Void, Void>() {
            @Override
            Void invoke(MainWebViewHost host, MainWebView view, Void... args) {
                host.setTitle(view, title);
                return null;
            }
        }, view);
    }

    @Override
    public void onReceivedIcon(WebView view, final Bitmap icon) {
        super.onReceivedIcon(view, icon);
        HostInvoker.invoke(new HostInvoker<Void, Void>() {
            @Override
            Void invoke(MainWebViewHost host, MainWebView view, Void... args) {
                host.setIcon(view, icon);
                return null;
            }
        }, view);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, final String url, final boolean precomposed) {
        super.onReceivedTouchIconUrl(view, url, precomposed);
        HostInvoker.invoke(new HostInvoker<Void, Void>() {
            @Override
            Void invoke(MainWebViewHost host, MainWebView view, Void... args) {
                host.setTouchIconUrl(view, url, precomposed);
                return null;
            }
        }, view);
    }

    @Override
    public void onProgressChanged(WebView view, final int newProgress) {
        super.onProgressChanged(view, newProgress);
        HostInvoker.invoke(new HostInvoker<Void, Void>() {
            @Override
            Void invoke(MainWebViewHost host, MainWebView view, Void... args) {
                host.changeProgress(view, newProgress);
                return null;
            }
        }, view);
    }
}
