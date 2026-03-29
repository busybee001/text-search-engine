package engine;

import algorithms.BoyerMoore;
import model.Document;
import util.DocumentLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchEngine {

    private final List<Document> documents;
    private final Indexer indexer;
    private final FrequencyAnalyzer frequencyAnalyzer;

    public SearchEngine() {
        this.documents         = DocumentLoader.loadDocuments();
        this.indexer           = new Indexer(documents);
        this.frequencyAnalyzer = new FrequencyAnalyzer(documents);
        System.out.println("[SearchEngine] Loaded " + documents.size() + " document(s).");
        System.out.println("[SearchEngine] Index built with " + indexer.getIndex().size() + " tokens.");
    }

    // ── Main search method called by HTTP server ──────────────
    public SearchResult searchWithStats(String text, String pattern, String algorithm) {
        switch (algorithm == null ? "bm" : algorithm.toLowerCase()) {
            case "naive": return naiveSearch(text, pattern);
            case "kmp":   return kmpSearch(text, pattern);
            case "rk":    return rabinKarpSearch(text, pattern);
            default:      return BoyerMoore.search(text, pattern);
        }
    }

    // ── Search across all loaded documents 
    public void search(String query) {
        System.out.println("\nSearching for: \"" + query + "\"");
        System.out.println("──────────────────────────────");

        // Use indexer for fast lookup first
        List<String> indexed = indexer.lookup(query);
        if (!indexed.isEmpty()) {
            System.out.println("[Index hit] Found in: " + indexed);
        }

        boolean found = false;
        for (Document doc : documents) {
            SearchResult result = BoyerMoore.search(doc.getContent(), query);
            if (!result.positions.isEmpty()) {
                System.out.println("Found in [" + doc.getName() + "] at positions: " + result.positions);
                found = true;
            }
        }
        if (!found) System.out.println("No matches found.");
    }

    // ── Search in manually provided text
    public void searchInManualText(String text, String query) {
        System.out.println("\nSearching for: \"" + query + "\"");
        System.out.println("──────────────────────────────");
        SearchResult result = BoyerMoore.search(text, query);
        if (result.positions.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            System.out.println("Found at positions: " + result.positions);
            System.out.println("Comparisons: " + result.comparisons + " | Shifts: " + result.shifts);
        }
    }

    // ── Get top words from frequency analyzer ────────────────
    public List<Map.Entry<String, Integer>> getTopWords(int n) {
        return frequencyAnalyzer.getTopWords(n);
    }

    // ── Naive search 
    private SearchResult naiveSearch(String text, String pattern) {
        SearchResult r = new SearchResult();
        int n = text.length(), m = pattern.length();
        for (int i = 0; i <= n - m; i++) {
            r.shifts++;
            int j = 0;
            while (j < m) { r.comparisons++; if (text.charAt(i+j) != pattern.charAt(j)) break; j++; }
            if (j == m) r.positions.add(i);
        }
        return r;
    }

    // ── KMP search
    private SearchResult kmpSearch(String text, String pattern) {
        SearchResult r = new SearchResult();
        int n = text.length(), m = pattern.length();
        if (m == 0) return r;
        int[] lps = new int[m];
        for (int i = 1, len = 0; i < m;) {
            if (pattern.charAt(i) == pattern.charAt(len)) lps[i++] = ++len;
            else if (len > 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        int i = 0, j = 0;
        while (i < n) {
            r.comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) { i++; j++; }
            if (j == m) { r.positions.add(i - j); j = lps[j - 1]; r.shifts++; }
            else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j > 0) { j = lps[j - 1]; r.shifts++; } else i++;
            }
        }
        return r;
    }

    // ── Rabin-Karp search 
    private SearchResult rabinKarpSearch(String text, String pattern) {
        SearchResult r = new SearchResult();
        int n = text.length(), m = pattern.length();
        final long B = 31, MOD = 1_000_000_009L;
        long ph = 0, th = 0, pw = 1;
        for (int i = 0; i < m; i++) {
            ph = (ph * B + pattern.charAt(i)) % MOD;
            th = (th * B + text.charAt(i)) % MOD;
            if (i > 0) pw = pw * B % MOD;
        }
        for (int i = 0; i <= n - m; i++) {
            r.shifts++;
            if (ph == th) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    r.comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) { match = false; break; }
                }
                if (match) r.positions.add(i);
            }
            if (i < n - m)
                th = ((th - text.charAt(i) * pw % MOD + MOD) * B + text.charAt(i + m)) % MOD;
        }
        return r;
    }

    // ── Result model 
    public static class SearchResult {
        public List<Integer> positions = new ArrayList<>();
        public int comparisons = 0;
        public int shifts      = 0;
    }
}