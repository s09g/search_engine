from bs4 import BeautifulSoup
from csv import reader
import os

webpage_directory = "LATimesDownloadData/"
map_filename = 'mapLATimesDataFile.csv'
output_filename = "edgeList.txt"

def get_map_file():
	webpage_map = {}
	map_file = open(map_filename)
	map_reader = reader(map_file)
	for row in map_reader:
		webpage_map[row[1]] = row[0]
	map_file.close()
	return webpage_map


def extract_links(webpage, webpage_map):
	page = open(webpage)
	html = page.read()
	page.close()
	bsObj = BeautifulSoup(html, "html.parser")
	links = set()
	for link in bsObj.findAll('a'):
		if 'href' in link.attrs:
			outlink = link.attrs['href']
			if outlink.startswith('/'):
				outlink = "http://www.latimes.com" + outlink
			if outlink.startswith('http://www.latimes.com') and outlink in webpage_map:
				links.add(webpage_map.get(outlink))
	return links


def build_network_graph():
	webpage_map = get_map_file()
	output_file = open(output_filename, 'w')
	for root, dirs, files in os.walk(webpage_directory):
		for file in files:
			webpage = os.path.join(root, file)
			outlinks = extract_links(webpage, webpage_map)
			for link in outlinks:
				output_file.write(link + " " + file + "\n")
	output_file.close()

if __name__ == '__main__':
	build_network_graph()