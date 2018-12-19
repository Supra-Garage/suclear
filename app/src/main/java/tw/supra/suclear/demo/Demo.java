package tw.supra.suclear.demo;

import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

import tw.supra.suclear.utils.typedbox.TypedCallback;

class Demo {

    public final String name;
    private String mDesc;

    private final Map<String, Action> mActionPool = new LinkedHashMap<>();
    private final ArraySet<String> mActions = new ArraySet<>();

    Demo(String name) {
        this.name = name;
    }

    public Demo setDesc(String desc) {
        mDesc = desc;
        return this;
    }

    public String getDesc() {
        return TextUtils.isEmpty(mDesc) ? "" : mDesc;
    }

    public Demo addAction(String... actions) {
        for (String action : actions) {
            addAction(new Action(action));
        }
        return this;
    }

    public Demo addAction(String action, TypedCallback<Action> defaultImpl) {
        addAction(new Action(action, defaultImpl));
        return this;
    }

    private Demo addAction(Action action) {
        if (action.isLegal) {
            mActionPool.put(action.name, action);
            mActions.add(action.name);
        }
        return this;
    }

    @Override
    public String toString() {
        return TextUtils.isEmpty(name) ? super.toString() : name;
    }


    protected boolean onActionClick(String action) {
        return false;
    }

    public class Action {

        public final String name;
        final TypedCallback<Action> defaultImpl;
        final boolean isLegal;

        private Action(String name) {
            this(name, null);
        }

        private Action(String name, TypedCallback<Action> defaultImpl) {
            this.name = name;
            this.defaultImpl = defaultImpl;
            isLegal = !TextUtils.isEmpty(name);
        }

        public void onClick() {
            if (!onActionClick(name) && null != defaultImpl) {
                defaultImpl.onCallback(this);
            }
        }

    }

    public int actionSize() {
        return mActionPool.size();
    }

    public Action getAction(int index) {
        return mActionPool.get(mActions.valueAt(index));
    }

}
