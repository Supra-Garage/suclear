package tw.supra.suclear;


import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by supra on 17-7-23.
 */

public class MainWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
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
