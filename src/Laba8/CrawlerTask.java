package Laba8;

import Laba7.HttpRequestMeta;
import Laba7.URLDepthPair;

import java.io.IOException;
import java.util.LinkedList;

/**
 * worker able to process single urls from urlPool until all required urls are not processed
 * */
class CrawlerTask implements Runnable {
    private final URLPool urlPool;
    private final CrawlerUnit crawler;

    CrawlerTask(URLPool urlPool, HttpRequestMeta requestMeta) {
        this.urlPool = urlPool;
        this.crawler = new CrawlerUnit(requestMeta);
    }

    @Override
    public void run() {
        while (Thread.currentThread().isAlive()) {
            URLDepthPair currentPair = urlPool.pollUrlPair();
            LinkedList<URLDepthPair> viewedLinks = new LinkedList<>();
            LinkedList<URLDepthPair> linksToSearch = new LinkedList<>();
            try {
                crawler.processSinglePath(currentPair, linksToSearch, viewedLinks);
            } catch (IOException e) {
                currentPair.setScanningException(e);
            } finally {
                urlPool.putUrlPairsToSearch(linksToSearch);
                urlPool.putViewedUrlPairs(viewedLinks);
            }
        }
    }
}