#include "Graph.h"
#include "stddef.h"
#include "stdlib.h"
#include "stdio.h"

// Allocates and constructs a new graph with n vertices.
// Returns a pointer to the new graph, or NULL on error.
// Post: the caller owns the graph.
Graph *Graph_new(int n)
{
    Graph *gp = malloc(sizeof(Graph));
    gp->numVertices = n;
    gp->numEdges = 0;
    Vertex *pw = malloc(n * (sizeof(Vertex)));
    gp->vertices = pw;
    for (int i = 0; i < n; i++)
    {
        pw->id = i;
        pw->outNeighbours = NULL;
        pw->inNeighbours = NULL;
        pw += sizeof(Vertex);
    }

    return gp;
}

void Graph_addEdgeIn(Graph *g, int i, int j)
{
    int *pi = malloc(sizeof(int));
    *pi = i;
    Vertex *a = g->vertices + (j * sizeof(Vertex));
    if (a->inNeighbours == NULL)
    {
        a->inNeighbours = LinkedList_new();
    }
     LinkedList_append(a->inNeighbours, pi);
    for (int v = 0; v < g->numVertices; v++){
        int id = (g->vertices+(v * sizeof(Vertex)))-> id;
        printf("%d \n" ,id);
    }

}
// Adds an edge from the i'th to the j'th vertex (0-indexed).
void Graph_addEdgeOut(Graph *g, int i, int j){
    int *pj = malloc(sizeof(int));
    *pj = j;
    Vertex *a = g->vertices + (i * sizeof(Vertex));
    if (a->outNeighbours == NULL)
    {
        a->outNeighbours = LinkedList_new();
    }
    LinkedList_append(a->outNeighbours, pj);
}

// Reads a graph from the given file and returns a newly
// constructed Graph representing it.
// Returns a pointer to the read graph, or NULL on error.
// Post: the caller owns the graph.
Graph *Graph_read(const char *filename)
{
    FILE *fp;
    char *line = NULL;
    size_t len = 0;
    ssize_t read;
    int nVertices = 0;

    fp = fopen(filename, "r");
    if (fp == NULL)
        exit(EXIT_FAILURE);

    if ((read = getline(&line, &len, fp)) != -1)
    {
        nVertices = atoi(line);
    }
    Graph *g = Graph_new(nVertices);
    for (int i = 0; i < nVertices; i++)
    {
        read = getline(&line, &len, fp);
        for (int j = 0; j < read; j++)
        {
            if (line[j] == '1')
            {
                Graph_addEdgeOut(g, i, j);
                Graph_addEdgeIn(g, i, j);
         
            }
        }
    }

    fclose(fp);

    return g;
}

// Deallocates the given graph and all its associated memory.
void Graph_delete(Graph *g)
{
    //    free(g);
}

// Prints some useful information about the given graph.
void Graph_print(Graph *g)
{
}
