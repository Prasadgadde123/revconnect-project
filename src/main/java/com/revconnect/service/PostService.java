package com.revconnect.service;

import com.revconnect.dao.*;
import com.revconnect.model.*;
import com.revconnect.util.AppLogger;
import com.revconnect.util.ConsoleColors;
import java.util.List;
import java.util.Scanner;

public class PostService {
    private PostDAO postDAO;
    private LikeDAO likeDAO;
    private CommentDAO commentDAO;
    private UserDAO userDAO;
    private NotificationService notificationService;
    private Scanner scanner;

    public PostService() {
        this.postDAO = new PostDAO();
        this.likeDAO = new LikeDAO();
        this.commentDAO = new CommentDAO();
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
        this.scanner = new Scanner(System.in);
        AppLogger.info("PostService initialized");
    }

    // CREATE POST
    public boolean createPost(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "CREATE POST" + ConsoleColors.RESET);

        System.out.print("Enter your post content: ");
        String content = scanner.nextLine().trim();

        if (content.isEmpty()) {
            System.out.println("Post content cannot be empty.");
            return false;
        }

        if (content.equalsIgnoreCase("cancel")) {
            System.out.println("Post creation cancelled.");
            return false;
        }

        // Create post object
        Post post = new Post(user.getUserId(), content);

        // Save to database
        boolean success = postDAO.createPost(post);

