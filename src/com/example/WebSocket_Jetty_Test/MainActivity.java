package com.example.WebSocket_Jetty_Test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends Activity {

    private static final String TAG = "WebSocketTest";

    private TextView textView;

    private Runnable webSocketRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                WebSocketClientFactory clientFactory = new WebSocketClientFactory();
                clientFactory.start();

                WebSocketClient client = clientFactory.newWebSocketClient();
                client.setProtocol("com.hoccer.talk.v5.bson");

                URI uri = new URI("wss://talkserver-test1.talk.hoccer.de/");
                WebSocket websocket = new WebSocketTest();
                client.open(uri, websocket);
            } catch (Exception e) {
                Log.e(TAG, "Something went wrong", e);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.tv_hello);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
        executorService.execute(webSocketRunnable);
    }

    public class WebSocketTest implements WebSocket {

        public static final String TAG = "WebSocketTest";

        @Override
        public void onOpen(Connection connection) {
            Log.i(TAG, "onOpen()");
            setText("SUCCESS");
        }

        @Override
        public void onClose(int i, String s) {
            Log.i(TAG, "onClose(" + i + ", " + s + ")");
            setText("ERROR: " + i + ", " + s);
        }

        private void setText(final String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(s);
                }
            });
        }
    }
}
