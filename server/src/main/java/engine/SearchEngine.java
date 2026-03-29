package engine;

import algorithms.BoyerMoore;
import model.Document;
import util.DocumentLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

    private final List<Document> documents;
    private final Indexer indexer;
    private final FrequencyAnalyzer frequencyAnalyzer;

    public SearchEngine() {
        this.documents         = DocumentLoader.loadDocuments();
        this.indexer           = new Indexer(documents);
        this.frequencyAnalyzer = new FrequencyAnalyzer(documents);
    }

    // ── Called by the HTTP server ─────────────────────────────
    /**
     * Run a search and return positions + stats.
     * @param text      the corpus to search in
     * @param pattern   the pattern to find
     * @param algorithm "bm" | "naive" | "kmp" | "rk"
     */
    public SearchResult searchWithStats(String text, String pattern, String algorithm) {
        SearchResult result = new SearchResult();

        switch (algorithm == null ? "bm" : algorithm.toLowerCase()) {
            case "bm":
            default:
                result = BoyerMoore.search(text, pattern);
                break;
            case "naive":
                result = naiveSearch(text, pattern);
                break;
            case "kmp":
                result = kmpSearch(text, pattern);
                break;
            case "rk":
                result = rabinKarpSearch(text, pattern);
                break;
        }

        return result;
    }

    // ── Original console methods (kept intact) ────────────────
    public void search(String query) {
        System.out.println("\nSearching for: \"" + query + "\"");
        System.out.println("──────────────────────────────");
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

    // ── Naive search ──────────────────────────────────────────
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

    // ── KMP search ────────────────────────────────────────────
    private SearchResult kmpSearch(String text, String pattern) {
        SearchResult r = new SearchResult();
        int n = text.length(), m = pattern.length();
        if (m == 0) return r;
        int[] lps = new int[m];
        for (int i=1,len=0; i<m;) {
            if (pattern.charAt(i) == pattern.charAt(len)) lps[i++] = ++len;
            else if (len > 0) len = lps[len-1]; else lps[i++] = 0;
        }
        int i=0, j=0;
        while (i < n) {
            r.comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) { i++; j++; }
            if (j == m) { r.positions.add(i-j); j = lps[j-1]; r.shifts++; }
            else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j > 0) { j = lps[j-1]; r.shifts++; } else i++;
            }
        }
        return r;
    }

    // ── Rabin-Karp search ─────────────────────────────────────
    private SearchResult rabinKarpSearch(String text, String pattern) {
        SearchResult r = new SearchResult();
        int n = text.length(), m = pattern.length();
        final long B = 31, MOD = 1_000_000_009L;
        long ph=0, th=0, pw=1;
        for (int i=0; i<m; i++) {
            ph = (ph*B + pattern.charAt(i)) % MOD;
            th = (th*B + text.charAt(i)) % MOD;
            if (i>0) pw = pw*B % MOD;
        }
        for (int i=0; i<=n-m; i++) {
            r.shifts++;
            if (ph == th) {
                boolean match = true;
                for (int j=0; j<m; j++) { r.comparisons++; if (text.charAt(i+j)!=pattern.charAt(j)){match=false;break;} }
                if (match) r.positions.add(i);
            }
            if (i < n-m) {
                th = ((th - text.charAt(i)*pw%MOD + MOD)*B + text.charAt(i+m)) % MOD;
            }
        }
        return r;
    }

    // ── Result model ──────────────────────────────────────────
    public static class SearchResult {
        public List<Integer> positions = new ArrayList<>();
        public int comparisons = 0;
        public int shifts      = 0;
    }
}