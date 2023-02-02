import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

@Getter
@Setter
public class Crauler extends RecursiveAction {

    private String link;
    private List<String> links = new ArrayList<>();
    private List<Crauler> tasks = new ArrayList<>();

    public Crauler(String link) {
        this.link = link;
    }

    public Crauler(List<String> links) {
        this.links = links;
    }

    @Override
    protected void compute() {
        if (links.isEmpty()) {
            links = SingleUtils.getLinks(link);
        }
        for (String link : links) {
            Crauler task = new Crauler(link);
            task.fork();
        }
        for (Crauler task : tasks) {
            task.join();
        }

        tasks = null;
    }
}
