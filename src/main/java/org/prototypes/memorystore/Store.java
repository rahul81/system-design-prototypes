package org.prototypes.memorystore;

import java.util.HashMap;

public class Store {

    private final HashMap<String, ValueType> store;

    public Store() {
        store = new HashMap<String, ValueType>();
    }

    public void put(String key, Object value){
        store.put(key, new ValueType(value.getClass(), value));
    }

    public Class<?> getType(String key){
        return store.get(key).getType();
    }

    public Object get(String key){
        ValueType data = store.get(key);
        if (data == null) {return null;}
        return  data.getData();
    }

    public void remove(String key) {
        store.remove(key);
        System.out.println(key + " Key Removed !");
    }
    public static void main (String[] args) {

        Store memstore = new Store();
        memstore.put("Test", "Success!");
        memstore.put("TestInt", 111);

        System.out.println(memstore.get("Test"));
        System.out.println(memstore.get("TestInt"));

        memstore.remove("Test");
        memstore.remove("TestInt");
        System.out.println(memstore.get("Test"));

    }
}
