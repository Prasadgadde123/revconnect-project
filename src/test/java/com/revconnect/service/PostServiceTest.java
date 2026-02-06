package com.revconnect.service;

import com.revconnect.model.PersonalUser;
import com.revconnect.model.Post;
import com.revconnect.util.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Post Service Tests")
class PostServiceTest {

    private PersonalUser testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new PersonalUser();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");

        testPost = new Post();
        testPost.setPostId(1);
        testPost.setUserId(1);
        testPost.setContent("Test post content");
        testPost.setCreatedAt(LocalDateTime.now());

        AppLogger.info("Setting up PostServiceTest");
    }

    @Test
    @DisplayName("Test post creation")
    void testPostCreation() {
        assertNotNull(testPost);
        assertEquals(1, testPost.getPostId());
        assertEquals("Test post content", testPost.getContent());
        AppLogger.info("Post creation test passed");
    }

    @Test
    @DisplayName("Test user creation")
    void testUserCreation() {
        assertNotNull(testUser);
        assertEquals(1, testUser.getUserId());
        assertEquals("testuser", testUser.getUsername());
        assertEquals("Test User", testUser.getFullName());
        AppLogger.info("User creation test passed");
    }

    @Test
    @DisplayName("Test basic relationship")
    void testBasicRelationship() {
        assertEquals(testUser.getUserId(), testPost.getUserId());
        AppLogger.info("Basic relationship test passed");
    }
}