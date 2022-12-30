package Laba7;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * web-scanner
 */
public class Crawler {

    private final LinkedList<URLDepthPair> linksToSearch = new LinkedList<>();
    private final LinkedList<URLDepthPair> viewedLinks = new LinkedList<>();

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        String url;
        int maxDepth;
        try {
            url = args[0]; // Задаем нулевый аргумент, чтобы чтобы программа согласилась с нами.
            maxDepth = Integer.parseInt(args[1]); // Мы преобразуем число в строку.
        } catch (Exception e) {  // Если нет, то мы выводим ошибку.
                System.out.printf("unexpected arguments. Expected '[string], [int]', got %s", Arrays.toString(args));
            return;
        }
        System.out.println("found: " + crawler.scan(url, maxDepth));
    }

    /**
     * возвращать список всех пар URL-глубины, которые были посещены..
     */
    public LinkedList<URLDepthPair> getSites() {
        return viewedLinks;
    }

    /**
     * поиск всех ссылок в html-документе для URL-адреса, запрос этих документов ссылок и циклическая операция
     *
     * @return all found valid links.
     */
    public LinkedList<URLDepthPair> scan(String startUrl, int maxDepth) {
        try {
            linksToSearch.add(new URLDepthPair(startUrl, 0));
            // Пока поиск строки не равно истине, то мы удаляем первый запрос.
            while (!linksToSearch.isEmpty())  {
                URLDepthPair currentPair = linksToSearch.removeFirst();
                // если наши нынешние ссылки больше чем макс.поиска
                if (currentPair.loadingDeps < maxDepth) {
                    processSinglePath(currentPair);
                } else {
                    viewedLinks.add(currentPair);
                }
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("usage: java Laba7.Crawler " + startUrl + " " + maxDepth + "\n exception: " + e);
        }
        return getSites();
    }

    private void processSinglePath(final URLDepthPair currentPair) throws IOException {
        /*if (isKnownUrl(currentPair.getUrl())) {
            return;
        }*/
        SocketClient socketClient = new SocketClient();
        socketClient.requestSingleHttp(currentPair.getPath(), currentPair.getHost(), new SocketClient.SocketCallback() {
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

    /**
     * @return true, если данный URL-адрес был основан
     */
    private boolean isKnownUrl(final String url) {
        return (isListContainsUrl(linksToSearch, url) || isListContainsUrl(viewedLinks, url));
    }

    private boolean isListContainsUrl(List<URLDepthPair> list, String url) {
        for (URLDepthPair urlPair : list) {
            if (url.equals(urlPair.getUrl())) {
                return true;
            }
        }
        return false;
    }

}

