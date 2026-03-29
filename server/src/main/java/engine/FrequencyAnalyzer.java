package engine;

import model.Document;

import java.util.*;

public class FrequencyAnalyzer {

    private final Map<String, Integer> wordFreq = new HashMap<>();
    private final Map<Character, Integer> charFreq = new HashMap<>();

    public FrequencyAnalyzer(List<Document> documents) {
        for (Document doc : documents) {
            String content = doc.getContent().toLowerCase();

            // Word frequency
            String[] words = content.split("\\W+");
            for (String word : words) {
                if (word.length() < 2) continue;
                wordFreq.merge(word, 1, Integer::sum);
            }

            // Character frequency
            for (char ch : content.toCharArray()) {
                if (Character.isLetter(ch)) {
                    charFreq.merge(ch, 1, Integer::sum);
                }
            }
        }
    }

    public Map<String, Integer> getWordFrequency()      { return Collections.unmodifiableMap(wordFreq); }
    public Map<Character, Integer> getCharFrequency()   { return Collections.unmodifiableMap(charFreq); }

    public List<Map.Entry<String, Integer>> getTopWords(int n) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFreq.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list.subList(0, Math.min(n, list.size()));
    }
}