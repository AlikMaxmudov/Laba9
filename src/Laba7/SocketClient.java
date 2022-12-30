package Laba7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class SocketClient {
    private Socket mySocket;

    /**
     * open socket with port=80 (for http, not https)
     */
    public void openSocketForHttp(String host) {
        openSocketForHttp(host, new HttpRequestMeta());
    }

    /**
     * open socket with port=80 (for http, not https)
     */
    public void openSocketForHttp(String host, HttpRequestMeta requestMeta) {
        if (requestMeta == null) {
            openSocketForHttp(host);
        } else {
            try {
                mySocket = new Socket(host, 80);
                mySocket.setSoTimeout(requestMeta.timeout);
            } catch (IOException e) {
                closeSocket();
            }
        }
    }

    public void closeSocket() {
        try {
            if (mySocket != null) {
                mySocket.close();
            }
        } catch (IOException e) {
            System.out.println("got exception " + e + " when closing socket");
        }
    }

    /**
     * Открываем соккет, запрашиваем http, закрываем сокет.
     * Переопределеяем метод.
     */
    public void requestSingleHttp(String path, String host, SocketCallback callback) {
        requestSingleHttp(path, host, new HttpRequestMeta(), callback);
    }

    /**
     * open socket, request http, close socket.
     * The response is passed through callback
     */
    public void requestSingleHttp(String path, String host, HttpRequestMeta httpRequestMeta, SocketCallback callback) {
        if (httpRequestMeta == null) {
            requestSingleHttp(path, host, callback);
        } else {
            try {
                openSocketForHttp(host, httpRequestMeta);
                // Создаем сокет.
                if (mySocket != null && mySocket.isConnected()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(mySocket.getInputStream())); // Создаем буфер.
                    HttpRequestHelper.requestHttp1_1(mySocket.getOutputStream(), path, host);
                    callback.onSuccess(in);
                } else {
                    callback.onError(new SocketException("unable to connect to socket"));
                }
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                closeSocket();
            }
        }
    }

    /**
     * callback for getting the result of request sending with socket
     */
    public interface SocketCallback {
        void onSuccess(BufferedReader in);
        void onError(Exception e);
    }
}
