import java.util.*;
import java.io.*;

public class HuffmanCoding {

    // Node for Huffman Tree
    static class Node implements Comparable<Node> {
        String symbol; // null for internal nodes
        long freq; // frequency of symbol
        Node left, right; // left and right children

        // Constructor for leaf nodes
        Node(String symbol, long freq) {
            this.symbol = symbol; // Assigning symbol
            this.freq = freq; // Assigning frequency
        }
        
        // Constructor for internal nodes 
        Node(String symbol, long freq, Node left, Node right) {
            this.symbol = symbol; // Assigning symbol
            this.freq = freq; // Assigning combined frequency
            this.left = left; // Assigning left child
            this.right = right; // Assigning right child
        }

        // Method to compare nodes by frequency (using priority queue)
        public int compareTo(Node other) {
            return Long.compare(this.freq, other.freq);
        }

         // Method to check if this node is a leaf (has no children)
        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    // Building Huffman tree from a frequency map
    public static Node buildTree(Map<String, Long> freqMap) {
        
        // Creating a priority queue (min-heap) sorted by frequency
        PriorityQueue<Node> pq = new PriorityQueue<>();
        
        // Adding all symbols from frequency map into priority queue as leaf nodes
        for (Map.Entry<String, Long> e : freqMap.entrySet()) {
            pq.add(new Node(e.getKey(), e.getValue())); // Each entry becomes a leaf node
        }

        // If frequency map is empty, return null (no tree to build)
        if (pq.isEmpty())
            return null;
            // Combining two smallest-frequency nodes until one tree remains
        while (pq.size() > 1) {
            Node a = pq.poll(); // Removing smallest node
            Node b = pq.poll(); // Remove next smallest node
            Node parent = new Node(null, a.freq + b.freq, a, b); // Creating new internal node
            pq.add(parent); // Inserting the new combined node back into queue
        }
        return pq.poll(); // Returning the remaining node, the root of the Huffman tree
    }

    // Generating binary codes by traversing the tree
    public static void generateCodes(Node node, String prefix, Map<String, String> codes) {
        // if node is null, do nothing
        if (node == null)
            return;
            // If the node is a leaf, assign its accumulated binary code (prefix)
        if (node.isLeaf()) {
            if (prefix.length() == 0)
                prefix = "0"; // single-symbol edge case
            codes.put(node.symbol, prefix); // Store the code for this symbol
            return; // End recursion for this path
        }
        generateCodes(node.left, prefix + "0", codes); // Left branch adds '0' to prefix
        generateCodes(node.right, prefix + "1", codes); // Right branch adds '1' to prefix
    }

    // Top-level Huffman method (wrapper)
    public static Map<String, String> huffman(Map<String, Long> freqMap) {
        Node root = buildTree(freqMap); // Building Huffman tree
        Map<String, String> codes = new HashMap<>(); // Creating a map for symbol → code
        generateCodes(root, "", codes); // Filling the map recursively
        return codes; // Return completed code map
    }

    // Computing total weighted length: sum(freq * codeLength)
    public static long weightedLength(Map<String, Long> freqMap, Map<String, String> codes) {
        long total = 0L; // Initializing total length sum
        for (Map.Entry<String, Long> e : freqMap.entrySet()) {
            String sym = e.getKey(); // Symbol
            long f = e.getValue(); // Frequency
            String code = codes.get(sym); // Binary code for symbol
            if (code == null) { // Safety check: every symbol should have a code
                throw new RuntimeException("Missing code for symbol " + sym);
            }
            total += f * code.length(); // Add weighted length
        }
        return total; // Return final sum
    }

    // Verifying prefix-free property using a small trie 
    static class TrieNode {
        TrieNode zero, one; // Child nodes for bit '0' and '1'
        boolean isLeaf; // if this node represents end of a valid code - then true
    }

     // Verify prefix-free property using a trie
    public static boolean verifyPrefixFree(Map<String, String> codes) {
        TrieNode root = new TrieNode(); // Start from an empty root node
        // Insert each code into trie
        for (Map.Entry<String, String> e : codes.entrySet()) {
            String code = e.getValue();
            TrieNode cur = root;
            // Traverse bit-by-bit
            for (int i = 0; i < code.length(); i++) {
                if (cur.isLeaf) // If we reach a leaf before code ends, prefix conflict
                    return false; 
                char c = code.charAt(i); // Current bit
                if (c == '0') { // Move or create zero branch
                    if (cur.zero == null)
                        cur.zero = new TrieNode();
                    cur = cur.zero;
                } else { // Move or create one branch
                    if (cur.one == null)
                        cur.one = new TrieNode();
                    cur = cur.one;
                }
            }
            // If current node already has children or is leaf -> prefix conflict
            if (cur.isLeaf || cur.zero != null || cur.one != null)
                return false;
            cur.isLeaf = true; // Mark end of valid code
        }
        return true; // All codes verified as prefix-free
    }

