package com.example.WebSocket_Jetty_Test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

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
                Log.d(TAG, "Create client");
                WebSocketClient client = new WebSocketClient();
                Log.d(TAG, "Create socket");
                WebSocketTest socket = new WebSocketTest();
                Log.d(TAG, "Create URI");
                URI uri = new URI("wss://talkserver-test1.talk.hoccer.de/");

                Log.d(TAG, "Create UpgradeRequest");
                ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
                upgradeRequest.setSubProtocols("com.hoccer.talk.v5.bson");

                Log.d(TAG, "Start client");
                client.start();
                Log.d(TAG, "Connect client");
                client.connect(socket, uri, upgradeRequest);
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

    @WebSocket
    public class WebSocketTest {

        @OnWebSocketConnect
        public void onOpen(Session session) {
            Log.i(TAG, "onOpen()");
            setText("SUCCESS");
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            Log.i(TAG, "onClose(" + statusCode + ", " + reason + ")");
            setText("ERROR: " + statusCode + ", " + reason);
        }

        @OnWebSocketError
        public void onError() {

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
