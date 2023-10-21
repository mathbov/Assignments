#include "cycleDetection.h"
#include "stddef.h"
#include "stdio.h"
#include "stdlib.h"

// Runs Kahn's algorithm on the graph, and outputs 'CYCLE DETECTED!\n'
// if a DAG cannot be created, or the vertices as a list fx. '4, 0, 1, 3, 2\n'
// representing an ordering in the DAG.
// The output is printed to stdout.
// The input may be altered in the process.
void cycleDetection(Graph *g) {
  Vertex *L = calloc(g->numVertices, sizeof(Vertex));
  Vertex *S = calloc(g->numVertices, sizeof(Vertex));
  int lcounter = g->numVertices; // total number of Vertex
  int scounter = 0;
  for (int i = 0; i < g->numVertices; i++) {
    if ((g->vertices + (i))->inNeighbours == NULL) { // Check if Vertex has no in neighbours
      *(S + (scounter)) = *(g->vertices + (i)); // adds Vertex to S
      scounter++;
      Vertex *temp = g->vertices + (i); // temp pointer to the Vertex in vertices
      if(temp->outNeighbours->head != NULL){
        LinkedListNode *nextOne = temp->outNeighbours->head; // nextone is out neighbours of vertex i we added to S
        int index = temp->outNeighbours->size;
        for (int j = 0; j < index; j++) {
          int id = (int)*(int *)nextOne->data; // Gets the id of next
          temp = g->vertices + (id);  // temp points to the Vertex with the id
          temp->inNeighbours->size--; // Vertex id in neighbours subtracted with 1
                                    // because we remove one of them
          nextOne = nextOne->next;
        }
      }
    }
  }
  int vertCounter = 0; // counter to check how many Vertex we added to L

  while (scounter > vertCounter) // while there are Vertex left in S we do
  {
    Vertex u = *(S + (vertCounter)); // Gets Vertex from S
    *(L + vertCounter) = u;          // Puts it in L
    vertCounter++;
    if (u.outNeighbours !=NULL) { // If u has outneighbours we take a look at them
      LinkedListNode *outNext = u.outNeighbours->head; // pointer to the first of u outneighbours

      for (int i = 0; i < u.outNeighbours->size;i++)  { // Loop through all outneighbours
        int id =(int)*(int *)outNext->data; // The outneighbour we are currently looking at

        if ((g->vertices + (id))->inNeighbours->size ==0) { // If that Vertex has 0 in neighbours
          *(S + scounter) = *(g->vertices + (id)); // Add it to S
          scounter++;
          (g->vertices + (id))->inNeighbours->size--; //Sets size to -1 so it only gets discovered once

          if ((g->vertices + (id))-> outNeighbours != NULL){
            LinkedListNode *outNew = (g->vertices + (id)) -> outNeighbours->head; // New in S outneighbours
            int index = (g->vertices + (id))->outNeighbours->size;

            for (int j = 0; j < index; j++) {
              int in = (int)*(int *)outNew->data; // Id of the Vertex of the
                                                // neighbours we have reached
              ((g->vertices + (in))->inNeighbours->size)--; // Subtract one from
                                                          // that in neighbours
              if (outNew->next != NULL) {                   // If any left
                outNew = outNew->next; // Go to next neighbour
              }
            }
          }
        }
        if (outNext->next != NULL) { // If any left
          outNext = outNext->next;   // Go to next neighbour
        }
      }
    }
  }

  if (lcounter == vertCounter) { // If we have gotten all Vertex over in L
    for (int v = 0; v < lcounter; v++) {
      int id = (L + (v))->id;
      printf("%d,", id); // We print their ID in the order they were found
    }
    printf("\n");
  } else {
    printf("CYCLE DETECTED!");
  }
}
