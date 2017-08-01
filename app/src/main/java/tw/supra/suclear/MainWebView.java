package tw.supra.suclear;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by supra on 17-7-23.
 */

public class MainWebView extends WebView {
    private MainWebViewHost mHost;

    public MainWebView(Context context) {
        super(context);
    }

    public MainWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MainWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    public void setHost(MainWebViewHost host) {
        mHost = host;
    }

    public MainWebViewHost getHost() {
        return mHost;
    }
}