    // Creating random frequency map with n unique symbols: "s0", "s1", ...
    public static Map<String, Long> createRandomFreqs(int n, int maxFreq, Random rng) {
        Map<String, Long> map = new LinkedHashMap<>(n); // Maintain insertion order
        for (int i = 0; i < n; i++) { 
            long f = 1 + rng.nextInt(maxFreq); // Random frequency between 1 and maxFreq
            map.put("s" + i, f);
        }
        return map;  // Return the generated frequency map
    }

    // Running experiments with different input sizes and write results to CSV
    public static void runExperiments(int[] sizes, int trials, String perTrialCsv, String summaryCsv)
            throws IOException {
        
        // Create writer for per-trial results CSV        
        PrintWriter per = new PrintWriter(new FileWriter(perTrialCsv));
        per.println("n,trial,timeMs,weightedLength,codesCount,prefixFree");
        
        // Map to store all run times for summary stats
        Map<Integer, List<Long>> timeMap = new LinkedHashMap<>();
        Random rng = new Random(42); // fixed seed for reproducibility
        
        // For each input size (n)
        for (int n : sizes) {
            timeMap.put(n, new ArrayList<>());
            // Repeat experiment 'trials' times
            for (int t = 1; t <= trials; t++) {
                Map<String, Long> freqs = createRandomFreqs(n, 1000, rng);
                
                long start = System.nanoTime(); // Record start time
                Map<String, String> codes = huffman(freqs); // Run Huffman algorithm
                long end = System.nanoTime(); // Record end time

                long timeMs = (end - start) / 1_000_000; // Converting nanoseconds to milliseconds
                long wlen = weightedLength(freqs, codes); // Computing weighted length
                boolean pf = verifyPrefixFree(codes); // Verify prefix-free property

                // Write trial data to CSV file
                per.printf("%d,%d,%d,%d,%d,%b%n", n, t, timeMs, wlen, codes.size(), pf);
                per.flush(); // Ensuring immediate write

                // Storing time for summary statistics
                timeMap.get(n).add(timeMs);
            }
        }
        per.close(); // Closing per-trial CSV writer

        // Write summary: avg/min/max/stddev
        PrintWriter sum = new PrintWriter(new FileWriter(summaryCsv));
        sum.println("n,avgMs,minMs,maxMs,stddevMs,trials");
        
        // For each input size, compute statistics 
        for (int n : sizes) {
            List<Long> list = timeMap.get(n); // Retrieving recorded times
            double avg = mean(list); // Computing average time
            long min = Collections.min(list); // Minimum time
            long max = Collections.max(list); // Maximum time
            double std = stddev(list, avg); // Standard deviation
            sum.printf("%d,%.3f,%d,%d,%.3f,%d%n", n, avg, min, max, std, list.size()); // Write one summary line per size
        }
        sum.close();
    }

    // computing standard deviation
    private static double mean(List<Long> list) {
        if (list.isEmpty())
            return 0.0; // Avoid division by zero
        double s = 0.0;
        for (long v : list)
            s += v;
        return s / list.size(); // return average
    }

    private static double stddev(List<Long> list, double mean) {
        if (list.isEmpty())
            return 0.0; // avoid invalid math
        double s = 0.0;
        for (long v : list) {
            double d = v - mean; // Difference from mean
            s += d * d; // sqauring and accumulating
        }
        return Math.sqrt(s / list.size()); // returning population standard deviation
    }

    // Demo + run experiments
    public static void main(String[] args) throws Exception {
        // A small demo using standard Huffman example
        Map<String, Long> sample = new LinkedHashMap<>();
        sample.put("A", 45L);
        sample.put("B", 13L);
        sample.put("C", 12L);
        sample.put("D", 16L);
        sample.put("E", 9L);
        sample.put("F", 5L);
        
        System.out.println("=== Demo: example ==="); // Print header
        Map<String, String> sampleCodes = huffman(sample);  // Generate Huffman codes for sample

        // Print each symbol and its Huffman code       
        for (Map.Entry<String, String> e : sampleCodes.entrySet()) {
            System.out.printf("%s : %s%n", e.getKey(), e.getValue());
        }

        // Print total weighted length
        System.out.println("Weighted length = " + weightedLength(sample, sampleCodes));

        // Check if prefix-free property holds
        System.out.println("Prefix-free? " + verifyPrefixFree(sampleCodes));
        System.out.println(); // Blank line

        // Running experiments for large random symbol sets
        int[] sizes = { 10000, 50000, 100000, 300000, 500000, 1000000 }; // Input sizes
        int trials = 5; // Number of trials per input size

        String perTrialCsv = "huffman_per_trial.csv"; // File for detailed trial data
        String summaryCsv = "huffman_summary.csv"; // File for summary stats

         // Announce start of experiments
        System.out.println("Running experiments... results -> " + perTrialCsv + " and " + summaryCsv);

        // Run experiments and write data
        runExperiments(sizes, trials, perTrialCsv, summaryCsv);
        
        // Inform user that processing is complete
        System.out.println("Done. Check the CSV files and plot the results (see instructions).");
    }
}

