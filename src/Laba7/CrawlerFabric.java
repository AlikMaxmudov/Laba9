package Laba7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * pull of crawlers processing given urls synchronously
 * */
public class CrawlerFabric {

    private static final LinkedList<String> urlsToParse = new LinkedList<>();
    private static final int THREADS_COUNT = 5;

    public static void main(String[] args) {
        final ArrayList<Future<List<URLDepthPair>>> futures = new ArrayList<>();
        final int maxDepth;

        try {
            maxDepth = Integer.parseInt(args[args.length - 1]);
            urlsToParse.addAll(Arrays.asList(args).subList(0, args.length - 2));
        } catch (Exception e) {
            System.out.printf("unexpected arguments. Expected '[string...], [int]', got %s", Arrays.toString(args));
            return;
        }

        ExecutorService crawlers = Executors.newFixedThreadPool(THREADS_COUNT);
        for (int i = 0; i < THREADS_COUNT; i++) {
            futures.add(crawlers.submit(new CrawlerCallable(maxDepth)));
        }
        try {
            for (Future<List<URLDepthPair>> pairs : futures) {
                System.out.println(pairs.get());
            }
        } catch (Exception ignored) {
            System.out.println("unable to access result from futures");
        }
        crawlers.shutdownNow();
    }

    private static synchronized String pollUrl() {
        return urlsToParse.removeFirst();
    }

    /**
     * worker to transmit given urls until they are gone
     * */
    private static class CrawlerCallable implements Callable<List<URLDepthPair>> {

        private final int maxDepth;
        private final Crawler crawler = new Crawler();

        CrawlerCallable(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        @Override
        public List<URLDepthPair> call() {
            String url = pollUrl();
            while (url != null) {
                crawler.scan(url, maxDepth);
                url = urlsToParse.poll();
            }
            return crawler.getSites();
        }
    }
}