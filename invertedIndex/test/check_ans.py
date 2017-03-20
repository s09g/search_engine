#! /usr/bin/env python3
import sys
import os


def sort_ans(file):
    ans_file = open(file)
    file_name = ans_file.name
    content = ans_file.read()
    ans_file.close()
    words_list = content.splitlines()
    file = open("sort_{0}".format(file_name), 'w')
    for word_list in words_list:
        word_list = word_list.split("\t")
        word = word_list[0]
        word_list.remove(word)
        word_list.sort()
        file.write(word+"\n")
        for w in word_list:
            if len(w) == 0:
                continue
            file.write(w + "\n")
    file.close()
    return file.name


def check_ans(file1, file2):
    f1 = sort_ans(file1)
    f2 = sort_ans(file2)
    os.system("diff -b {0} {1}".format(f1, f2))

check_ans(sys.argv[1], sys.argv[2])

# sort_ans(sys.argv[1])