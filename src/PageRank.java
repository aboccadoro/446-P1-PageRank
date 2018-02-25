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
        final float N = pages.size();
        for (int i = 0; i < pages.size(); i++) {
            I.put(pages.get(i), 1.0f / pages.size());
        }

        //Get the inlinks and sort them by descending count
        //Map<String, Integer> inlinks = MapUtil.sortByValue(links.w);
        //PrintWriter writer = new PrintWriter("inlinks.txt", "UTF-8");
        //int index = 0;
        //for (Map.Entry<String, Integer> entry : inlinks.entrySet()) {
        //    writer.println((index + 1) + " " + entry.getKey() + " " + entry.getValue().toString());
        //    if (++index == 50) break;
        //}
        //writer.close();

        //Get the PageRank and sort them by descending count
        float convergence = tau + .01f;
        Map<String, Float> lastR;
        float count = 0.0f;
        while(convergence > tau) {
            System.out.println("----------Ranking----------");
            lastR = R;
            R.clear();
            for (String page : pages) {
                R.put(page, lambda / N);
            }
            for (String page : pages) {
                ArrayList<String> Q = links.z.get(page);
                if (Q != null) {
                    for (String q : Q) {
                        float rank = R.get(page);
                        R.replace(q, rank, rank + (1.0f - lambda) * I.get(page) / (float)Q.size());
                    }
                }
                else count += (1.0f - lambda) * I.get(page) / N;
                I = R;
            }
            boolean first = true;
            for (String page : pages) {
                float min = Math.abs(lastR.get(page) - I.get(page));
                if (first) {
                    convergence = min;
                    first = false;
                }
                if (convergence > min) convergence = min;
                if (convergence < tau) break;
            }
            System.out.println(count);
            System.out.println(convergence);
        }

        PrintWriter writer2 = new PrintWriter("pagerank.txt", "UTF-8");
        I = MapUtil.sortByValue(I);
        int index = 0;
        for (Map.Entry<String, Float> entry : I.entrySet()) {
            writer2.println((index + 1) + " " + entry.getKey() + " " + entry.getValue().toString());
            if (++index == 50) break;
        }
        writer2.close();
        System.out.println('\n' + System.nanoTime() - start);
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
