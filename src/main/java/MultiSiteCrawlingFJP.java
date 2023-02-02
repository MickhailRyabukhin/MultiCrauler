import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.*;

public class MultiSiteCrawlingFJP {

    public static ArrayList<String> FIRST_PAGES =
            new ArrayList<>(Arrays.asList("https://playback.ru/", "https://skillbox.ru/", "https://volochek.life/"));
    public static long start;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nURLs list for parsing "+FIRST_PAGES);
        start = System.currentTimeMillis();
        for (String firstPage : FIRST_PAGES) {
           new Starter(firstPage).start();
        }
        System.out.println("Parsing started");
        while (!SingleUtils.stop){
            SingleUtils.stop=scanner.nextBoolean();
        }

    }

    private static class Starter extends Thread {
        private final String link;
        public Starter(String link) {
            this.link = link;
        }
        ForkJoinPool Worker = new ForkJoinPool();
        @Override
        public void run() {
            Worker.invoke(new Crauler(link));
            Worker.shutdown();
        }


    }
}
