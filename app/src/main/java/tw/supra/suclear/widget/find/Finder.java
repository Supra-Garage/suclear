package tw.supra.suclear.widget.find;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tw.supra.suclear.R;
import tw.supra.suclear.widget.Agency;
import tw.supra.suclear.widget.Widget;

public class Finder<HosT extends Activity> extends Widget<HosT>
        implements WebView.FindListener, View.OnClickListener, TextWatcher {
    private View mViewRoot;
    private TextView mFindCount;
    private EditText mFindKey;
    private @Nullable
    WebView mWebView;

    public Finder(@NonNull Agency<HosT> agency) {
        super(agency);
    }

    @Nullable
    @Override
    protected HostCallback createHostCallback() {
        return null;
    }

    @Override
    protected boolean onInit() {
        if (hasValidHost()) {
            HosT host = getHost();
            mViewRoot = host.findViewById(R.id.find_container);
            mFindCount = host.findViewById(R.id.tv_find_count);
            mFindKey = host.findViewById(R.id.et_find_key);
            host.findViewById(R.id.btn_find_cancel).setOnClickListener(this);
            host.findViewById(R.id.btn_find_next).setOnClickListener(this);
            host.findViewById(R.id.btn_find_privous).setOnClickListener(this);
            mFindKey.addTextChangedListener(this);
            return true;
        }
        return false;
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find_privous:
                findNext(false);
                break;
            case R.id.btn_find_next:
                findNext(true);
                break;
            case R.id.btn_find_cancel:
            default:
                cancel();
                break;
        }

    }

    @Override
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        mFindCount.setText(activeMatchOrdinal + "/" + numberOfMatches);
    }

    public void toggleFind(@Nullable WebView webView) {
        if (!openFind(webView)) {
            cancel();
        }
    }

    private void findNext(boolean forward) {
        if (null != mWebView) {
            mWebView.findNext(false);
        }
    }

    public boolean openFind(@Nullable WebView webView) {
        if (null != webView && mWebView != webView) {
            cancel();
            mWebView = webView;
            mViewRoot.setVisibility(View.VISIBLE);
            mFindKey.requestFocus();
            mWebView.setFindListener(this);
            return true;
        }
        return false;
    }

    public void cancel() {
        if (null != mWebView) {
            mFindKey.setText("");
            mViewRoot.setVisibility(View.GONE);
            mWebView.setFindListener(null);
            mWebView = null;
        }
    }

}
