hdfs dfs -rm -r /output
hadoop com.sun.tools.javac.Main *.java
jar cf ngram.jar *.class
hadoop jar ngram.jar Driver input /output 2 3 4
