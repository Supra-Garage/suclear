package tw.supra.suclear.server;

import android.app.Activity;
import android.os.Environment;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.File;

/**
 * Created by wangjia20 on 2018/5/7.
 */

public final class SuServer extends AsyncHttpServer {


    private static boolean sFlagInited = false;

    public static void initIfNecessary(Activity activity) {
        if (!sFlagInited) {
            synchronized (SuServer.class) {
                if (!sFlagInited) {
                    init(activity);
                }
            }
        }
    }

    private static class ServerDirectory {
        final String regex;
        final File directory;
        final boolean list;

        private ServerDirectory(String regex, File directory, boolean list) {
            this.regex = regex;
            this.directory = directory;
            this.list = list;
        }
    }

    private static void init(Activity activity) {

        SuServer server = new SuServer();

        ServerDirectory[] directories = {
          new ServerDirectory("/sd1", new File(Environment.getExternalStorageDirectory(), "sd1"), true),
          new ServerDirectory("/sd2", new File(Environment.getExternalStorageDirectory(), "sd2"), true)
        };

        for (ServerDirectory directory : directories) {
            if (!directory.directory.exists()) {
                directory.directory.mkdirs();
            }
            if (directory.directory.isDirectory()) {
                server.directory(directory.regex, directory.directory, directory.list);
            }
        }

        server.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send("Hello!!!");
            }
        });


        server.setErrorCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(null != ex){
                    ex.printStackTrace();
                }
            }
        });
// listen on port 5000
        server.listen(5000);

// browsing http://localhost:5000 will return Hello!!!
    }
}
