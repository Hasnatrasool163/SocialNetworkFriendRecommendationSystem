// author : Muhammad Hasnat Rasool
// 17 / 11 / 24

package org.example.dsassignment3_4.view;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Project Requirements:
 * - Create a social network simulation using graph data structures.
 * - Implement user management with features like adding/removing friends and posts.
 * - Suggest friends based on mutual connections and levels of separation.
 * - Provide options to display mutual friends, user posts, and network connections.
 * - Include BFS traversal for level-wise exploration of the network.
 * - Ensure scalability, user interaction, and data validation.
 */

/**
 * Represents a user in the social network. Each user has a unique ID, name,
 * a set of friends, and a queue of recent posts.
 */
class User implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private int inDegree = 0; // incoming connections
    private int outDegree = 0; // outgoing connections
    private String name;
    private Set<User> friends;
    private Queue<String> posts;

    /**
     * Constructs a User with the specified user ID and name.
     *
     * @param userId the unique identifier for the user
     * @param name   the name of the user
     */
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.friends = new HashSet<>();
        this.posts = new LinkedList<>();
    }

    /**
     * @return the user's unique ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the set of friends associated with the user
     */
    public Set<User> getFriends() {
        return friends;
    }

    /**
     * @return the queue of the user's posts
     */
    public Queue<String> getPosts() {
        return posts;
    }

    /**
     * Adds a friend to the user's friend list and ensures bidirectional friendship.
     *
     * @param user the user to be added as a friend
     */
    public void addFriend(User user) {
        this.friends.add(user);
        user.getFriends().add(this);
    }

    /**
     * Removes a friend from the user's friend list and ensures bidirectional removal.
     *
     * @param user the user to be removed as a friend
     */
    public void removeFriend(User user) {
        this.friends.remove(user);
        user.getFriends().remove(this);
    }

    /**
     * Adds a post to the user's post queue. Ensures the post queue maintains
     * a maximum size of 10 posts, removing the oldest if necessary.
     *
     * @param post the content of the post
     */
    public void addPost(String post) {
        if (post == null || post.trim().isEmpty()) {
            System.out.println("Post content cannot be empty.");
            return;
        }
        if (posts.size() >= 10) { // Limit to the latest 10 posts
            posts.poll();
        }
        posts.offer(post);
    }

    /**
     * Method that provides in-degree connections for a user
     * @return in-degree connections
     */
    public int getInDegree() {
        return inDegree;
    }

    /**
     * Method that provides out-degree connections for a user
     * @return out-degree connections
     */
    public int getOutDegree() {
        return outDegree;
    }

    /**
     * Method that increment in-degree connections for a user
     */
    public void incrementInDegree() {
        inDegree++;
    }

    /**
     * Method that increment out-degree connections for a user
     */
    public void incrementOutDegree() {
        outDegree++;
    }

    /**
     * Method that decrement in-degree connections for a user
     */
    public void decrementInDegree() {
        inDegree = Math.max(0, inDegree - 1);
    }

    /**
     * Method that decrement out-degree connections for a user
     */
    public void decrementOutDegree() {
        outDegree = Math.max(0, outDegree - 1);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", friends=" + friends.size() +
                ", posts=" + posts.size() +
                '}';
    }
}

/**
 * Represents a graph-based social network where users are nodes and friendships are edges.
 * Provides methods to manage users, friendships, posts, and network traversal.
 */
class SocialNetworkGraph {
    private static final String DATA_FILE = "social_network_data.ser";
    private Map<String, User> users;

    /**
     * @return the map of users in the social network, keyed by user ID.
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Constructs a social network graph and loads persisted data if available.
     */
    public SocialNetworkGraph() {
        if (!loadData()) {
            this.users = new HashMap<>();
        }
    }

