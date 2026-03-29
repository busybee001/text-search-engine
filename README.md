# 🔍 TEXT SEARCH ENGINE — Boyer-Moore Pattern Matching Engine

> A mini search engine built in Java with a sleek dark-themed browser UI, featuring multiple string-matching algorithms, keyword indexing, frequency analysis, and DNA pattern matching.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![HTML](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔎 **Pattern Search** | Find any word or phrase in a document |
| ⚡ **Boyer-Moore** | Bad-character heuristic for fast right-to-left scanning |
| 🧮 **Multiple Algorithms** | Compare Boyer-Moore, KMP, Naive, and Rabin-Karp side by side |
| 📊 **Frequency Analysis** | Word and character frequency bar charts |
| 🗂️ **Keyword Index** | Filterable inverted index of all tokens and positions |
| 🧬 **DNA Mode** | Pattern matching on nucleotide sequences (A T G C) |
| 🎞️ **Visualizer** | Step-by-step Boyer-Moore animation with shift explanations |
| 🌐 **Built-in Web Server** | Java HTTP server — no Spring Boot or extra libraries needed |

---

## 📁 Project Structure

```
text-search-engine/
├── client/
│   └── src/
│       ├── templates/
│       │   └── index.html          # Main UI page
│       ├── static/css/
│       │   ├── main.css            # Reset, variables, layout
│       │   ├── components.css      # Buttons, cards, forms
│       │   └── panels.css          # Per-tab styles
│       └── js/
│           ├── app.js              # Global state, tab switching
│           ├── algorithms.js       # BM, KMP, Naive, Rabin-Karp
│           ├── search.js           # API call + client fallback
│           ├── frequency.js        # Frequency analysis
│           ├── indexer.js          # Keyword index
│           ├── dna.js              # DNA pattern matching
│           ├── samples.js          # Sample data
│           └── visualizer.js       # Step-by-step BM visualizer
│
└── server/
    └── src/main/java/
        ├── Main.java               # HTTP server entry point
        ├── algorithms/
        │   └── BoyerMoore.java     # Boyer-Moore implementation
        ├── engine/
        │   ├── SearchEngine.java   # Runs all 4 algorithms
        │   ├── Indexer.java        # Inverted keyword index
        │   └── FrequencyAnalyzer.java
        ├── model/
        │   └── Document.java       # Document data model
        └── util/
            └── DocumentLoader.java # Loads .txt files from docs/
```

---

## 🚀 How to Run

### Prerequisites
- Java JDK 11 or higher
- VS Code (or any terminal)

### 1. Clone the repository
```bash
git clone https://github.com/mdkbk/text-search-engine.git
cd text-search-engine/server
```

### 2. Compile
```bash
mkdir -p out
javac -sourcepath src/main/java -d out $(find src/main/java -name "*.java")
```

### 3. Run
```bash
java -cp out Main
```

### 4. Open in browser
```
http://localhost:8080
```

You should see:
```
╔══════════════════════════════════════╗
║   BM Search Engine — Server Started  ║
║   Open: http://localhost:8080         ║
╚══════════════════════════════════════╝
```

---

## 🧠 Algorithms

### Boyer-Moore
Scans the pattern **right-to-left** and uses the **bad-character heuristic** to skip large portions of the text. One of the most efficient algorithms for string searching in practice.

```
Text:    A B C A A B A B C A B C A B C A B
Pattern:         A B C A B
                       ↑ mismatch → shift by bad-char table
```

### KMP (Knuth-Morris-Pratt)
Builds a **failure function (LPS array)** from the pattern to avoid re-checking characters after a mismatch.

### Naive (Brute Force)
Checks every position in the text one by one. O(nm) time — useful as a baseline for comparison.

### Rabin-Karp
Uses a **rolling hash** to fingerprint the pattern and compare hash values before checking characters. Good for multiple pattern search.

---

## 🧬 DNA Mode

The DNA tab uses Boyer-Moore on nucleotide sequences. Each base is colour-coded:

| Base | Name | Colour |
|---|---|---|
| **A** | Adenine | 🔴 Red |
| **T** | Thymine | 🟡 Yellow |
| **G** | Guanine | 🟢 Green |
| **C** | Cytosine | 🔵 Blue |

Matched patterns are highlighted in bright green across the full sequence viewer.

---

## 📂 Loading Your Own Documents

1. Create a `docs/` folder inside `server/`
2. Add any `.txt` files to it
3. Restart the server
4. Your documents are automatically indexed and searchable

---

## 🛠️ Tech Stack

- **Backend** — Pure Java, no frameworks. Uses `com.sun.net.httpserver` for the built-in HTTP server
- **Frontend** — Vanilla HTML, CSS, JavaScript. No React, no bundler
- **Fonts** — IBM Plex Mono + Syne (via Google Fonts)
- **Algorithms** — All implemented from scratch in both Java and JavaScript

---

## 📸 Preview

```
┌─────────────────────────────────────────────────────┐
│  • BM·SEARCH          [BOYER-MOORE] [INDEX] [DNA]   │
│─────────────────────────────────────────────────────│
│  Search  Frequency  Index  DNA Mode  Visualizer     │
│─────────────────────────────────────────────────────│
│  ┌──────────────────┐  ┌──────────────────────────┐ │
│  │ Document Corpus  │  │ Algorithm                │ │
│  │                  │  │ [Boyer-Moore] [Naive]    │ │
│  │ Paste text...    │  │ [KMP]        [Rabin-Karp]│ │
│  └──────────────────┘  └──────────────────────────┘ │
│  ┌─────────────────────────────────┐ [SEARCH]       │
│  │ ⌕ Enter pattern to search…     │                 │
│  └─────────────────────────────────┘                │
│  12 matches · 847 comparisons · 0.42ms              │
└─────────────────────────────────────────────────────┘
```

---

## 📄 License

This project is for educational purposes — feel free to use and modify it.

---

> Built with ☕ Java + vanilla web technologies