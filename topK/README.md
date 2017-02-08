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
