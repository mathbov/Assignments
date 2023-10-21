#include "LinkedList.h"
#include "Graph.h"
#include "stddef.h"
#include "stdlib.h"
#include "stdio.h"


// Allocate and initialize an empty linked list.
// Returns: a pointer to the new linked list, or NULL on error.
// Post: the caller owns the linked list.
LinkedList *LinkedList_new(){
    LinkedList *ll = malloc(sizeof(LinkedList));
    ll -> head = NULL;
    ll -> tail = NULL;
    ll -> size = 0;
    return ll;
}

// Deallocate the given linked list, including all nodes and their data.
void LinkedList_delete(LinkedList *ll){
    
     LinkedListNode *temp = ll-> head;
    for (int i = 0;i < ll-> size; i++){
        ll-> head = ll-> head -> next;
 //       free(temp -> data);
//        free(temp);
        temp = ll-> head;
        
    }
//    free(ll);
}

// Append a the given element to the list.
// The linked list does _not_ take ownership over the element
// (only the linked list node).
// Returns: a pointer to the node with the new element, or NULL on error.
LinkedListNode *LinkedList_append( LinkedList *list,  void *elem){ // void *elem
    LinkedListNode* newNode = malloc(sizeof(LinkedListNode));
    list -> size += 1;
    newNode -> next = NULL;
    newNode -> prev = list -> tail;
    newNode -> data = elem;
   // int id =(int)* (int*)newNode->data;

    if (list -> head == NULL){
        list -> head = newNode;
    }
    list -> tail = newNode;  
    if (newNode -> prev != NULL ){
        newNode -> prev -> next = newNode;
    }



}

// Remove and return the first element from the given list.
// Pre: ll->size != 0
void *LinkedList_popFront(LinkedList *ll){

    LinkedListNode *firstEle = ll -> head;
    if ( ll -> head -> next != NULL){
    ll -> head -> next -> prev = NULL;
    ll -> head = ll -> head -> next;
    ll -> size -=1;
    }
    else {
        ll -> head = NULL;
        ll -> tail = NULL;
        ll -> size -= 1;
    }
    return firstEle;

}

// Find the linked list node containing the given element.
// Returns: a pointer to the found node, or NULL if the element was not found.
LinkedListNode *LinkedList_find(LinkedList *ll, void *elem){
    if (ll -> head != NULL){
        LinkedListNode *temp = ll->head;
        for (int i = 0; i < ll -> size; i++ ){
            if (temp -> data == elem){
                return temp;
            }
            temp = temp -> next;
        }

    }
    return NULL;
}

// Remove the given node from the given linked list (and deallocate it).
// Pre: node must belong to ll
// Returns: node->data
void *LinkedList_remove(LinkedList *ll, LinkedListNode *node){
    if(node -> prev != NULL){
        node -> prev -> next = node -> next;
    }
    if(node -> prev == NULL){
        ll -> head = node -> next;
    }
    if (node -> next != NULL){
        node -> next -> prev = node -> prev;
    }
    if (node -> next == NULL){
        ll -> tail = node -> prev;
    }
    ll -> size -= 1;
    //free(node);
}


