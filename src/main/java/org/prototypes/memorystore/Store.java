package org.prototypes.memorystore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Store {

    private final HashMap<String, Object> store;

    public Store() {
//        Demonstration for using complex object class as value in the hashmap, Object class be directly used as value type in HashMap in Java as present in the active code.
//        store = new HashMap<String, ValueType>();
        store = new HashMap<String, Object>();
    }

    public void put(String key, Object value){
//        store.put(key, new ValueType(value.getClass(), value));
        store.put(key, value);
    }

    public Class<?> getType(String key){
        return store.get(key).getClass();
    }

    public Object get(String key){
        //        ValueType data = store.get(key);
        //        if (data == null) {return null;}
        //        return  data.getData();
        return store.get(key);
    }

    public void remove(String key) {
        store.remove(key);
        System.out.println(key + " Key Removed !");
    }
    public static void main (String[] args) {

        Store memstore = new Store();
        memstore.put("Test", "Success!");
        memstore.put("TestInt", 111);
        memstore.put("TestList", List.of(1, 2, 3));

        System.out.println(memstore.get("Test"));
        System.out.println(memstore.get("TestInt"));
        System.out.println(memstore.get("TestList"));

        memstore.remove("Test");
        memstore.remove("TestInt");
        memstore.remove("TestList");
        System.out.println(memstore.get("Test"));

    }
}
