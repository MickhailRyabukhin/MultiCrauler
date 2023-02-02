import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleUtils {
    public static final Set<String> nodoubles = new HashSet<>();

    private static final Queue<String> links = new LinkedList<>();
    private static final ArrayList<String> FIRST_PAGES = MultiSiteCrawlingFJP.FIRST_PAGES;
    private static final String regexUrl = "^(https?|ftp|file)://" + "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[^#!:,.;]$";
    private static final String badUrl = "^(https?|ftp|file):\\/\\/.*(\\?(https?|ftp|file):\\/\\/).*";
    private static final Pattern patt = Pattern.compile(regexUrl);
    private static final Pattern badpatt = Pattern.compile(badUrl);
    public static long start;
    private static volatile int index;
    public static boolean stop = false;
    private static final ArrayList<Integer> indexes = new ArrayList<>();


    public SingleUtils() {
        indicator(0);
        links.addAll(FIRST_PAGES);
    }

    public static ArrayList<String> getLinks(String url) {
        ArrayList<String> urls = new ArrayList<>();
        if (stop) {
            return urls;
        }

        crawling(url);
        while (!links.isEmpty()) {
            urls.add(links.poll());
        }
        return urls;
    }

    public static void crawling(String url) {
        if (url == null || url.equals("")) {
            return;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }
        Document doc = null;
        try {
            Connection con = Jsoup.connect(url);
            doc = con.ignoreContentType(true).ignoreHttpErrors(true).followRedirects(false)
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) " +
//                            "SiteCraulerMIR Firefox/2.0.0.6")
//                    .referrer("http://www.google.com")
                    .get();
            String conType = con.response().contentType();
            if (conType == null || conType.equals("")) {
                return;
            }
            if (!conType.startsWith("text/html")) {
                return;
            }
        } catch (IOException ignored) {
        }
        if (doc == null) {
            return;
        }
        Elements adlinks = doc.select("a");
        String newURL;
        doc = null;
        for (Element adlink : adlinks) {
            newURL = adlink.attr("abs:href");
            if (!isMatchURL(newURL)) {
                continue;
            }
            boolean found = false;
            newURL = newURL.toLowerCase(); //.replaceFirst("www", "");
            for (int i = 0; i < FIRST_PAGES.size() && !found; i++) {
                if (newURL.startsWith(FIRST_PAGES.get(i))) {
                    index = i;
                    found = true;
                }
            }
            if (!found) {
                continue;
            }

            synchronized (nodoubles) {
                if (!nodoubles.add(newURL)) {
                    continue;
                }
            }
            indexes.add(index);
            links.add(newURL);
            indicator(nodoubles.size());
        }
        adlinks = null;
    }


    private static boolean isMatchURL(String s) {
        try {
            final Matcher badMatcher = badpatt.matcher(s);
            final Matcher matcher = patt.matcher(s);
            return matcher.matches() && !badMatcher.matches();
        } catch (final RuntimeException e) {
            return false;
        }
    }


    private static void indicator(int count) {

        if (count <= 1) {
            start = System.currentTimeMillis();
        }
        if (count % 1000 == 0) {
            long time = System.currentTimeMillis();
            for (int i = 0; i < FIRST_PAGES.size(); i++) {
                System.out.print(FIRST_PAGES.get(i) + "\t" + Collections.frequency(indexes, i) + "\t");
            }
            System.out.println("\t- Итого " + count + " ссылок, общее время " + (time - start) / 1000 + " c ");
           System.gc();
            return;
        }
//        if (count % 100 == 0) {
//            System.gc();
//        }
    }


    public static class SingletonHolder {

        private static final SingleUtils HOLDER_INSTANCE = new SingleUtils();

    }

    public static SingleUtils getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }


}

