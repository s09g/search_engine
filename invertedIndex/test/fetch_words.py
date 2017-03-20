#! /usr/bin/env python3
import os
words = ["magnifiers", "livelihood", "thrilling", "fuel", "warning", "mars", "sawyer", "guaranty"]

for word in words:
    cmd = "grep -e '^{0}\\t' output.txt >> index.txt".format(word)
    os.system(cmd)
