package tw.supra.suclear.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import tw.supra.lib.supower.util.Logger;

public class SuService extends Service {


    /** Command to the service to display a message */
    static final int MSG_SAY_HELLO = 1;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());


    public SuService() {
        Log.i(Logger.getStackTag(), "SuService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Logger.getStackTag(), "onBind " +
                "\n intent: " + intent.toString());
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        Log.i(Logger.getStackTag(), "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Logger.getStackTag(), "onStartCommand" +
                "\n intent: " + intent.toString() +
                "\n flags: " + flags +
                "\n startId" + startId
        );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(Logger.getStackTag(), "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(Logger.getStackTag(), "onUnbind " +
                "\n intent: " + intent.toString());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(Logger.getStackTag(), "onRebind " +
                "\n intent: " + intent.toString());
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(Logger.getStackTag(), "onTaskRemoved " +
                "\n rootIntent: " + rootIntent.toString());
        super.onTaskRemoved(rootIntent);
    }

}
