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

def status_code():
    print("\nStatus")
    print("200 OK")
    system("cat " + fetch_csv + " | grep 200 | wc -l")
    print("301")
    system("cat " + fetch_csv + " | grep 301 | wc -l")
    print("401")
    system("cat " + fetch_csv + " | grep 401 | wc -l")
    print("403")
    system("cat " + fetch_csv + " | grep 403 | wc -l")
    print("404")
    system("cat " + fetch_csv + " | grep 404 | wc -l")


def fetch_statistics(file):
    num_of_fetches_attempted = len(file)
    num_of_fetches_succeeded = 0
    num_of_fetches_3xx = 0
    num_of_fetches_4xx = 0
    num_of_fetches_5xx = 0
    for row in file:
        if row['http status code'][0] is '2':
            num_of_fetches_succeeded += 1
        if row['http status code'][0] is '3':
            num_of_fetches_3xx += 1
        if row['http status code'][0] is '4':
            num_of_fetches_4xx += 1
        if row['http status code'][0] is '5':
            num_of_fetches_5xx += 1

    print("\nFetch")
    print("num_of_fetches_attempted", num_of_fetches_attempted)
    print("num_of_fetches_succeeded", num_of_fetches_succeeded)
    print("num_of_fetches_3xx", num_of_fetches_3xx)
    print("num_of_fetches_4xx", num_of_fetches_4xx)
    print("num_of_fetches_5xx", num_of_fetches_5xx)


def visit_statistics(file):
    total_urls_extracted = 0
    KB_0_to_1 = 0
    KB_1_to_10 = 0
    KB_10_to_100 = 0
    KB_100_to_1000 = 0
    more_than_1MB = 0

    text_html = 0
    image_gif = 0
    image_jpeg = 0
    image_png = 0
    application_pdf = 0

    for row in file:
        total_urls_extracted += int(row["# of outlinks"])
        size = int(row["size"])
        content_type = row["content type"]

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


        if "text/html" in content_type:
            text_html += 1
        elif "image/gif" in content_type:
            image_gif += 1
        elif "image/jpeg" in content_type:
            image_jpeg += 1
        elif "application/pdf" in content_type:
            application_pdf += 1
        else:
            print(content_type)

    print("\nOutgoing URL")
    print("total extracted", total_urls_extracted)


    print("\nFile size:")
    print("< 1KB", KB_0_to_1)
    print("1KB ~ <10KB", KB_1_to_10)
    print("10KB ~ <100KB", KB_10_to_100)
    print("100KB ~ <1MB", KB_100_to_1000)
    print(">= 1MB", more_than_1MB)

    print("\nContent Type:")
    print("text/html", text_html)
    print("gif", image_gif)
    print("jpeg", image_jpeg)
    print("png", image_png)
    print("pdf", application_pdf)



def url_statistics(file):
    url_set = set()
    num_of_within = 0
    num_of_outside = 0
    for row in file:
        # pprint(row)
        url = row["url"]
        indicator = row["indicator"]
        if url not in url_set:
            url_set.add(url)
            if indicator == "OK":
                num_of_within += 1
            else:
                num_of_outside += 1
    num_of_unique = len(url_set)

    print("\nOutgoing URL")
    print("unique", num_of_unique)
    print("num with", num_of_within)
    print("num outside", num_of_outside)


fetch = parse_file(fetch_csv)
urls = parse_file(urls_csv)
visit = parse_file(visit_csv)

fetch_statistics(fetch)
url_statistics(urls)
visit_statistics(visit)
status_code()



