package tw.supra.suclear;

import android.webkit.WebView;

/**
 * Created by supra on 17-7-23.
 */
public abstract class HostInvoker<ReturnT, ArgT> {

    static MainWebViewHost getHost(WebView view) {
        if (view instanceof MainWebView) {
            return ((MainWebView) view).getHost();
        }
        return null;
    }

    static <ReturnT, ArgT> ReturnT invoke(HostInvoker<ReturnT, ArgT> invoker, WebView view, ArgT... args) {
        MainWebViewHost host = getHost(view);
        if (null == host) {
            return null;
        }
        return invoker.invoke(host, (MainWebView) view, args);
    }

    abstract ReturnT invoke(MainWebViewHost host, MainWebView view, ArgT... args);
}
