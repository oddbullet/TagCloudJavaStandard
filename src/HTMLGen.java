import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * All method of generating HTML for Tag cloud can be found here.
 *
 * @author Bill Yang
 * @author Pengyu Chen
 * @author Wei Luo
 *
 */
public final class HTMLGen {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private HTMLGen() {
    }

    /**
     *
     * @param out
     *            SimpleWriter. For writing the output file.
     * @param count
     *            The amount of words to be display in the tag cloud.
     * @param fileLocation
     *            The location of the input file.
     *
     * @ensures the correct HTML head tag be written in the output file.
     *
     */
    public static void createHead(PrintWriter out, int count,
            String fileLocation) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Top " + count + " words in " + fileLocation
                + "</title>");

        ////One Link
        out.print(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/");
        out.print(
                "assignments/projects/tag-cloud-generator/data/tagcloud.css\"");
        out.println(" rel=\"stylesheet\" type=\"text/css\">");
        ////

        out.println(
                "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
    }

    /**
     *
     * @param out
     *            SimpleWriter. For writing the output file.
     * @param count
     *            The amount of words to be display in the tag cloud.
     * @param fileLocation
     *            The location of the input file.
     * @param wordKeySort
     *            Sorted alphabetically of Entry<String, Integer> by key.
     * @param keyFontSize
     *            A Map with the appropriate font size for each key.
     *
     * @updates wordKeyMachine
     *
     * @ensures The correct HTML body tag and it contents be written in the
     *          output file.
     */
    public static void createBody(PrintWriter out, int count,
            String fileLocation,
            PriorityQueue<Entry<String, Integer>> wordKeySort,
            Map<String, Integer> keyFontSize) {

        out.println("<body>");
        out.println("<h2>Top " + count + " words in " + fileLocation + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");

        for (int i = 0; i < count; i++) {
            Entry<String, Integer> wordPair = wordKeySort.poll();
            String title = " title=\"count: " + wordPair.getValue() + "\"";
            String cssClass = "class=\"" + "f"
                    + keyFontSize.get(wordPair.getKey()) + "\"";
            out.println("<span style=\"cursor:default\" " + cssClass + title
                    + ">" + wordPair.getKey() + "</span>");
        }

        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");

    }
}
