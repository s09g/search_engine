## approximate top-k algorithm
+ **Tradeoff**: sacrifice accuracy for space
+ Flexible space
+ O(log K) time complexity
+ When a new word comes in, update its count in hashmap
+ update topK in treemap
+ Bloom filter

Disadvantages:
+ All low frequency words will be hashed to same value, which will result in incorrect result
+ Some low frequency words will come later, which will have a great count, then replace other high frequency words.

Bloom Filter:
+ HashMap will have 3 different hash functions
+ Choose the lowest count from hashmap

---
* 根据文档信息去统计当前文档里面所有出现的单词出现在TOP50的热门词语。
* 如果文档会不断更新，实现实时统计的方法，实时统计Top50的热门话题。
