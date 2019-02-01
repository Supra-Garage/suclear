package tw.supra.suclear;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by supra on 17-7-23.
 */

public interface MainWebViewHost {

    @Nullable
    WebViewClient getWebViewClient();

    @Nullable
    WebChromeClient getWebChromeClient();

    @Nullable
    WebSuclearClient getWebSuclearClient();

    void setTitle(WebView view, String title);

    void setUrl(WebView view, String url);

    void clearUrl(WebView view, String url);

    void setIcon(WebView view, Bitmap icon);

    void setTouchIconUrl(WebView view, String url, boolean precomposed);

    void changeProgress(WebView view, int newProgress);


}
