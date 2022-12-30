package Laba8;

import Laba7.URLDepthPair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Ресурс с URL-адресами, которые необходимо просканировать, и уже просканированные Urls
 * */
public class URLPool {
    private final int maxDepth;
    /** количество подсчетов, ожидающих URL */
    private int sleepWorkersCount;
    /** максимальное количество подсчетов */
    private final int threadsCount;

    private final Set<String> seenUrls = new HashSet<>();

    private final LinkedList<URLDepthPair> linksToSearch = new LinkedList<>();
    private final LinkedList<URLDepthPair> viewedPairs = new LinkedList<>();

    public URLPool(int maxDepth, int threadsCount) {
        this.maxDepth = maxDepth;
        this.threadsCount = threadsCount;
    }

    /** добавить URL-адрес в набор поиска, если он должен быть обработан, или добавить в набор результатов
     */
    public synchronized void putUrlPairToSearch(URLDepthPair pair) {
        if (isUnknownUrl(pair.getUrl())) {
            if (pair.loadingDeps < maxDepth) {
                linksToSearch.add(pair);
                notify();
            } else {
                viewedPairs.add(pair);
            }
        }
    }

    /** добавить несколько URL-адресов в коллекцию поиска, если она должна быть обработана, или добавить в коллекцию результатов
     */
    public synchronized void putUrlPairsToSearch(List<URLDepthPair> pairs) {
        for (URLDepthPair pair : pairs) {
            if (isUnknownUrl(pair.getUrl())) {
                if (pair.loadingDeps < maxDepth) {
                    linksToSearch.add(pair);
                } else {
                    viewedPairs.add(pair);
                }
            }
        }
        notifyAll();
    }

    /** добавить несколько URL-адресов в коллекцию результатов */
    public synchronized void putViewedUrlPairs(List<URLDepthPair> pairs) {
        viewedPairs.addAll(pairs);
    }

    /** получить первый URL-адрес, необходимый для обработки
     */
    public synchronized URLDepthPair pollUrlPair() {
        sleepWorkersCount++;
        try {
            while (linksToSearch.isEmpty()) {
                wait();
            }
        } catch (InterruptedException ignored) {
        }
        sleepWorkersCount--;
        return linksToSearch.removeFirst();
    }

    /** @return false, если все URL-адреса были обработаны */
    public boolean isInProgress() {
        return sleepWorkersCount != threadsCount;
    }

    /**
     * @return все найденные пары
     * */
    public List<URLDepthPair> getResult() {
        return viewedPairs;
    }

    /**
     * @return true, если данный URL-адрес был основан
     */
    private boolean isUnknownUrl(final String url) {
        return seenUrls.add(url);
    }
}