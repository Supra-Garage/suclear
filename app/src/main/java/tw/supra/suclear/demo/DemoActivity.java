package tw.supra.suclear.demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import tw.supra.lib.supower.util.Logger;
import tw.supra.suclear.R;
import tw.supra.suclear.service.SuService;
import tw.supra.suclear.utils.typedbox.TypedCallback;

public class DemoActivity extends Activity implements ServiceConnection, AdapterView.OnItemClickListener,
//        ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnChildClickListener {

    private final Map<String, IBinder> mServices = new HashMap<>();

    private final Demo[] mDemoSet = {
            new Demo("Service").setDesc("服务组件的生命周期")
                    .addAction("startService", msg -> startService(
                            new Intent(DemoActivity.this, SuService.class)))
                    .addAction("stopService", msg -> stopService(
                            new Intent(DemoActivity.this, SuService.class)))
                    .addAction("bindService", msg -> {
                        synchronized (mServices) {
                            bindService(new Intent(DemoActivity.this, SuService.class),
                                    DemoActivity.this, 0);
                        }
                    })
                    .addAction("unbindService", msg -> {
                synchronized (mServices) {
                    if (hasServiceConnection()) {
                        unbindService(DemoActivity.this);
                    }
                }
            }),
            new Demo("PipedSteam").setDesc("流").addAction("A", "B", "C")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ExpandableListView exListView = findViewById(R.id.ex_list_view);
        exListView.setAdapter(new Adapter());
        exListView.setOnChildClickListener(this);
        exListView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(Logger.getStackTag(), "position=" + position + " id=" + id);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        ExpandableListAdapter expandableListAdapter = parent.getExpandableListAdapter();
        if (expandableListAdapter instanceof Adapter) {
            Adapter adapter = (Adapter) expandableListAdapter;
            adapter.getChild(groupPosition, childPosition).onClick();
            return true;
        }
        return false;
    }

//    @Override
//    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//        return false;
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(Logger.getStackTag(), "name: " + name + "  service: " + service);
        synchronized (mServices) {
            mServices.put(name.flattenToString(), service);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(Logger.getStackTag(), "name: " + name);
        synchronized (mServices) {
            mServices.remove(name.flattenToString());
        }
    }

    private boolean hasServiceConnection() {
        synchronized (mServices) {
            return !mServices.isEmpty();
        }
    }

    private class Adapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mDemoSet.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getGroup(groupPosition).actionSize();
        }

        @Override
        public Demo getGroup(int groupPosition) {
            return mDemoSet[groupPosition];
        }

        @Override
        public Demo.Action getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition).getAction(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return getGroup(groupPosition).hashCode();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return getChild(groupPosition, childPosition).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(parent.getContext(), android.R.layout.simple_expandable_list_item_2, null);
            }

            Demo demo = getGroup(groupPosition);

            TextView textView1 = convertView.findViewById(android.R.id.text1);
            TextView textView2 = convertView.findViewById(android.R.id.text2);
            textView1.setText(demo.name);
            textView2.setText(demo.getDesc());

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(parent.getContext(), android.R.layout.simple_expandable_list_item_1, null);
            }

            TextView textView1 = convertView.findViewById(android.R.id.text1);
            textView1.setText(getChild(groupPosition, childPosition).name);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
