## Page Rank
---
### demo

I collected around 20,000 web pages from LA Times based on my [web crawler](https://github.com/ZhangShiqiu1993/search_engine/tree/master/webcrawler). By extracting out links on each page, I built a network graph. I got 234702 edges in this graph, and also tried virtualized part of them.
---
Here is first 1000 edges in the network graph.
![1000 edges](https://github.com/ZhangShiqiu1993/search_engine/blob/master/pageRank/demo/figure_2.png?raw=true)
---
Here is first 5000 edges in the network graph.
![5000 edges](https://github.com/ZhangShiqiu1993/search_engine/blob/master/pageRank/demo/figure_1.png?raw=true)

---
* implemented one of the most important algorithm in search engine ranking  -- Page Rank algorithm
* built relation matrix based on the hyperlink in each page
* calculated the PageRank value based on relation matrix
* after PageRank convergence, ranked each page based on the PR value

---
+ 从Wiki 数据集中实现 Google 搜索引擎排名的重要算法Page Rank。
+ 利用邻接矩阵构建各个网页之间的关系，基于构建的关系矩阵计算各个网页的PageRank。
+ 利用PageRank收敛值实现网页排名。
