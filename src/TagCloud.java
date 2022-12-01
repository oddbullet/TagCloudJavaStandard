import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Generate HTML tag cloud. Using standard java libraries.
 *
 * @author Bill Yang
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

            //TODO make sure it works like equals
            return o1.getKey().compareToIgnoreCase(o2.getKey());
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

            //TODO make sure it works like equals
            return o2.getValue().compareTo(o1.getValue());
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
     * @param wordMap
     * @param wordSortVal
     */
    public static void loadMapAndQueue(BufferedReader inFile,
            Map<String, Integer> wordMap,
            PriorityQueue<Entry<String, Integer>> wordSortVal) {

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

        }
    }

    /**
     *
     * @param size
     *            the amount of words that the user want on the tag cloud.
     * @param wordKeyMachine
     *            sorted Map.Pair<String, Integer> by Key, alphabetically, in a
     *            Sorting Machine.
     * @param wordValMachine
     *            sorted Map.Pair<String, Integer> by Value, number, in a
     *            SortingMachine.
     *
     * @updates wordKeyMachine
     *
     * @updates wordValMachine
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

        Entry<String, Integer> pair = wordValueSort.poll();
        int mostCount = pair.getValue();
        int minCount = 0;

        wordKeySort.add(pair);
        //Generate wordValMachine
        for (int i = 1; i < size; i++) {
            pair = wordValueSort.poll();
            wordKeySort.add(pair);

            if (size - i == 1) {
                minCount = pair.getValue();
            }
        }

        for (Entry<String, Integer> pair2 : wordKeySort) {
            int fontSize = (((maxFont - minFont)
                    * (pair2.getValue() - minCount)) / (mostCount - minCount))
                    + minFont;

            keyFontSize.put(pair2.getKey(), fontSize);
        }

        return keyFontSize;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        BufferedReader inFile;
        PrintWriter outFile;

        String fileLocation;

        // Open File
        try {
            System.out.print("Input File Location: ");
            fileLocation = in.readLine();

            inFile = new BufferedReader(new FileReader(fileLocation));

        } catch (IOException e) {
            System.err.println("Error Opening File.");
            return;
        }

        //Read File, Open and Close Writer.
        try {
            System.out.print("Output File Location: ");

            outFile = new PrintWriter(
                    new BufferedWriter(new FileWriter(in.readLine())));

            System.out.print("Number of Words: ");
            String wordStrCount = in.readLine();

            //TODO issue here
            int wordCount = 0;
            if (!(wordStrCount == null)) {
                wordCount = Integer.parseInt(wordStrCount);
            } else {
                System.err.println("Invalid Number");
                in.close();
                inFile.close();
                outFile.close();
                return;
            }

            Map<String, Integer> wordMap = new HashMap<>();

            Comparator<Entry<String, Integer>> ck = new KeySort();
            Comparator<Entry<String, Integer>> cv = new ValueSort();

            PriorityQueue<Entry<String, Integer>> wordKeySort = new PriorityQueue<>(
                    ck);
            PriorityQueue<Entry<String, Integer>> wordValSort = new PriorityQueue<>(
                    cv);

            //add words to the map and the sorting machine
            loadMapAndQueue(inFile, wordMap, wordValSort);

            //generate font proportion to the count of the word
            Map<String, Integer> keyFontSize = createwordKeyMachine(wordCount,
                    wordKeySort, wordValSort);

            //output the head in the html page
            HTMLGen.createHead(outFile, wordCount, fileLocation);
            HTMLGen.createBody(outFile, wordCount, fileLocation, wordKeySort,
                    keyFontSize);

            //output the body in the html page

            outFile.close();
        } catch (IOException e) {
            System.err.println("Error Reading File.");
        }

        //Close File
        try {
            in.close();
            inFile.close();
        } catch (IOException e) {
            System.err.println("Error Close File.");
        }

    }

}
