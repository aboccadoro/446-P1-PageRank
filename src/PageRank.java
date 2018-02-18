import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class PageRank {

    private final double lamda = 0.2;
    private final double tau = 0.02;
    private static Map<String, Integer> inlinks = new HashMap<>();

    public static void main(String[] args) throws IOException {
        PageRank pr = new PageRank();
        pr.getInLinks();
        PrintWriter writer = new PrintWriter("inlinks.txt", "UTF-8");
        Map<String, Integer> recurring = MapUtil.sortByValue(inlinks);
        int index = 0;
        for (Map.Entry<String, Integer> entry : recurring.entrySet()) {
            writer.println(entry.getKey() + " " + entry.getValue().toString());
            if (index++ >= 50) break;
        }
        writer.close();
    }

    public PageRank() { }

    public void getInLinks() throws IOException{
        FileReader fr = new FileReader("links.srt");
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line, "\t", false);
            st.nextToken();
            String dst = st.nextToken();
            if (inlinks.containsKey(dst)) {
                int old = inlinks.get(dst);
                inlinks.replace(dst, old, old + 1);
            }
            else inlinks.put(dst, 1);
        }
    }
}
