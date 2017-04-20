# <img src="https://imgsa.baidu.com/baike/w%3D268/sign=1e68cddb36adcbef0134790094ae2e0e/8d5494eef01f3a29e33f21d19e25bc315c607c3e.jpg" width="32" height="32" /> HUAJI Search

---
### Search Engine Module

- [x] Web Crawler
- [x] Links Extraction
- [x] Page Rank
- [x] TF-IDF
- [x] N-Gram
- [x] Top K
- [ ] Distributed Indexer
- [x] Inverted Index
- [x] [Recommender System](https://github.com/ZhangShiqiu1993/recommender_system)
- [x] Sentiment Analysis
- [x] Front page
- [x] Spelling Correction
- [x] Language Identifier
- [x] Auto Completion
- [ ] Snippet


---
* A well-functional search engine, including Web Crawler, Spelling Correction, Inverted index, PageRank Algorithm, TF-IDF Algorithm, AutoComplete, Recommender System and Sentiment Analysis
* Implemented a single machine web crawler based on crawler4j
* Extracted out-links from webpages collected by web crawler and computed PageRank scores
* Parsed HTML pages, extracted content text and computed TF-IDF
* Implemented N-Gram and generated language model, built real-time AutoCompletion based on N-Gram statistics
* Implemented Inverted Index
* Implemented top K algorithm
* Implemented Recommendation System based on Item Based Collaborative Filtering Algorithm
* Implemented Sentiment Analysis based on Emotion Feature Dictionary
* Implemented Spelling Correction
* Built front pages with PHP, Bootstrap and jQuery

---
### demo

When I processing a query:
![query](https://github.com/ZhangShiqiu1993/search_engine/blob/master/demo/query.png?raw=true)

Highlight the keyword
![highlight](https://github.com/ZhangShiqiu1993/search_engine/blob/master/demo/query_highlight.png?raw=true)

AutoCompletion: give user query suggestions
![AutoCompletion](https://github.com/ZhangShiqiu1993/search_engine/blob/master/demo/autocomplete.png?raw=true)

Spelling Correction: When I mis-typing *California* as **californa**, it will aks "Are you looking for California"
![spellcorrection](https://github.com/ZhangShiqiu1993/search_engine/blob/master/demo/spellcorrection.png?raw=true)

We can click on the spelling correction hint. It will help us redirect to the correct word.
![redirect](https://github.com/ZhangShiqiu1993/search_engine/blob/master/demo/redirect.png?raw=true)
