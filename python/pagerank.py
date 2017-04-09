import networkx as nx

edgeList = "edgeList.txt"
page_rank_file = open("external_pageRankFile.txt", 'w')

G = nx.read_edgelist(edgeList, create_using=nx.DiGraph())
pr = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight', dangling=None)

for docid in pr:
	page_rank_score = pr[docid]
	page_rank_file.write(docid + "=" + str(page_rank_score) + "\n")
page_rank_file.close()
