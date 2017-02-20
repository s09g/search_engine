import csv
from pprint import pprint
from os import system

fetch_csv = "./fetch_LATimes.csv"
urls_csv = "./urls_LATimes.csv"
visit_csv = "./visit_LATimes.csv"

def parse_file(datafile):
    data = []
    with open(datafile, encoding="utf8") as file:
        csv_reader = csv.DictReader(file)
        for line in csv_reader:
            data.append(line)
    return data


def fetch_statistics(file):
    num_of_fetches_attempted = len(file)
    fetch_dict = {}
    for row in file:
        status_code = int(row['http status code'])
        if status_code in fetch_dict:
            curt = fetch_dict[status_code]
            fetch_dict[status_code] = curt + 1
        else:
            fetch_dict[status_code] = 1

    print("\nFetch")
    pprint(fetch_dict)
    print(num_of_fetches_attempted)


def visit_statistics(file):
    total_urls_extracted = 0
    KB_0_to_1 = 0
    KB_1_to_10 = 0
    KB_10_to_100 = 0
    KB_100_to_1000 = 0
    more_than_1MB = 0

    content_type_dict = {}

    for row in file:
        total_urls_extracted += int(row["# of outlinks"])
        size = int(row["size"])
        content_type = row["content type"]

        if content_type in content_type_dict:
            curt = content_type_dict[content_type]
            content_type_dict[content_type] = curt + 1
        else:
            content_type_dict[content_type] = 1

        tmp = size / 1024
        if tmp < 1:
            KB_0_to_1 += 1
        elif tmp < 10:
            KB_1_to_10 += 1
        elif tmp < 100:
            KB_10_to_100 += 1
        elif tmp < 1024:
            KB_100_to_1000 += 1
        else:
            more_than_1MB += 1

    print("\nOutgoing URL")
    print("total extracted", total_urls_extracted)


    print("\nFile size:")
    print("< 1KB", KB_0_to_1)
    print("1KB ~ <10KB", KB_1_to_10)
    print("10KB ~ <100KB", KB_10_to_100)
    print("100KB ~ <1MB", KB_100_to_1000)
    print(">= 1MB", more_than_1MB)

    print("\nContent Type:")
    pprint(content_type_dict)



def url_statistics(file):
    url_set = set()
    num_of_within = 0
    num_of_outside = 0
    other = 0
    for row in file:
        url = row["url"]
        indicator = row["indicator"]
        if url not in url_set:
            url_set.add(url)
            if indicator == "OK":
                num_of_within += 1
            elif indicator == "N_OK":
                num_of_outside += 1
            else:
                other += 1
    num_of_unique = len(url_set)

    print("\nOutgoing URL")
    print("unique", num_of_unique)
    print("num with", num_of_within)
    print("num outside", num_of_outside)
    print("other", other)


fetch = parse_file(fetch_csv)
urls = parse_file(urls_csv)
visit = parse_file(visit_csv)

fetch_statistics(fetch)
url_statistics(urls)
visit_statistics(visit)