    /**
     * Saves the current state of the social network to a file.
     */
    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Loads the social network data from a file.
     *
     * @return true if data was successfully loaded, false otherwise.
     */
    private boolean loadData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (Map<String, User>) in.readObject();
            System.out.println("Data loaded successfully.");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
        return false;
    }


    /**
     * Adds a user to the social network.
     *
     * @param userId the unique ID of the user
     * @param name   the name of the user
     */
    public void addUser(String userId, String name) {
        if (users.containsKey(userId)) {
            System.out.println("User ID already exists.");
            return;
        }
        users.put(userId, new User(userId, name));
        saveData();
    }

    /**
     * Establishes a friendship between two users.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     */
    public void addFriend(String userId1, String userId2) {
        if (userId1.equals(userId2)) {
            System.out.println("A user cannot be friends with themselves.");
            return;
        }

        User user1 = users.get(userId1);
        User user2 = users.get(userId2);

        if (user1 == null || user2 == null) {
            System.out.println("One or both users not found.");
            return;
        }

        if (user1.getFriends().contains(user2)) {
            System.out.println("Friendship already exists between " + user1.getName() + " and " + user2.getName());
            return;
        }

        user1.addFriend(user2);
        user1.incrementOutDegree();
        user2.incrementInDegree();
        System.out.println("Friendship created between " + user1.getName() + " and " + user2.getName());
        saveData();
    }

    /**
     * Removes a friendship between two users.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     */
    public void removeFriend(String userId1, String userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);

        if (user1 == null || user2 == null) {
            System.out.println("One or both users not found.");
            return;
        }

        if (!user1.getFriends().contains(user2)) {
            System.out.println(user1.getName() + " and " + user2.getName() + " are not friends.");
            return;
        }

        user1.removeFriend(user2);
        user1.decrementOutDegree();
        user2.decrementInDegree();
        System.out.println("Friendship removed between " + user1.getName() + " and " + user2.getName());
        saveData();
    }

    /**
     * Retrieves the mutual friends between two users.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return a set of mutual friends
     */
    public Set<User> getMutualFriends(String userId1, String userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);

        if (user1 == null || user2 == null) {
            System.out.println("One or both users not found.");
            return Set.of();
        }

        Set<User> mutualFriends = new HashSet<>(user1.getFriends());
        mutualFriends.retainAll(user2.getFriends());

        if (mutualFriends.isEmpty()) {
            System.out.println("No mutual friends found.");
        }
        return mutualFriends;
    }

    /**
     * Suggests potential friends for a user based on mutual connections.
     *
     * @param userId the ID of the user
     * @return a set of suggested friends
     */
    public Set<User> suggestFriends(String userId) {
        User user = users.get(userId);

        if (user == null) {
            System.out.println("User not found.");
            return Set.of();
        }

        // Map to track potential friends and their scores
        Map<User, Integer> recommendationScores = new HashMap<>();

        // Traverse the direct friends of the user
        for (User friend : user.getFriends()) {
            // Traverse friends of friends
            for (User potentialFriend : friend.getFriends()) {
                // Skip the user itself and already connected friends
                if (!potentialFriend.equals(user) && !user.getFriends().contains(potentialFriend)) {
                    // Increment score based on mutual friends
                    recommendationScores.put(
                            potentialFriend,
                            recommendationScores.getOrDefault(potentialFriend, 0) + 1
                    );
                }
            }
        }

        // Rank recommendations by:
        // 1. Number of mutual friends (descending)
        // 2. Total connections of the potential friend (degree, descending)
        // 3. Lexicographical order of userId (ascending) for tie-breaking
        return recommendationScores.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    int mutualComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (mutualComparison != 0) {
                        return mutualComparison; // More mutual friends first
                    }

                    int degree1 = entry1.getKey().getFriends().size();
                    int degree2 = entry2.getKey().getFriends().size();
                    if (degree1 != degree2) {
                        return Integer.compare(degree2, degree1); // Higher degree first
                    }

                    return entry1.getKey().getUserId().compareTo(entry2.getKey().getUserId());
                })
                .map(Map.Entry::getKey)
                .limit(5) // Limit to top 5 recommendations
                .collect(Collectors.toSet());
    }


    /**
     * Displays all users in the social network.
     */
    public void displayAllUsers() {
        System.out.println("Users in the Social Network:");
        for (User user : users.values()) {
            System.out.println("- " + user.getName() + " (ID: " + user.getUserId() + ")");
        }
    }

    /**
     * Displays all posts of a specified user.
     *
     * @param userId the ID of the user
     */
    public void displayUserPosts(String userId) {
        User user = users.get(userId);
        if (user != null) {
            System.out.println("Posts by " + user.getName() + ":");
            for (String post : user.getPosts()) {
                System.out.println("- " + post);
            }
        } else {
            System.out.println("User not found.");
        }
    }

    /**
     * Adds a post to a user's post queue.
     *
     * @param userId the ID of the user
     * @param post   the content of the post
     */
    public void addUserPost(String userId, String post) {
        User user = users.get(userId);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        if (post == null || post.trim().isEmpty()) {
            System.out.println("Post content cannot be empty.");
            return;
        }

        user.addPost(post);
        System.out.println("Post added for " + user.getName());
        saveData();
    }

    /**
     * Displays all user connections in the social network.
     */
    public void displayNetwork() {
        System.out.println("Social Network Connections:");
        for (User user : users.values()) {
            System.out.print(user.getName() + " -> ");
            for (User friend : user.getFriends()) {
                System.out.print(friend.getName() + " ");
            }
            System.out.println();
        }
    }

    /**
     * Performs a BFS traversal of the network starting from a specified user.
     *
     * @param startUserId the ID of the starting user
     */
    public void bfsTraversal(String startUserId) {
        User startUser = users.get(startUserId);
        if (startUser == null) {
            System.out.println("User not found.");
            return;
        }

        Set<User> visited = new HashSet<>();
        Queue<User> queue = new LinkedList<>();
        queue.offer(startUser);
        visited.add(startUser);

        System.out.println("BFS Traversal from " + startUser.getName() + ":");
        while (!queue.isEmpty()) {
            User current = queue.poll();
            System.out.print(current.getName() + " -> ");
            for (User friend : current.getFriends()) {
                if (!visited.contains(friend)) {
                    queue.offer(friend);
                    visited.add(friend);
                }
            }
        }
        System.out.println();
    }
}

