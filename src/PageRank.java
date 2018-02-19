import java.io.*;
import java.util.*;

public class PageRank {

    private static final float lambda = 0.2f;
    private static final float tau = 0.02f;
    private static ArrayList<String> pages = new ArrayList<>();
    private static Map<String, Float> I = new HashMap<>();
    private static Map<String, Float> R = new HashMap<>();

    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();
        PageRank pr = new PageRank();
        Tuple<Map<String, Integer>, Map<String, Integer>, Map<String, ArrayList<String>>, Map<String, ArrayList<String>>> links = pr.populate();
        final int N = pages.size();
        for (int i = 0; i < pages.size(); i++) {
            I.put(pages.get(i), 1.0f / pages.size());
        }

        //Get the inlinks and sort them by descending count
        Map<String, Integer> inlinks = MapUtil.sortByValue(links.w);
        PrintWriter writer = new PrintWriter("inlinks.txt", "UTF-8");
        int index = 0;
        for (Map.Entry<String, Integer> entry : inlinks.entrySet()) {
            writer.println((index + 1) + " " + entry.getKey() + " " + entry.getValue().toString());
            if (++index == 50) break;
        }
        writer.close();

        //Get the PageRank and sort them by descending count
        boolean first = true;
        R.put("Biography", 0.0f);
        float old = 0.0f;
        while(Math.abs(R.get("Biography") - old) > tau || first) {
            if (!first) old = R.get("Biography");
            else first = false;
            System.out.println("Ranking...");
            R.clear();
            for (int i = 0; i < pages.size(); i++) {
                R.put(pages.get(i), lambda / N);
            }
            for (String page : pages) {
                ArrayList<String> intemp = links.y.get(page);
                ArrayList<String> outtemp = links.z.get(page);
                ArrayList<String> Q = new ArrayList<>();
                if (intemp != null && outtemp != null) {
                    for (String link : outtemp) {
                        if (!intemp.contains(link)) intemp.add(link);
                    }
                    Q = intemp;
                }
                if (Q.size() > 0) {
                    for(String link : Q) {
                        float prob = R.get(link);
                        R.replace(link, prob, prob + (1 - lambda) * (I.get(link) / Q.size()));
                    }
                }
                else if (outtemp != null) {
                    for(String link : outtemp) {
                        float prob = R.get(link);
                        R.replace(link, prob, prob + (1 - lambda) * (I.get(link) / N));
                    }
                }
                I = R;
            }
        }
        PrintWriter writer2 = new PrintWriter("pagerank.txt", "UTF-8");
        Map<String, Float> PageRank = new HashMap<>();
        for (String page : pages) {
            PageRank.put(page, I.get(page));
        }
        PageRank = MapUtil.sortByValue(PageRank);
        index = 0;
        for (Map.Entry<String, Float> entry : PageRank.entrySet()) {
            writer2.println((index + 1) + " " + entry.getKey() + " " + entry.getValue().toString());
            if (++index == 50) break;
        }
        writer2.close();
        System.out.println(System.nanoTime() - start);
    }

    private PageRank() { }

    private Tuple<Map<String, Integer>, Map<String, Integer>, Map<String, ArrayList<String>>, Map<String, ArrayList<String>>> populate() throws IOException{
        FileReader fr = new FileReader("links.srt");
        BufferedReader br = new BufferedReader(fr);
        Map<String, Integer> inlinks = new HashMap<>();
        Map<String, Integer> outlinks = new HashMap<>();
        Map<String, ArrayList<String>> stringinlinks = new HashMap<>();
        Map<String, ArrayList<String>> stringoutlinks = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line, "\t", false);
            String src = st.nextToken();
            String dst = st.nextToken();
            if (inlinks.containsKey(dst)) {
                ArrayList<String> old = stringinlinks.get(dst);
                ArrayList<String> curr = old;
                curr.add(src);
                inlinks.replace(dst, curr.size() - 1, curr.size());
                stringinlinks.replace(dst, old, curr);
            }
            else {
                ArrayList<String> curr = new ArrayList<>();
                curr.add(src);
                inlinks.put(dst, 1);
                stringinlinks.put(dst, curr);
                pages.add(dst);
            }
            if (outlinks.containsKey(src)) {
                ArrayList<String> old = stringoutlinks.get(src);
                ArrayList<String> curr = old;
                curr.add(dst);
                outlinks.replace(src, curr.size() - 1, curr.size());
                stringoutlinks.replace(src, old, curr);
            }
            else {
                ArrayList<String> curr = new ArrayList<>();
                curr.add(dst);
                outlinks.put(src, 1);
                stringoutlinks.put(src, curr);
                pages.add(src);
            }
        }
        return new Tuple<>(inlinks, outlinks, stringinlinks, stringoutlinks);
    }
}
