#include "cycleDetection.h"
#include "stddef.h"
#include "stdlib.h"
#include "stdio.h"

// Runs Kahn's algorithm on the graph, and outputs 'CYCLE DETECTED!\n'
// if a DAG cannot be created, or the vertices as a list fx. '4, 0, 1, 3, 2\n'
// representing an ordering in the DAG.
// The output is printed to stdout.
// The input may be altered in the process.
void cycleDetection(Graph *g)
{
    Vertex *L = calloc(g->numVertices, sizeof(Vertex));
    Vertex *S = calloc(g->numVertices, sizeof(Vertex));
    int lcounter = g->numVertices; //total number of Vertex
    int scounter = 0;
    for (int i = 0; i < g->numVertices; i++) {
        if ((g->vertices + (i*sizeof(Vertex)))->inNeighbours == NULL) { //Check if Vertex has no in neighbours
            *(S + (scounter * sizeof(Vertex))) = *(g->vertices + (i * sizeof(Vertex))); // adds Vertex to S
            Vertex *temp = g->vertices + (i * sizeof(Vertex)); //temp pointer to the Vertex in vertices
            if (temp -> outNeighbours -> size > 0){   
                LinkedListNode *nextOne = temp -> outNeighbours->head; //nextone is out neighbours of vertex i we added to S
        
                int index = temp -> outNeighbours->size;
                for (int j = 0; j < index; j++){
                    int id =(int)* (int*)nextOne->data; //Gets the id of next 
                    temp = g->vertices + (id * sizeof(Vertex)); //temp points to the Vertex with the id
                    temp->inNeighbours->size--; //Vertex id in neighbours subtracted with 1 because we remove one of them
                    if (nextOne -> next != NULL){
                        nextOne = nextOne->next;
                    }
                }    
            }
            scounter++;
        }
    }
    int vertCounter = 0; //counter to check how many Vertex we added to L
   
    while (scounter > vertCounter) //while there are Vertex left in S we do
    {
        Vertex u = *(S + (vertCounter * sizeof(Vertex))); //Gets Vertex from S
        *(L + vertCounter * sizeof(Vertex)) = u; //Puts it in L
        vertCounter++;
        if (u.outNeighbours != NULL){ //If u has outneighbours we take a look at them
            LinkedListNode *outNext = u.outNeighbours->head; //pointer to the first of u outneighbours
        
            for (int i = 0; i < u.outNeighbours->size; i++) //Loop through all outneighbours
            {
                int id = (int)* (int*)outNext->data; //The outneighbour we are currently looking at
                if ((g->vertices + (id* sizeof(Vertex)))->inNeighbours->size == 0) //If that Vertex has 0 in neighbours
                {
                    *(S + scounter) = *(g->vertices + (id * sizeof(Vertex))); //Add it to S
                    scounter++;
                    LinkedListNode *outNew = (g->vertices + (id * sizeof(Vertex)))->outNeighbours->head; //New in S outneighbours
                    int index = (g->vertices + (id * sizeof(Vertex)))->outNeighbours->size;
                    for (int j = 0; j < index; j++)
                    {
                        int in =(int)* (int*)outNew->data; //Id of the Vertex of the neighbours we have reached
                        ((g->vertices + (in * sizeof(Vertex)))->inNeighbours->size)--; //Subtract one from that in neighbours
                        if (outNew -> next != NULL){ //If any left
                            outNew = outNew->next; //Go to next neighbour
                        }
                    }
                
                
                }
                if (outNext -> next != NULL){ //If any left
                        outNext = outNext->next; //Go to next neighbour
                }
            }
        }    
    }
    if (lcounter == vertCounter){ //If we have gotten all Vertex over in L
        for (int v = 0; v < lcounter; v++){
            int id = (L+(v * sizeof(Vertex))) -> id;
            printf("%d ,", id); //We print their ID in the order they were found
        }
        printf("\n");
    }
    else {
        printf("CYCLE DETECTED!");
    }
}
