package Laba7;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Util for sending http requests
 * */
public class HttpRequestHelper {

    /**
     *  send get request with given outputStream. Close connection right after response
     * */
    public static void requestHttp1_1(OutputStream outputStream, String path, String host) {
        PrintWriter out = new PrintWriter(outputStream, true);
        out.println("GET " + path+ " HTTP/1.1");
        out.println("Host: " + host);
        out.println("Connection: close");
        out.println();
        out.flush();
    }
}