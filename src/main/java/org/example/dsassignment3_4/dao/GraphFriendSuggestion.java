package org.example.dsassignment3_4.dao;

import org.example.dsassignment3_4.model.ScoreReport;
import org.example.dsassignment3_4.model.SuggestedFriend;
import org.example.dsassignment3_4.service.FriendsService;

import java.sql.*;
import java.util.*;

public class GraphFriendSuggestion {

    private  Map<Integer, List<Integer>> graph; // user - friends
    Map<Integer, List<Integer>> postLikesGraph; // user - liked-posts

    public GraphFriendSuggestion() {
        graph = new HashMap<>();
        postLikesGraph = new HashMap<>();
        loadGraph();
        loadLikesGraph();
        System.out.println(graph.toString());
        System.out.println(graph.size()+"is the size of graph");
    }

    public Map<Integer, List<Integer>> getGraph() {
        loadGraph();
        return graph;
    }


    public void loadGraph() {
        String query = "SELECT user1_id, user2_id FROM friendships";
        String query2 = "SELECT user1_id, user2_id FROM friendships where status = ?";

        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
//             stmt.setString(1,"ACCEPTED");
             ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int user1 = rs.getInt("user1_id");
                int user2 = rs.getInt("user2_id");

                // if (!graph.containsKey(user1)) {
                //    graph.put(user1, new ArrayList<>());
                //}
                //graph.get(user1).add(user2);

                graph.computeIfAbsent(user1, k -> new ArrayList<>()).add(user2); // bi-directional
                graph.computeIfAbsent(user2, k -> new ArrayList<>()).add(user1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void loadLikesGraph(){
        String queryPostLikes = "SELECT post_id, user_id FROM post_likes";
        try {Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryPostLikes);
            while (rs.next()) {
                int postId = rs.getInt("post_id");
                int userId = rs.getInt("user_id");

                postLikesGraph.computeIfAbsent(userId, k -> new ArrayList<>()).add(postId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getFriendsOfFriends(int userId) {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> directFriends = new HashSet<>(graph.getOrDefault(userId, Collections.emptyList()));
        Set<Integer> friendsOfFriends = new HashSet<>();

        Queue<Integer> queue = new LinkedList<>();
        queue.add(userId);
        visited.add(userId);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);

                    // Only add non-direct friends to the suggestion list
                    if (!directFriends.contains(neighbor) && neighbor != userId) {
                        friendsOfFriends.add(neighbor);
                    }
                }
            }
        }

        return friendsOfFriends.isEmpty() ? Collections.emptyList() : new ArrayList<>(friendsOfFriends);
    }



    public  List<SuggestedFriend> getSuggestedFriends(int userId) {
        List<SuggestedFriend> suggestions = new ArrayList<>();
        List<Integer> friendsOfFriends = getFriendsOfFriends(userId);
        System.out.println(friendsOfFriends);
        ScoreReport report = null;

        String query = """
            SELECT
                u.id AS suggested_friend_id,
                u.username,
                cp.hobbies,
                cp.location,
                cp.education,
                cp.extras,
                cp.age
            FROM users u
            JOIN completeProfile cp ON u.id = cp.user_id
            WHERE u.id = ?
        """;

        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);

            for (int friendId : friendsOfFriends) {
                stmt.setInt(1, friendId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int suggestedId = rs.getInt("suggested_friend_id");
                    String username = rs.getString("username");
                    String hobbies = rs.getString("hobbies");
                    String location = rs.getString("location");
                    String education = rs.getString("education");
                    String extras = rs.getString("extras");
                    int age = rs.getInt("age");

                   report = calculateScore(userId, friendId, hobbies, education, location, extras, age);

                    suggestions.add(new SuggestedFriend(suggestedId, username, report.getScore()));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        suggestions.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return suggestions;
    }

    public int findMutualFriends(int userId, int friendId) {
        Set<Integer> userFriends = new HashSet<>(graph.getOrDefault(userId,Collections.emptyList()));
        Set<Integer> friendFriends = new HashSet<>(graph.getOrDefault(friendId,Collections.emptyList()));

        //  intersection
        userFriends.retainAll(friendFriends);

        return userFriends.size();  // Count
    }

    public List<String> getMutualFriends(int user1, int user2) throws SQLException {

        // friend of user 1
        Set<Integer> friendsOfUser1 = new HashSet<>(graph.get(user1));
        // friend of user 2
        Set<Integer> friendsOfUser2 = new HashSet<>(graph.get(user2));

        // intersection
        friendsOfUser1.retainAll(friendsOfUser2);

        List<String> mutualFriends = new ArrayList<>();
        for (Integer friendId : friendsOfUser1) {
            String username = FriendsService.getUsernameById(friendId);
            mutualFriends.add(username);
        }

        return mutualFriends;
    }

    private ScoreReport calculateScore(int userId, int friendId, String hobbies,String education, String location,String extras, int age) {
//        System.out.println(userId + "is userId"  + friendId + "is friend_id");
        double mutualFriendWeight = 0.20;
        double hobbiesWeight = 0.15;
        double educationWeight = 0.15;
        double ageProximityWeight = 0.15;
        double locationWeight = 0.1;
        double extrasWeight = 0.1;
        double postLikeWeight = 0.1;
        double postsCountWeight =0.05;


        // mutual friends logic:
        // graph.getOrDefault(userId) get direct friends of userId.
        // graph.getOrDefault(friendId) get list of direct friends of friendId.
        // then fetch userFriends - > friendFriends

        Set<Integer> userFriends = new HashSet<>(graph.getOrDefault(userId, Collections.emptyList()));
        Set<Integer> friendFriends = new HashSet<>(graph.getOrDefault(friendId, Collections.emptyList()));
//        System.out.println(friendFriends);

        int maxMutualFriends = userFriends.isEmpty() ? 1 : userFriends.size();

        userFriends.retainAll(friendFriends);
        int mutualFriends = userFriends.size();
        boolean sharedHobbies = hobbies.equalsIgnoreCase(getUserHobbies(userId));
        boolean locationMatch = location.equalsIgnoreCase(getUserLocation(userId));
        boolean educationMatch = education.equalsIgnoreCase(getUserEducation(userId));
        boolean ageProximity = Math.abs(age - getUserAge(userId)) <= 5;
        double extraScore = 0.1;
        if (extras != null && !extras.isEmpty()) {
            extraScore = extras.equalsIgnoreCase(getUserExtras(userId)) ? 1 : 0;
        }
        boolean mutualLikes = getMutualLikes(userId, friendId);
        double likesScore = postLikeWeight * (mutualLikes?1:0);
        double postScore =0;
        boolean postCount = getFriendPostCount(friendId)>5;
        if(postCount) postScore = postsCountWeight;


        double score = (mutualFriendWeight * ((double) mutualFriends /  maxMutualFriends)) +
                (hobbiesWeight * (sharedHobbies ? 1 : 0)) +
                (locationWeight * (locationMatch ? 1 : 0)) +
                (educationWeight * (educationMatch ? 1 : 0)) +
                (ageProximityWeight * (ageProximity ? 1 : 0)) +
                (extrasWeight * extraScore)+ postLikeWeight * (mutualLikes?1:0)+
                (postsCountWeight * postScore);

        StringBuilder report = new StringBuilder();
        report.append("---- Detailed Analysis Report ----\n")
                .append("User ID: ").append(userId).append("\n")
                .append("Friend ID: ").append(friendId).append("\n")
                .append("Mutual Friends: ").append(mutualFriends).append(" / ").append(maxMutualFriends).append("\n")
                .append("Shared Hobbies: ").append(sharedHobbies ? "Yes" : "No").append("\n")
                .append("Location Match: ").append(locationMatch ? "Yes" : "No").append("\n")
                .append("Education Match: ").append(educationMatch ? "Yes" : "No").append("\n")
                .append("Age Proximity (within 5 years): ").append(ageProximity ? "Yes" : "No").append("\n")
                .append("Extras Match: ").append(extraScore == 1 ? "Yes" : "No").append("\n")
                .append("Likes Match:").append(mutualLikes ?"Yes":"No").append("\n")
                .append("Post Count:").append(postCount ?"Yes":"No").append("\n")
                .append("\nContribution Breakdown:\n")
                .append("Mutual Friends Weight: ").append(mutualFriendWeight).append(" | Contribution: ").append(mutualFriendWeight * ((double) mutualFriends /  maxMutualFriends)).append("\n")
                .append("Hobbies Weight: ").append(hobbiesWeight).append(" | Contribution: ").append(hobbiesWeight * (sharedHobbies ? 1 : 0)).append("\n")
                .append("Location Weight: ").append(locationWeight).append(" | Contribution: ").append(locationWeight * (locationMatch ? 1 : 0)).append("\n")
                .append("Education Weight: ").append(educationWeight).append(" | Contribution: ").append(educationWeight * (educationMatch ? 1 : 0)).append("\n")
                .append("Age Proximity Weight: ").append(ageProximityWeight).append(" | Contribution: ").append(ageProximityWeight * (ageProximity ? 1 : 0)).append("\n")
                .append("Extras Weight: ").append(extrasWeight).append(" | Contribution: ").append(extrasWeight * extraScore).append("\n")
                .append("Likes Weight:").append(postLikeWeight).append(" | Contribution: ").append(likesScore).append("\n")
                .append("Post Weight:").append(postsCountWeight).append(" | Contribution: ").append(postScore).append("\n")
                .append("Final Score: ").append(score).append("\n")
                .append("----------------------------------");

        System.out.println(report);

        return new ScoreReport(score, report.toString());
    }

    // fetch user hobbies
    private  String getUserHobbies(int userId) {
        String query = "SELECT hobbies FROM completeProfile WHERE user_id = ?";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("hobbies");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private  String getUserLocation(int userId) {
        String query = "SELECT location FROM completeProfile WHERE user_id = ?";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("location");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private  String getUserEducation(int userId) {
        String query = "SELECT education FROM completeProfile WHERE user_id = ?";
        try {Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("education");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private  String getUserExtras(int userId) {
        String query = "SELECT extras FROM completeProfile WHERE user_id = ?";
        try {Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("extras");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private  int getUserAge(int userId) {
        String query = "SELECT age FROM completeProfile WHERE user_id = ?";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("age");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getFriendPostCount(int userId) {
        String query = "select count(*) as count from posts where user_id=?";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Integer> findShortestPath(int startUserId, int targetUserId) {
        if (!graph.containsKey(startUserId) || !graph.containsKey(targetUserId)) {
            return Collections.emptyList(); // if invalid id
        }

        // like Min-Heap
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{startUserId, 0});

        Map<Integer, Integer> distances = new HashMap<>(); // shortest distances
        Map<Integer, Integer> previous = new HashMap<>();  // previous nodes
        Set<Integer> visited = new HashSet<>();

        // Initialize
        for (int node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(startUserId, 0);

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentNode = current[0];
            int currentDistance = current[1];

            if (!visited.add(currentNode)) {
                continue; // Skip - if already visited
            }

            if (currentNode == targetUserId) {
                break; // Stop
            }

            for (int neighbor : graph.getOrDefault(currentNode, Collections.emptyList())) {
                int newDistance = currentDistance + 1;

                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentNode);
                    pq.offer(new int[]{neighbor, newDistance});
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        for (Integer at = targetUserId; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        if (path.isEmpty() || path.getFirst() != startUserId) {
            return Collections.emptyList();
        }

        return path;
    }

    public double calculateCentrality(int userId) {
        if (!graph.containsKey(userId)) {
            System.out.println("User not found.");
            return 0.0;
        }

        int degree = graph.get(userId).size();
        int totalUsers = graph.size();
        double centrality = (double) degree /(totalUsers-1);
        System.out.println(centrality);
        return centrality;
    }

    public List<Integer> predictNetworkGrowth(int userId) {
        List<Integer> potentialConnections = new ArrayList<>();

        if (!graph.containsKey(userId)) {
            System.out.println("User not found.");
            return potentialConnections;
        }

        List<Integer> directFriends = graph.get(userId);

        for (int friend : directFriends) {
            List<Integer> friendsOfFriend = graph.get(friend);

            for (int potentialFriend : friendsOfFriend) {
                if (potentialFriend != userId && !directFriends.contains(potentialFriend)) {
                    potentialConnections.add(potentialFriend);
                }
            }
        }

        return new ArrayList<>(new HashSet<>(potentialConnections));
    }


    public boolean getMutualLikes(int userId, int friendId) {
        List<Integer> userLikedPosts = postLikesGraph.getOrDefault(userId,new ArrayList<>());
//        System.out.println(userLikedPosts +"user liked" + "userId"+userId);
        List<Integer> friendLikedPosts = postLikesGraph.getOrDefault(friendId,new ArrayList<>());
//        System.out.println(friendLikedPosts+"friend liked" + "userId"+friendId);
        // check mutual liked post exist between the user and friend
        for (int postId : userLikedPosts) {
            if (friendLikedPosts.contains(postId)) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        GraphFriendSuggestion friendSuggestion = new GraphFriendSuggestion();
//        friendSuggestion.loadGraph();
//        System.out.println(friendSuggestion.graph);
//        System.out.println(friendSuggestion.findMutualFriends(4,3));
        System.out.println(friendSuggestion.getSuggestedFriends(5));
//        System.out.println(friendSuggestion.postLikesGraph.toString());


//        System.out.println(friendSuggestion.findShortestPath(1, 7));
//
//        friendSuggestion.calculateCentrality(1);
//        friendSuggestion.calculateCentrality(2);
//        friendSuggestion.calculateCentrality(4);
//        friendSuggestion.calculateCentrality(5);
//
//        System.out.println(friendSuggestion.predictNetworkGrowth(1));
//        System.out.println(friendSuggestion.predictNetworkGrowth(4));
//        System.out.println(friendSuggestion.predictNetworkGrowth(5));
    }
}
