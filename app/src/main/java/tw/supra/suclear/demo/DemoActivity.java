package tw.supra.suclear.demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import tw.supra.lib.supower.util.Logger;
import tw.supra.suclear.R;
import tw.supra.suclear.service.SuService;

public class DemoActivity extends Activity implements AdapterView.OnItemClickListener, ServiceConnection {

    private final Map<String, IBinder> mServices = new HashMap<>();

    private final Demo[] mDemoSet = {
            new Demo().setLabel("startService")
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startService(new Intent(view.getContext(), SuService.class));
                }
            }),

            new Demo().setLabel("stopService")
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    stopService(new Intent(view.getContext(), SuService.class));
                }
            }),

            new Demo().setLabel("bindService")
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    synchronized (mServices) {
                        bindService(new Intent(view.getContext(), SuService.class),
                                DemoActivity.this, 0);
                    }
                }
            }),

            new Demo().setLabel("unbindService")
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    synchronized (mServices) {
                        if (hasServiceConnection()) {
                            unbindService(DemoActivity.this);
                        }
                    }
                }
            })


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mDemoSet));
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDemoSet[position].onItemClick(parent, view, position, id);
    }

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
}
