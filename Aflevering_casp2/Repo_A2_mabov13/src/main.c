#include "Graph.h"
#include "cycleDetection.h"

#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv) {
  if (argc < 2) {
    printf("Missing argument: input file\n");
    printf("Usage:\n");
    printf("%s <filename>\n", argv[0]);
    return 1;
  }

  Graph *g = Graph_read(argv[1]);
 // Graph_print(g);
  // Graph *g = Graph_read("../data/"graphmatrix-1.txt");
  if (!g)
    return 2;

  cycleDetection(g);
  //	Graph_delete(g);
  return 0;
}
