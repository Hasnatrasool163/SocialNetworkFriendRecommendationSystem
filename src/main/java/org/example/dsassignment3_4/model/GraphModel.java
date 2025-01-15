package org.example.dsassignment3_4.model;

import java.util.*;

public class GraphModel {

    private Map<Integer, List<Integer>> graph;

    public Map<Integer, List<Integer>> getGraph() {return graph;}

    public GraphModel() {
        this.graph = new HashMap<>();
    }

    public void addVertex(int user1){
        graph.put(user1,new ArrayList<>());
    }

    public void addEdge(int user1, int user2) {
        graph.computeIfAbsent(user1, k -> new ArrayList<>()).add(user2);
        graph.computeIfAbsent(user2, k -> new ArrayList<>()).add(user1);
    }

    public void removeEdge(int user1, int user2) {
        graph.getOrDefault(user1, new ArrayList<>()).remove(Integer.valueOf(user2));
        graph.getOrDefault(user2, new ArrayList<>()).remove(Integer.valueOf(user1));
    }

    public List<Integer> getFriends(int userId) {
        return graph.getOrDefault(userId, Collections.emptyList());
    }

    public boolean areFriends(int user1, int user2) {
        return graph.getOrDefault(user1, Collections.emptyList()).contains(user2);
    }


    public void printGraph() {
        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            System.out.println("User " + entry.getKey() + " -> " + entry.getValue());
        }
    }
}
