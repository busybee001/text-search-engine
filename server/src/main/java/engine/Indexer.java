package engine;

import model.Document;

import java.util.*;

public class Indexer {

    // word -> list of document names it appears in
    private final Map<String, List<String>> index = new HashMap<>();

    public Indexer(List<Document> documents) {
        for (Document doc : documents) {
            String[] words = doc.getContent().toLowerCase().split("\\W+");
            for (String word : words) {
                if (word.length() < 2) continue;
                index.computeIfAbsent(word, k -> new ArrayList<>()).add(doc.getName());
            }
        }
    }

    public List<String> lookup(String word) {
        return index.getOrDefault(word.toLowerCase(), Collections.emptyList());
    }

    public Map<String, List<String>> getIndex() {
        return Collections.unmodifiableMap(index);
    }
}