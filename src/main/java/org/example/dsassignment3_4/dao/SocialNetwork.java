package org.example.dsassignment3_4.dao;

import java.util.*;

public class SocialNetwork {

    static Map<Integer, List<Integer>> graph = new HashMap<>(); // Adjacency list
    static Map<Integer, String> users = new HashMap<>(); // User ID to Name mapping
    static int userCount = 0;

    // Add a user to the graph
    static void addUser(String name) {
        users.put(userCount, name);
        graph.put(userCount, new ArrayList<>());
        userCount++;
        System.out.println("User " + name + " added with ID: " + (userCount - 1));
    }

    // Add a friendship between two users
    static void addFriend(int userId1, int userId2) {
        if (isValidUser(userId1) && isValidUser(userId2)) {
            graph.get(userId1).add(userId2);
            graph.get(userId2).add(userId1);
            System.out.println(users.get(userId1) + " and " + users.get(userId2) + " are now friends.");
        } else {
            System.out.println("Invalid User IDs!");
        }
    }

    // Remove a friendship
    static void removeFriend(int userId1, int userId2) {
        if (isValidUser(userId1) && isValidUser(userId2)) {
            graph.get(userId1).remove((Integer) userId2);
            graph.get(userId2).remove((Integer) userId1);
            System.out.println(users.get(userId1) + " and " + users.get(userId2) + " are no longer friends.");
        } else {
            System.out.println("Invalid User IDs!");
        }
    }

    // Display all users
    static void displayUsers() {
        System.out.println("All Users:");
        for (Map.Entry<Integer, String> entry : users.entrySet()) {
            System.out.println("ID: " + entry.getKey() + ", Name: " + entry.getValue());
        }
    }

    // Display mutual friends between two users
    static void displayMutualFriends(int userId1, int userId2) {
        if (isValidUser(userId1) && isValidUser(userId2)) {
            Set<Integer> mutualFriends = new HashSet<>(graph.get(userId1));
            mutualFriends.retainAll(graph.get(userId2));

            System.out.println("Mutual friends between " + users.get(userId1) + " and " + users.get(userId2) + ":");
            if (mutualFriends.isEmpty()) {
                System.out.println("No mutual friends found.");
            } else {
                for (int friendId : mutualFriends) {
                    System.out.println(users.get(friendId));
                }
            }
        } else {
            System.out.println("Invalid User IDs!");
        }
    }

    // Recommend friends using BFS
    static void recommendFriendsBFS(int userId) {
        if (!isValidUser(userId)) {
            System.out.println("Invalid User ID!");
            return;
        }

        Set<Integer> visited = new HashSet<>();
        Set<Integer> recommendations = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.add(userId);
        visited.add(userId);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int friend : graph.get(current)) {
                if (!visited.contains(friend)) {
                    visited.add(friend);
                    queue.add(friend);

                    for (int potentialFriend : graph.get(friend)) {
                        if (potentialFriend != userId && !graph.get(userId).contains(potentialFriend)) {
                            recommendations.add(potentialFriend);
                        }
                    }
                }
            }
        }

        System.out.println("Friend recommendations for " + users.get(userId) + ":");
        if (recommendations.isEmpty()) {
            System.out.println("No new recommendations.");
        } else {
            for (int id : recommendations) {
                System.out.println(users.get(id));
            }
        }
    }

    // Recommend friends using DFS
    static void recommendFriendsDFS(int userId) {
        if (!isValidUser(userId)) {
            System.out.println("Invalid User ID!");
            return;
        }

        Set<Integer> visited = new HashSet<>();
        Set<Integer> recommendations = new HashSet<>();
        dfs(userId, visited, recommendations, userId);

        System.out.println("Friend recommendations for " + users.get(userId) + ":");
        if (recommendations.isEmpty()) {
            System.out.println("No new recommendations.");
        } else {
            for (int id : recommendations) {
                System.out.println(users.get(id));
            }
        }
    }

    private static void dfs(int current, Set<Integer> visited, Set<Integer> recommendations, int userId) {
        visited.add(current);

        for (int friend : graph.get(current)) {
            if (!visited.contains(friend)) {
                for (int potentialFriend : graph.get(friend)) {
                    if (potentialFriend != userId && !graph.get(userId).contains(potentialFriend)) {
                        recommendations.add(potentialFriend);
                    }
                }
                dfs(friend, visited, recommendations, userId);
            }
        }
    }

    // Utility: Check if a user ID is valid
    static boolean isValidUser(int userId) {
        return users.containsKey(userId);
    }

    // Main menu
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int user1;
        int user2;
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add User");
            System.out.println("2. Add Friend");
            System.out.println("3. Remove Friend");
            System.out.println("4. Display Users");
            System.out.println("5. Display Mutual Friends");
            System.out.println("6. Recommend Friends (BFS)");
            System.out.println("7. Recommend Friends (DFS)");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter user name: ");
                    String name = scanner.next();
                    addUser(name);
                    break;
                case 2:
                    System.out.print("Enter first user ID: ");
                    user1 = scanner.nextInt();
                    System.out.print("Enter second user ID: ");
                    user2 = scanner.nextInt();
                    addFriend(user1, user2);
                    break;
                case 3:
                    System.out.print("Enter first user ID: ");
                    user1 = scanner.nextInt();
                    System.out.print("Enter second user ID: ");
                    user2 = scanner.nextInt();
                    removeFriend(user1, user2);
                    break;
                case 4:
                    displayUsers();
                    break;
                case 5:
                    System.out.print("Enter first user ID: ");
                    user1 = scanner.nextInt();
                    System.out.print("Enter second user ID: ");
                    user2 = scanner.nextInt();
                    displayMutualFriends(user1, user2);
                    break;
                case 6:
                    System.out.print("Enter user ID for recommendations: ");
                    int userIdBFS = scanner.nextInt();
                    recommendFriendsBFS(userIdBFS);
                    break;
                case 7:
                    System.out.print("Enter user ID for recommendations: ");
                    int userIdDFS = scanner.nextInt();
                    recommendFriendsDFS(userIdDFS);
                    break;
                case 8:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}