        if (success) {
            System.out.println(ConsoleColors.GREEN + "✅ Post created successfully!" + ConsoleColors.RESET);
            return true;
        } else {
            System.out.println(ConsoleColors.RED + "❌ Failed to create post." + ConsoleColors.RESET);
            return false;
        }
    }

    // VIEW FEED
    public void viewFeed(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "YOUR FEED" + ConsoleColors.RESET);

        List<Post> posts = postDAO.getPostsByUserId(user.getUserId());

        if (posts.isEmpty()) {
            System.out.println("Your feed is empty. Create your first post!");
        } else {
            for (Post post : posts) {
                displayPost(post, user);
            }
        }
    }

    // LIKE/UNLIKE POST - WORKING VERSION
    public void likePost(int postId, User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "LIKE/UNLIKE POST" + ConsoleColors.RESET);

        try {
            // Check if user already liked this post
            boolean alreadyLiked = likeDAO.hasUserLikedPost(postId, user.getUserId());

            if (alreadyLiked) {
                // Unlike the post
                System.out.print("You already liked this post. Unlike it? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("yes")) {
                    boolean success = likeDAO.removeLike(postId, user.getUserId());
                    if (success) {
                        System.out.println(ConsoleColors.YELLOW + "👍 Post unliked!" + ConsoleColors.RESET);
                        AppLogger.info("User " + user.getUsername() + " unliked post " + postId);
                    } else {
                        System.out.println("Failed to unlike post.");
                    }
                } else {
                    System.out.println("Like action cancelled.");
                }
            } else {
                // Like the post
                System.out.print("Like this post? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("yes")) {
                    boolean success = likeDAO.addLike(postId, user.getUserId());
                    if (success) {
                        System.out.println(ConsoleColors.GREEN + "❤️ Post liked!" + ConsoleColors.RESET);
                        AppLogger.info("User " + user.getUsername() + " liked post " + postId);

                        // Get post owner and send notification if not own post
                        Post post = postDAO.getPostById(postId);
                        if (post != null && post.getUserId() != user.getUserId()) {
                            notificationService.sendLikeNotification(post.getUserId(), user.getUserId(), postId);
                        }
                    } else {
                        System.out.println("Failed to like post.");
                    }
                } else {
                    System.out.println("Like action cancelled.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            AppLogger.error("Error in likePost", e);
        }
    }

    // COMMENT ON POST - WORKING VERSION
    public void commentOnPost(int postId, User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "ADD COMMENT" + ConsoleColors.RESET);

        try {
            System.out.print("Enter your comment: ");
            String content = scanner.nextLine().trim();

            if (content.isEmpty()) {
                System.out.println("Comment cannot be empty.");
                return;
            }

            if (content.equalsIgnoreCase("cancel")) {
                System.out.println("Comment cancelled.");
                return;
            }

            Comment comment = new Comment(postId, user.getUserId(), content);
            boolean success = commentDAO.createComment(comment);

            if (success) {
                System.out.println(ConsoleColors.GREEN + "💬 Comment posted!" + ConsoleColors.RESET);
                AppLogger.info("User " + user.getUsername() + " commented on post " + postId);

                // Get post owner and send notification if not own post
                Post post = postDAO.getPostById(postId);
                if (post != null && post.getUserId() != user.getUserId()) {
                    notificationService.sendCommentNotification(post.getUserId(), user.getUserId(), postId, comment.getCommentId());
                }
            } else {
                System.out.println("Failed to post comment.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            AppLogger.error("Error in commentOnPost", e);
        }
    }

    // VIEW COMMENTS - WORKING VERSION
    public void viewComments(int postId) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "COMMENTS" + ConsoleColors.RESET);

        try {
            List<Comment> comments = commentDAO.getCommentsByPostId(postId);

            if (comments.isEmpty()) {
                System.out.println("No comments yet. Be the first to comment!");
            } else {
                System.out.println("Found " + comments.size() + " comments:");
                System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

                for (int i = 0; i < comments.size(); i++) {
                    Comment comment = comments.get(i);
                    System.out.println(ConsoleColors.YELLOW + comment.getFullName() +
                            " (@" + comment.getUsername() + ")" + ConsoleColors.RESET);
                    System.out.println("  " + comment.getContent());
                    System.out.println(ConsoleColors.GREEN + "  Posted: " +
                            comment.getCreatedAt().toLocalDate() + ConsoleColors.RESET);
                    System.out.println("-".repeat(40));
                }
            }

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            AppLogger.error("Error in viewComments", e);
        }
    }

    // DISPLAY POST
    private void displayPost(Post post, User currentUser) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + post.getFullName() +
                " (@" + post.getUsername() + ")" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + post.getCreatedAt() + ConsoleColors.RESET);

        System.out.println("\n" + post.getContent());

        // Show hashtags if any
        if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
            System.out.print(ConsoleColors.BLUE);
            String[] hashtags = post.getHashtags().split(",");
            for (String hashtag : hashtags) {
                System.out.print("#" + hashtag.trim() + " ");
            }
            System.out.println(ConsoleColors.RESET);
        }

        // Get engagement stats
        int likeCount = likeDAO.getLikeCount(post.getPostId());
        List<Comment> comments = commentDAO.getCommentsByPostId(post.getPostId());
        boolean userLiked = likeDAO.hasUserLikedPost(post.getPostId(), currentUser.getUserId());

        System.out.printf(ConsoleColors.CYAN + "\n👍 %d | 💬 %d" + ConsoleColors.RESET, likeCount, comments.size());

        if (userLiked) {
            System.out.print(" | ❤️ You liked this");
        }

        System.out.println("\nPost ID: " + post.getPostId());
        System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);
    }

    // VIEW MY POSTS
    public void viewMyPosts(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "YOUR POSTS" + ConsoleColors.RESET);

        List<Post> posts = postDAO.getPostsByUserId(user.getUserId());

        if (posts.isEmpty()) {
            System.out.println("You haven't created any posts yet.");
        } else {
            for (int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                System.out.println(ConsoleColors.CYAN + "\n[" + (i+1) + "] " + ConsoleColors.RESET);
                System.out.println("Content: " +
                        (post.getContent().length() > 50 ?
                                post.getContent().substring(0, 50) + "..." : post.getContent()));
                System.out.println("Date: " + post.getCreatedAt().toLocalDate());
                System.out.println("Likes: " + likeDAO.getLikeCount(post.getPostId()));
                System.out.println("Comments: " + commentDAO.getCommentsByPostId(post.getPostId()).size());
                System.out.println("ID: " + post.getPostId());
            }
        }
    }

    // EDIT MY POST
    public void editMyPost(User user) {
        viewMyPosts(user);

        List<Post> posts = postDAO.getPostsByUserId(user.getUserId());
        if (posts.isEmpty()) {
            return;
        }

        System.out.print("\nEnter post number to edit (0 to cancel): ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice > 0 && choice <= posts.size()) {
                Post post = posts.get(choice - 1);

                System.out.println("Current content:");
                System.out.println(post.getContent());
                System.out.print("\nEnter new content: ");
                String newContent = scanner.nextLine();

                post.setContent(newContent);
                boolean success = postDAO.updatePost(post);

                if (success) {
                    System.out.println(ConsoleColors.GREEN + "✅ Post updated successfully!" + ConsoleColors.RESET);
                } else {
                    System.out.println("Failed to update post.");
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    // DELETE MY POST
    public void deleteMyPost(User user) {
        viewMyPosts(user);

        List<Post> posts = postDAO.getPostsByUserId(user.getUserId());
        if (posts.isEmpty()) {
            return;
        }

        System.out.print("\nEnter post number to delete (0 to cancel): ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice > 0 && choice <= posts.size()) {
                Post post = posts.get(choice - 1);

                System.out.print("Are you sure you want to delete this post? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("yes")) {
                    boolean success = postDAO.deletePost(post.getPostId());
                    if (success) {
                        System.out.println(ConsoleColors.GREEN + "✅ Post deleted successfully!" + ConsoleColors.RESET);
                    } else {
                        System.out.println("Failed to delete post.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    // TRENDING POSTS
    public void viewTrendingPosts() {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "TRENDING POSTS" + ConsoleColors.RESET);

        System.out.println("🔥 Most engaged posts this week:");
        System.out.println("\n1. " + ConsoleColors.YELLOW + "User1" + ConsoleColors.RESET);
        System.out.println("   Great content! #trending #viral");
        System.out.println("   👍 250 likes | 💬 45 comments");

        System.out.println("\n2. " + ConsoleColors.YELLOW + "User2" + ConsoleColors.RESET);
        System.out.println("   Amazing tutorial! #learning #tech");
        System.out.println("   👍 180 likes | 💬 32 comments");

        System.out.println("\n3. " + ConsoleColors.YELLOW + "User3" + ConsoleColors.RESET);
        System.out.println("   Check out our new product! #business");
        System.out.println("   👍 120 likes | 💬 28 comments");
    }

    // SEARCH POSTS BY HASHTAG
    public void searchPostsByHashtag() {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "SEARCH POSTS BY HASHTAG" + ConsoleColors.RESET);

        System.out.print("Enter hashtag (without #): ");
        String hashtag = scanner.nextLine().trim().toLowerCase();

        if (hashtag.isEmpty()) {
            System.out.println("Please enter a hashtag.");
            return;
        }

        System.out.println("\n🔍 Searching for posts with #" + hashtag + "...");

        List<Post> foundPosts = postDAO.searchPostsByHashtag(hashtag);

        if (foundPosts.isEmpty()) {
            System.out.println("No posts found with #" + hashtag);
        } else {
            System.out.println("Found " + foundPosts.size() + " posts:");
            for (Post post : foundPosts) {
                System.out.println("\n" + ConsoleColors.YELLOW + post.getFullName() + ConsoleColors.RESET);
                System.out.println(post.getContent());
                System.out.println("Date: " + post.getCreatedAt().toLocalDate());
            }
        }
    }
}