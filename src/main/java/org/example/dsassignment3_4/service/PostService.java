package org.example.dsassignment3_4.service;

import org.example.dsassignment3_4.dao.PostDAO;
import org.example.dsassignment3_4.model.Post;

import java.util.Queue;

public class PostService {

    public static void savePostToDatabase(int userId, String postText) {
        PostDAO.savePostToDatabase(userId, postText);
    }

    public static boolean checkIfFirstPost(int userId) {
        return PostDAO.checkIfFirstPost(userId);
    }

    public static boolean checkIfPostDone(int userId) {
        return PostDAO.checkIfPostDone(userId);
    }

    public static boolean checkIfPostGotTenLikes(int userId) {
        return PostDAO.checkIfPostGotTenLikes(userId);
    }

    public static Queue<Post> fetchPostsFromDatabase()  {
        return PostDAO.fetchPostsFromDatabase();
    }

    public static Queue<Post> fetchPostsFromDatabase(int userId)  {
        return PostDAO.fetchPostsFromDatabase(userId);
    }

    public static boolean savePostRating(int postId, int userId) {
        return PostDAO.savePostRating(postId, userId);
    }

    public static void deletePost(int postId) {
        PostDAO.deletePost(postId);
    }

    public static boolean hasUserLikedPost(int postId, int userId) {
        return PostDAO.hasUserLikedPost(postId, userId);
    }

    public static void updatePostContent(int postId,String content){
        PostDAO.updatePostContent(postId, content);

    }


}
