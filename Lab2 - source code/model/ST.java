package model;

import java.util.Arrays;


public class ST {
    private String[] hashTable;
    private int capacity;

    public ST(int capacity) {
        this.capacity = capacity;
        this.hashTable = new String[capacity];
    }

    private int hashFunction(String identifier) {
        int sum = 0;
        //compute sum of char values of identifier
        for(int i = 0; i < identifier.length(); i++) {
            sum += identifier.charAt(i);
        }
        //then divide by size of hash table and ret remainder
        return sum % this.capacity;
    }


    public boolean insert(String identifier) {
        for (String s : hashTable) {
            if (s != null && s.equals(identifier)) {
                System.out.println("Already in sym table.");
                return false;
            }
        }
        int hashValue = hashFunction(identifier);
        if (hashTable[hashValue] == null) {
            hashTable[hashValue] = identifier;
            System.out.println("Insert " + identifier + " at position " + hashValue);
            return true;
        }
        // otherwise, we have a collision
        int x = 0;
        int nextAvailablePosition = (hashValue + x) % this.capacity;
        while (hashTable[nextAvailablePosition] != null) {
            x++;
            nextAvailablePosition = (hashValue + x) % this.capacity;
        }
        if (hashTable[nextAvailablePosition] == null) {
            hashTable[nextAvailablePosition] = identifier;
            System.out.println("Insert " + identifier + " at position " + nextAvailablePosition);
            return true;
        }
        System.out.println("Insert failed.");
        return false;
    }


    public int find(String identifier) {
        int x = 0;
        int hashValue = hashFunction(identifier);
        int hashValueCheck = hashValue;
        while (hashTable[hashValueCheck] != null) {
            if(hashTable[hashValueCheck].equals(identifier)) {
                return hashValueCheck;
            }
            hashValueCheck = (hashValue + x) % this.capacity;
            x++;
        }
        return -1;

    }


}
