import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class MultSite_crauling2 {
    public static ArrayList<String> FIRST_PAGES =
            new ArrayList<>(Arrays.asList("https://playback.ru/", "https://skillbox.ru/", "https://volochek.life/"));
    public static long start;

    public static void main(String[] args) {
        System.out.println(FIRST_PAGES);
        start = System.currentTimeMillis();
        ArrayList<Crauler> tasks = new ArrayList<>();
        for (String firstPage : FIRST_PAGES) {
            new Starter(firstPage).start();
        }
        System.out.println("\n\t  время " + (System.currentTimeMillis() - start) / 1000 + "\n" +
                "найдено " + SingleUtils.nodoubles.size());
    }

    private static class Starter extends Thread {
        private String link;
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
