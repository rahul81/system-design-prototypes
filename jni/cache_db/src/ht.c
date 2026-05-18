#include "ht.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

#define INITIAL_CAPACITY 16
#define FNV_OFFSET 14695981039346656037UL
#define FNV_PRIME 1099511628211UL

static uint64_t hash_key(const char* key) {
    uint64_t hash = FNV_OFFSET;
    for (const char* p = key; *p; p++) {
        hash ^= (uint64_t)(unsigned char)(*p);
        hash *= FNV_PRIME;
    }
    return hash;
}

ht* ht_create(void) {
    ht* table = malloc(sizeof(ht));
    if (!table) return NULL;
    table->length = 0;
    table->capacity = INITIAL_CAPACITY;
    table->entries = calloc(table->capacity, sizeof(ht_entry));
    if (!table->entries) { free(table); return NULL; }
    return table;
}

void ht_destroy(ht* table) {
    for (size_t i = 0; i < table->capacity; i++) {
        if (table->entries[i].key) {
            free(table->entries[i].key);
            free(table->entries[i].value);
        }
    }
    free(table->entries);
    free(table);
}

static bool ht_expand(ht* table) {
    size_t new_cap = table->capacity * 2;
    ht_entry* new_entries = calloc(new_cap, sizeof(ht_entry));
    if (!new_entries) return false;

    for (size_t i = 0; i < table->capacity; i++) {
        ht_entry e = table->entries[i];
        if (e.key) {
            size_t idx = (size_t)(hash_key(e.key) & (uint64_t)(new_cap - 1));
            while (new_entries[idx].key)
                idx = (idx + 1) % new_cap;
            new_entries[idx] = e;
        }
    }
    free(table->entries);
    table->entries = new_entries;
    table->capacity = new_cap;
    return true;
}

bool ht_set(ht* table, const char* key, void* value, size_t value_size, int dtype) {
    if (table->length >= table->capacity / 2) {
        if (!ht_expand(table)) return false;
    }

    size_t idx = (size_t)(hash_key(key) & (uint64_t)(table->capacity - 1));

    while (table->entries[idx].key) {
        if (strcmp(key, table->entries[idx].key) == 0) {
            free(table->entries[idx].value);
            table->entries[idx].value = malloc(value_size);
            memcpy(table->entries[idx].value, value, value_size);
            table->entries[idx].value_size = value_size;
            table->entries[idx].dtype = dtype;
            return true;
        }
        idx = (idx + 1) % table->capacity;
    }

    table->entries[idx].key = strdup(key);
    table->entries[idx].value = malloc(value_size);
    memcpy(table->entries[idx].value, value, value_size);
    table->entries[idx].value_size = value_size;
    table->entries[idx].dtype = dtype;
    table->length++;
    return true;
}

void* ht_get(ht* table, const char* key) {
    size_t idx = (size_t)(hash_key(key) & (uint64_t)(table->capacity - 1));
    while (table->entries[idx].key) {
        if (strcmp(key, table->entries[idx].key) == 0)
            return table->entries[idx].value;
        idx = (idx + 1) % table->capacity;
    }
    return NULL;
}

int ht_get_type(ht* table, const char* key) {
    size_t idx = (size_t)(hash_key(key) & (uint64_t)(table->capacity - 1));
    while (table->entries[idx].key) {
        if (strcmp(key, table->entries[idx].key) == 0)
            return table->entries[idx].dtype;
        idx = (idx + 1) % table->capacity;
    }
    return -1;
}

bool ht_del(ht* table, const char* key) {
    size_t idx = (size_t)(hash_key(key) & (uint64_t)(table->capacity - 1));
    while (table->entries[idx].key) {
        if (strcmp(key, table->entries[idx].key) == 0) {
            free(table->entries[idx].key);
            free(table->entries[idx].value);
            table->entries[idx].key = NULL;
            table->entries[idx].value = NULL;
            table->length--;
            return true;
        }
        idx = (idx + 1) % table->capacity;
    }
    return false;
}

size_t ht_length(ht* table) {
    return table->length;
}

void ht_print_value(ht* table, const char* key) {
    void* val = ht_get(table, key);
    if (!val) { printf("(nil)\n"); return; }

    int dtype = ht_get_type(table, key);
    if (dtype == TYPE_STRING)
        printf("\"%s\"\n", (char*)val);
    else if (dtype == TYPE_INT)
        printf("%d\n", *(int*)val);
    else if (dtype == TYPE_FLOAT)
        printf("%f\n", *(float*)val);
}
