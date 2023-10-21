gcc -c -o cycleDetection.o cycleDetection.c
gcc -c -o Graph.o Graph.c
gcc -c -o LinkedList.o LinkedList.c
gcc main.c cycleDetection.o Graph.o LinkedList.o -o main
./main in
