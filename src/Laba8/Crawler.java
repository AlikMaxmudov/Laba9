package Laba8;

import Laba7.HttpRequestMeta;
import Laba7.URLDepthPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * web-scanner
 */
public class Crawler {

    public static void main(String[] args) {
        final String url;
        final int maxDepth;
        final int threadsCount;
        int timeout = -1;
        try {
            url = args[0];
            maxDepth = Integer.parseInt(args[1]);
            threadsCount = Integer.parseInt(args[2]);
            if (args.length == 4) {
                timeout = Integer.parseInt(args[3]);
            }
        } catch (Exception e) {
            System.out.printf("unexpected arguments. Expected '[string], [int], [int]', got %s", Arrays.toString(args));
            return;
        }
        System.out.println(new Crawler().process(url, maxDepth, threadsCount, timeout));
    }

    public List<URLDepthPair> process(String url, int maxDepth, int threadsCount, int timeout) {
        HttpRequestMeta httpRequestMeta;
        if (timeout == -1) {
            httpRequestMeta = new HttpRequestMeta();
        } else {
            httpRequestMeta = new HttpRequestMeta(timeout);
        }

        final URLPool urlPool = new URLPool(maxDepth, threadsCount);
        ExecutorService crawlers = Executors.newFixedThreadPool(threadsCount);

        final ArrayList<CrawlerTask> tasks = new ArrayList<>();
        urlPool.putUrlPairToSearch(new URLDepthPair(url, 0));

        for (int i = 0; i < threadsCount; i++) {
            tasks.add(new CrawlerTask(urlPool, httpRequestMeta));
            crawlers.submit(tasks.get(tasks.size() - 1));
        }
        while (urlPool.isInProgress()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        crawlers.shutdownNow();
        return urlPool.getResult();
    }
}