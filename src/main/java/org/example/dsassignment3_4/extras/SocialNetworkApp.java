package org.example.dsassignment3_4.extras;

import java.util.*;

class User {
    private String userId;
    private String name;
    private Set<User> friends;
    private Queue<String> posts;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.friends = new HashSet<>();
        this.posts = new LinkedList<>();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public Set<User> getFriends() { return friends; }
    public Queue<String> getPosts() { return posts; }

    public void addFriend(User user) {
        this.friends.add(user);
        user.getFriends().add(this);  // Bidirectional friendship
    }

    public void removeFriend(User user) {
        this.friends.remove(user);
        user.getFriends().remove(this);
    }

    public void addPost(String post) {
        if (posts.size() >= 10) {  // limit to the latest 10 posts
            posts.poll();
        }
        posts.offer(post);
    }
}

class SocialNetwork {
    private Map<String, User> users;

    public SocialNetwork() {
        this.users = new HashMap<>();
    }

    public void addUser(String userId, String name) {
        users.putIfAbsent(userId, new User(userId, name));
    }

    public void addFriend(String userId1, String userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);
        if (user1 != null && user2 != null && !user1.equals(user2)) {
            user1.addFriend(user2);
            System.out.println("Friendship created between " + user1.getName() + " and " + user2.getName());
        } else {
            System.out.println("One or both users not found.");
        }
    }

    public void removeFriend(String userId1, String userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);
        if (user1 != null && user2 != null) {
            user1.removeFriend(user2);
            System.out.println("Friendship removed between " + user1.getName() + " and " + user2.getName());
        } else {
            System.out.println("One or both users not found.");
        }
    }

    public Set<User> getMutualFriends(String userId1, String userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);
        if (user1 == null || user2 == null) return Set.of();

        Set<User> mutualFriends = new HashSet<>(user1.getFriends());
        mutualFriends.retainAll(user2.getFriends());
        return mutualFriends;
    }

    public Set<User> suggestFriends(String userId) {
        User user = users.get(userId);
        if (user == null) return Set.of();

        Set<User> suggestions = new HashSet<>();
        for (User friend : user.getFriends()) {
            for (User friendOfFriend : friend.getFriends()) {
                if (!user.getFriends().contains(friendOfFriend) && !friendOfFriend.equals(user)) {
                    suggestions.add(friendOfFriend);
                }
            }
        }
        return suggestions;
    }

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

    public void addUserPost(String userId, String post) {
        User user = users.get(userId);
        if (user != null) {
            user.addPost(post);
            System.out.println("Post added for " + user.getName());
        } else {
            System.out.println("User not found.");
        }
    }

    public void displayAllUsers() {
        System.out.println("Users in the Social Network:");
        for (User user : users.values()) {
            System.out.println("- " + user.getName() + " (ID: " + user.getUserId() + ")");
        }
    }
}

public class SocialNetworkApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SocialNetwork network = new SocialNetwork();

        while (true) {
            System.out.println("\nSocial Network Menu:");
            System.out.println("1. Add User");
            System.out.println("2. Add Friend");
            System.out.println("3. Remove Friend");
            System.out.println("4. Display Mutual Friends");
            System.out.println("5. Suggest Friends");
            System.out.println("6. Add User Post");
            System.out.println("7. Display User Posts");
            System.out.println("8. Display All Users");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // consume newline

            switch (choice) {
                case 1 -> addUser(network);
                case 2 -> addFriend(network);
                case 3 -> removeFriend(network);
                case 4 -> displayMutualFriends(network);
                case 5 -> suggestFriends(network);
                case 6 -> addUserPost(network);
                case 7 -> displayUserPosts(network);
                case 8 -> network.displayAllUsers();
                case 9 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addUser(SocialNetwork network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        network.addUser(userId, name);
        System.out.println("User added successfully.");
    }

    private static void addFriend(SocialNetwork network) {
        System.out.print("Enter user ID 1: ");
        String userId1 = scanner.nextLine();
        System.out.print("Enter user ID 2: ");
        String userId2 = scanner.nextLine();
        network.addFriend(userId1, userId2);
    }

    private static void removeFriend(SocialNetwork network) {
        System.out.print("Enter user ID 1: ");
        String userId1 = scanner.nextLine();
        System.out.print("Enter user ID 2: ");
        String userId2 = scanner.nextLine();
        network.removeFriend(userId1, userId2);
    }

    private static void displayMutualFriends(SocialNetwork network) {
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

    private static void suggestFriends(SocialNetwork network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        Set<User> suggestions = network.suggestFriends(userId);
        System.out.println("Friend Suggestions:");
        for (User user : suggestions) {
            System.out.println("- " + user.getName());
        }
    }

    private static void addUserPost(SocialNetwork network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter post content: ");
        String post = scanner.nextLine();
        network.addUserPost(userId, post);
    }

    private static void displayUserPosts(SocialNetwork network) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        network.displayUserPosts(userId);
    }
}
