#ifndef HT_H
#define HT_H

#include <stdbool.h>
#include <stddef.h>

#define TYPE_STRING 0
#define TYPE_INT    1
#define TYPE_FLOAT  2

typedef struct {
    char* key;
    void* value;
    size_t value_size;
    int dtype;
} ht_entry;

typedef struct {
    ht_entry* entries;
    size_t capacity;
    size_t length;
} ht;

ht* ht_create(void);
void ht_destroy(ht* table);
bool ht_set(ht* table, const char* key, void* value, size_t value_size, int dtype);
void* ht_get(ht* table, const char* key);
int ht_get_type(ht* table, const char* key);
bool ht_del(ht* table, const char* key);
size_t ht_length(ht* table);
void ht_print_value(ht* table, const char* key);

#endif
