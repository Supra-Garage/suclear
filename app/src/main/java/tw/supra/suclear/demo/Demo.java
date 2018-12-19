package tw.supra.suclear.demo;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

class Demo implements AdapterView.OnItemClickListener {

    private AdapterView.OnItemClickListener mOnItemClickListener;

    private String mLabel;

    public Demo setLabel(String label){
        mLabel = label;
        return this;
    }

    public Demo setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    public String toString() {
        return TextUtils.isEmpty(mLabel) ? super.toString() : mLabel;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mOnItemClickListener) {
            mOnItemClickListener.onItemClick(parent, view, position, id);
        }
    }




}
