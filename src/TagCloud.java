import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

/**
 * Generate HTML tag cloud. Using standard java libraries.
 *
 * @author Bill Yang
 * @author Pengyu Chen
 * @author Wei Luo
 *
 */
public final class TagCloud {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloud() {
    }

    /**
     * Compare Sting in Alphabetical Order. Where bar will come before Foo.
     */
    private static class KeySort implements Comparator<Entry<String, Integer>> {

        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {

            int ans = o1.getKey().compareToIgnoreCase(o2.getKey());

            if (ans == 0) {
                ans = o1.getValue().compareTo(o2.getValue());
            }
            return ans;
        }

    }

    /**
     * Compares Numbers in decreasing order.
     */
    private static class ValueSort
            implements Comparator<Entry<String, Integer>> {

        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {

            int ans = o2.getValue().compareTo(o1.getValue());

            if (ans == 0) {
                ans = o1.getKey().compareToIgnoreCase(o2.getKey());

            }
            return ans;

        }

    }

    /**
     * Definition of whitespace separators.
     */
    private static final String SEPARATORS = " \t\n\r,-.!?[]';:/()`*\"";

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code SEPARATORS}) or "separator string" (maximal length string of
     * characters in {@code SEPARATORS}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection entries(SEPARATORS) = {}
     * then
     *   entries(nextWordOrSeparator) intersection entries(SEPARATORS) = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection entries(SEPARATORS) /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of entries(SEPARATORS)  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of entries(SEPARATORS))
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position) {
        assert text != null : "Violation of: text is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        String ans = "";
        int end = position;
        boolean isSeps = SEPARATORS.contains(text.substring(position, end + 1));

        while (end < text.length()
                && isSeps != SEPARATORS.indexOf(text.charAt(end)) < 0) {
            end++;
        }

        ans = text.substring(position, end);

        return ans;
    }

    /**
     *
     * @param inFile
     *            the file being read from.
     * @param wordMap
     *            a map with the words, key, and the amount of times it appears,
     *            value.
     * @param wordSortVal
     *
     */
    public static void loadMapAndQueue(BufferedReader inFile,
            Map<String, Integer> wordMap,
            PriorityQueue<Entry<String, Integer>> wordSortVal) {

        //Read File
        try {
            String input;
            while ((input = inFile.readLine()) != null) {
                String word = "";
                int pos = 0;
                while (pos < input.length()) {
                    word = nextWordOrSeparator(input, pos).toLowerCase();
                    pos += word.length();
                    if (SEPARATORS.indexOf(word.charAt(0)) < 0) {
                        if (!wordMap.containsKey(word)) {
                            wordMap.put(word, 1);
                        } else {
                            boolean search = true;
                            Set<Entry<String, Integer>> pairMap = wordMap
                                    .entrySet();
                            Iterator<Entry<String, Integer>> child = pairMap
                                    .iterator();
                            while (child.hasNext() && search) {
                                Entry<String, Integer> pair = child.next();
                                if (pair.getKey().equals(word)) {
                                    pair.setValue(pair.getValue() + 1);
                                    search = false;
                                }
                            }
                        }
                    }

                }
            }

            Set<Entry<String, Integer>> pairMap = wordMap.entrySet();
            Iterator<Entry<String, Integer>> child = pairMap.iterator();
            while (child.hasNext()) {
                Entry<String, Integer> pair = child.next();
                wordSortVal.add(pair);
            }

        } catch (IOException e) {
            System.err.println("Error Reading File in LoadMapAndQueue.");
            return;

        }
    }

    /**
     *
     * @param size
     *            the amount of words that the user want on the tag cloud.
     * @param wordKeySort
     *            sorted Map.Pair<String, Integer> by Key, alphabetically, in a
     *            Sorting Machine.
     * @param wordValueSort
     *            sorted Map.Pair<String, Integer> by Value, number, in a
     *            SortingMachine.
     *
     * @updates wordKeySort
     *
     * @updates wordValSort
     *
     * @return return a Map with the appropriate font size for each key.
     *
     */
    public static Map<String, Integer> createwordKeyMachine(int size,
            PriorityQueue<Entry<String, Integer>> wordKeySort,
            PriorityQueue<Entry<String, Integer>> wordValueSort) {

        Map<String, Integer> keyFontSize = new HashMap<>();
        final int maxFont = 48;
        final int minFont = 11;
        final int defaultFont = 20;

        Entry<String, Integer> pair = wordValueSort.poll();
        int mostCount = 0;

        if (!(wordValueSort.size() == 0)) {
            mostCount = pair.getValue();
            wordKeySort.add(pair);
        }

        int minCount = 0;

        //Generate wordValSort
        for (int i = 1; i < size; i++) {
            pair = wordValueSort.poll();
            wordKeySort.add(pair);

            if (size - i == 1) {
                minCount = pair.getValue();
            }
        }

        for (Entry<String, Integer> pair2 : wordKeySort) {
            int fontSize = defaultFont;
            if (!(mostCount == minCount)) {
                fontSize = (((maxFont - minFont)
                        * (pair2.getValue() - minCount))
                        / (mostCount - minCount)) + minFont;
            }

            keyFontSize.put(pair2.getKey(), fontSize);
        }

        return keyFontSize;
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
     * @updates wordKeySort
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

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        BufferedReader inFile;
        PrintWriter outFile;

        String fileLocation;

        Map<String, Integer> wordMap = new HashMap<>();

        Comparator<Entry<String, Integer>> ck = new KeySort();
        Comparator<Entry<String, Integer>> cv = new ValueSort();

        PriorityQueue<Entry<String, Integer>> wordKeySort = new PriorityQueue<>(
                ck);
        PriorityQueue<Entry<String, Integer>> wordValSort = new PriorityQueue<>(
                cv);

        // Open File
        try {

            System.out.print("Input File Location: ");
            fileLocation = in.nextLine();

            inFile = new BufferedReader(new FileReader(fileLocation));

        } catch (IOException e) {
            System.err.println("Error Opening File.");
            in.close();
            return;
        }

        //add words to the map and the sorting machine
        loadMapAndQueue(inFile, wordMap, wordValSort);

        //Close In File.
        try {
            inFile.close();
        } catch (IOException e1) {
            System.err.println("Error Closing File");
            in.close();
            return;
        }

        try {
            System.out.print("Output File Location: ");
            outFile = new PrintWriter(
                    new BufferedWriter(new FileWriter(in.nextLine())));
        } catch (IOException e) {
            System.out.println("Error Opening Writer");
            in.close();
            return;
        }

        //Check if count is greater than the number of words.
        System.out.println("Enter a number from 0 to " + wordValSort.size());
        System.out.print("Number of Words: ");

        boolean state = in.hasNextInt();
        int wordCount = -1;
        if (state) {
            wordCount = in.nextInt();
        }

        while (wordCount < 0 || wordCount > wordValSort.size() || !state) {
            if (!state) {
                in.next();
                System.out.println("Not a number!");
                System.out.print("Number of Words: ");
                state = in.hasNextInt();

            } else {
                System.out.println("Outside of Range.");
                System.out.print("Number of Words: ");
                state = in.hasNextInt();

            }

            if (state) {
                wordCount = in.nextInt();
            }

        }

        //Close Scanner.
        in.close();

        //generate font proportion to the count of the word
        Map<String, Integer> keyFontSize = createwordKeyMachine(wordCount,
                wordKeySort, wordValSort);

        //output the head in the html page
        createHead(outFile, wordCount, fileLocation);

        //output the body in the html page
        createBody(outFile, wordCount, fileLocation, wordKeySort, keyFontSize);

        System.out.println("File Created");

        //Close Writer
        outFile.close();

    }

}