/**
 * Main application class for the Social Network.
 * Provides a menu-driven console interface for users to interact with a social network graph.
 * Features include adding users, managing friendships, suggesting friends, and displaying posts.
 */
public class SocialNetworkApp {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Entry point of the application. Displays the menu and handles user input.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SocialNetworkGraph network = new SocialNetworkGraph();

        while (true) {
            System.out.println("\nSocial Network Menu:");
            System.out.println("1. Add User");
            System.out.println("2. Add Friend");
            System.out.println("3. Remove Friend");
            System.out.println("4. Display Mutual Friends");
            System.out.println("5. Suggest Friends");
            System.out.println("6. Add User Post");
            System.out.println("7. Display User Posts");
            System.out.println("8. Display Network Connections");
            System.out.println("9. BFS Traversal");
            System.out.println("10. Display All Users");
            System.out.println("11. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addUser(network);
                case 2 -> addFriend(network);
                case 3 -> removeFriend(network);
                case 4 -> displayMutualFriends(network);
                case 5 -> suggestFriends(network);
                case 6 -> addUserPost(network);
                case 7 -> displayUserPosts(network);
                case 8 -> network.displayNetwork();
                case 9 -> bfsTraversal(network);
                case 10 -> network.displayAllUsers();
                case 11 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * Adds a new user to the social network.
     *
     * @param network the social network graph
     */
    private static void addUser(SocialNetworkGraph network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        if (network.getUsers().containsKey(userId)) {
            System.out.println("Duplicate ID not allowed!");
            return;
        }
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        network.addUser(userId, name);
        System.out.println("User added successfully.");
    }

    /**
     * Adds a friendship between two users in the social network.
     *
     * @param network the social network graph
     */
    private static void addFriend(SocialNetworkGraph network) {
        System.out.print("Enter user ID 1: ");
        String userId1 = scanner.nextLine();
        System.out.print("Enter user ID 2: ");
        String userId2 = scanner.nextLine();
        network.addFriend(userId1, userId2);
    }

    /**
     * Removes a friendship between two users in the social network.
     *
     * @param network the social network graph
     */
    private static void removeFriend(SocialNetworkGraph network) {
        System.out.print("Enter user ID 1: ");
        String userId1 = scanner.nextLine();
        System.out.print("Enter user ID 2: ");
        String userId2 = scanner.nextLine();
        network.removeFriend(userId1, userId2);
    }

    /**
     * Displays mutual friends between two users.
     *
     * @param network the social network graph
     */
    private static void displayMutualFriends(SocialNetworkGraph network) {
        System.out.print("Enter user ID 1: ");
        String userId1 = scanner.nextLine();
        System.out.print("Enter user ID 2: ");
        String userId2 = scanner.nextLine();
        Set<User> mutualFriends = network.getMutualFriends(userId1, userId2);
        System.out.println("Mutual Friends:");
        for (User user : mutualFriends) {
            System.out.println("- " + user.getName());
        }
    }

    /**
     * Suggests friends for a user based on mutual connections.
     *
     * @param network the social network graph
     */
    private static void suggestFriends(SocialNetworkGraph network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        Set<User> suggestions = network.suggestFriends(userId);
        System.out.println("Friend Suggestions:");
        for (User user : suggestions) {
            System.out.println("- " + user.getName());
        }
    }

    /**
     * Adds a post for a user in the social network.
     *
     * @param network the social network graph
     */
    private static void addUserPost(SocialNetworkGraph network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter post content: ");
        String post = scanner.nextLine();
        network.addUserPost(userId, post);
    }

    /**
     * Displays all posts of a specific user.
     *
     * @param network the social network graph
     */
    private static void displayUserPosts(SocialNetworkGraph network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        network.displayUserPosts(userId);
    }

    /**
     * Performs a BFS traversal starting from a specific user.
     *
     * @param network the social network graph
     */
    private static void bfsTraversal(SocialNetworkGraph network) {
        System.out.print("Enter start user ID: ");
        String userId = scanner.nextLine();
        network.bfsTraversal(userId);
    }
}
