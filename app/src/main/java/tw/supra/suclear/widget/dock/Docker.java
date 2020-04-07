package tw.supra.suclear.widget.dock;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import tw.supra.suclear.GlideApp;
import tw.supra.suclear.GlideRequest;
import tw.supra.suclear.R;
import tw.supra.suclear.widget.AbsWidget;

public class Docker<HosT extends Activity> extends AbsWidget<HosT>  {
    private ImageView mViewIcon;

    public Docker(HosT host) {
        super(host);
    }

    @Override
    protected boolean onInit() {
        if (hasValidHost()) {
            mViewIcon = getHost().findViewById(R.id.icon);
            return true;
        }
        return false;
    }


    public void setIcon(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            setIcon(GlideApp.with(getHost()).load(imgUrl));
        }
    }

    public void setIcon(@NonNull Bitmap img) {
        setIcon(GlideApp.with(getHost()).load(img));
    }

    private <TranscodeType> void setIcon(@NonNull GlideRequest<TranscodeType> glideRequest) {
        if (initIfNecessary()) {
            glideRequest.centerCrop().transform(new RoundedCorners(10)).into(mViewIcon);
        }
    }

}
