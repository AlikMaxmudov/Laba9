package Laba7;

/**
 * Settings for socket, request
 * */
public class HttpRequestMeta {
    public static final int DEFAULT_TIMEOUT = 5000;

    public final int timeout;

    public HttpRequestMeta(int timeout) {
        this.timeout = timeout;
    }

    public HttpRequestMeta() {
        this.timeout = DEFAULT_TIMEOUT;
    }
}
