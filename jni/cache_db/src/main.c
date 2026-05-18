#include <stdio.h>
#include <string.h>
#include "ht.h"

int main() {
    ht* table = ht_create();

    // SET string
    char* name = "john";
    ht_set(table, "name", name, strlen(name) + 1, TYPE_STRING);

    // SET int
    int age = 25;
    ht_set(table, "age", &age, sizeof(int), TYPE_INT);

    // SET float
    float score = 3.14f;
    ht_set(table, "score", &score, sizeof(float), TYPE_FLOAT);

    // GET + print
    printf("name  -> "); ht_print_value(table, "name");
    printf("age   -> "); ht_print_value(table, "age");
    printf("score -> "); ht_print_value(table, "score");

    // DEL
    ht_del(table, "age");
    printf("age   -> "); ht_print_value(table, "age");

    ht_destroy(table);
    return 0;
}
