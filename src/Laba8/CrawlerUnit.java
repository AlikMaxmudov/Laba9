package Laba8;

import Laba7.HtmlParser;
import Laba7.HttpRequestMeta;
import Laba7.SocketClient;
import Laba7.URLDepthPair;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;

/**
 * один веб-сканер для обработки одного URL
 */
public class CrawlerUnit {

    private final HttpRequestMeta requestMeta;

    public CrawlerUnit(HttpRequestMeta requestMeta) {
        this.requestMeta = requestMeta;
    }

    /**
     * Подключиться к указанному ЮРЛ.,
     * добавить найденные ЮРЛ адреса в linksToSearch,
     * добавить указынные ЮРЛ в  viewedLinks,
     * Можем обратабывать постоянную ошибку 301.
     *
     * @throws MalformedURLException for invalid urls
     */
    public void processSinglePath(
            final URLDepthPair currentPair,
            final LinkedList<URLDepthPair> linksToSearch,
            final LinkedList<URLDepthPair> viewedLinks
    ) throws MalformedURLException {
        SocketClient socketClient = new SocketClient();
        socketClient.requestSingleHttp(currentPair.getPath(), currentPair.getHost(), requestMeta, new SocketClient.SocketCallback() {
            @Override
            public void onSuccess(BufferedReader in) {
                try {
                    // сканирование на 301 ошибку. сканировать ссылки в документе, если документ правильный
                    String redirectLocation = HtmlParser.scanForRedirect(currentPair.getUrl(), in);
                    if (redirectLocation != null) {
                        currentPair.setUrl(redirectLocation);
                        linksToSearch.add(currentPair);
                    } else {
                        for (String url : HtmlParser.scanHttpLinks(in)) {
                            linksToSearch.add(new URLDepthPair(url, currentPair.loadingDeps + 1));
                        }
                        viewedLinks.add(currentPair);
                    }
                } catch (IOException e) {
                    currentPair.setScanningException(e);
                    viewedLinks.add(currentPair);
                }
            }

            @Override
            public void onError(Exception e) {
                currentPair.setScanningException(e);
                viewedLinks.add(currentPair);
            }
        });
    }
}