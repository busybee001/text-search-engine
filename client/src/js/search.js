/* 
   Main search logic
   Calls the backend API first; falls back to
   client-side algorithms.js if the API is
   unavailable (useful for offline dev).
 */

   'use strict';

   // ── API endpoint (set to Java backend) ──
   const API_BASE = '/api';
   
   // ── Main entry point 
   async function runSearch() {
     const corpus       = document.getElementById('corpus').value;
     const pattern      = document.getElementById('mainSearch').value.trim();
     const caseSensitive = document.getElementById('caseSensitive').checked;
     const wholeWord     = document.getElementById('wholeWord').checked;
   
     if (!corpus || !pattern) {
       alert('Please provide both a corpus and a pattern.');
       return;
     }
   
     const spinner = document.getElementById('spinner');
     spinner.classList.add('active');
   
     // Allow repaint before heavy work
     await new Promise(r => setTimeout(r, 30));
   
     const t0 = performance.now();
     let result;
   
     try {
       // ── Try backend first ──
       result = await searchViaAPI({ corpus, pattern, caseSensitive, wholeWord });
     } catch (err) {
       console.warn('API unavailable — falling back to client-side search.', err);
       // ── Client-side fallback ──
       result = searchClientSide({ corpus, pattern, caseSensitive, wholeWord });
     }
   
     const elapsed = (performance.now() - t0).toFixed(2);
     spinner.classList.remove('active');
   
     renderStats(result, elapsed);
     renderResults(corpus, pattern, result.positions);
   
     // Keep index in sync
     buildKeywordIndex(corpus);
   }
   
   // ── Backend call 
   async function searchViaAPI({ corpus, pattern, caseSensitive, wholeWord }) {
     const res = await fetch(`${API_BASE}/search`, {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify({
         text:          corpus,
         pattern:       pattern,
         algorithm:     App.currentAlgo,
         caseSensitive: caseSensitive,
         wholeWord:     wholeWord,
       }),
     });
   
     if (!res.ok) throw new Error(`HTTP ${res.status}`);
     return res.json(); // expects { positions, comparisons, shifts }
   }
   
   // ── Client-side fallback
   function searchClientSide({ corpus, pattern, caseSensitive, wholeWord }) {
     let text = caseSensitive ? corpus  : corpus.toLowerCase();
     let pat  = caseSensitive ? pattern : pattern.toLowerCase();
   
     let { positions, comparisons, shifts } = runAlgo(text, pat);
   
     if (wholeWord) {
       positions = positions.filter(pos => {
         const before = pos === 0 || /\W/.test(text[pos - 1]);
         const after  = (pos + pat.length >= text.length) || /\W/.test(text[pos + pat.length]);
         return before && after;
       });
     }
   
     return { positions, comparisons, shifts };
   }
   
   // ── Render stats chips 
   function renderStats({ positions, comparisons, shifts }, elapsed) {
     document.getElementById('statsRow').style.display = 'flex';
   
     animateNum('statMatches',     positions.length);
     animateNum('statComparisons', comparisons);
     animateNum('statShifts',      shifts);
   
     document.getElementById('statTime').textContent = elapsed + 'ms';
     document.getElementById('statAlgo').textContent =
       { bm: 'BM', naive: 'Naive', kmp: 'KMP', rk: 'RK' }[App.currentAlgo] || 'BM';
   }
   
   // ── Render result cards 
   function renderResults(corpus, pattern, positions) {
     const resultsEl = document.getElementById('results');
     const CONTEXT   = 80;
   
     if (positions.length === 0) {
       resultsEl.innerHTML = `
         <div class="empty-state">
           <span class="big" style="font-size:2rem">∅</span>
           No matches found for <mark>${escHtml(pattern)}</mark>
         </div>`;
       return;
     }
   
     resultsEl.innerHTML = positions.map((pos, i) => {
       const start  = Math.max(0, pos - CONTEXT);
       const end    = Math.min(corpus.length, pos + pattern.length + CONTEXT);
       const before = escHtml(corpus.slice(start, pos));
       const match  = escHtml(corpus.slice(pos, pos + pattern.length));
       const after  = escHtml(corpus.slice(pos + pattern.length, end));
   
       return `
         <div class="result-item">
           <div class="result-meta">
             <span class="result-pos">pos:${pos}</span>
             <span>match ${i + 1} of ${positions.length}</span>
             <span>end:${pos + pattern.length - 1}</span>
           </div>
           <div class="result-snippet">
             ${start > 0 ? '…' : ''}${before}<mark>${match}</mark>${after}${end < corpus.length ? '…' : ''}
           </div>
         </div>`;
     }).join('');
   }