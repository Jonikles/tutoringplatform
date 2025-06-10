package com.tutoringplatform.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.tutoringplatform.models.User;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    private static SessionManager instance;
    private Map<String, User> sessions;
    private Map<String, String> sessionTokens;

    private SessionManager() {
        this.sessions = new HashMap<>();
        this.sessionTokens = new HashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String createSession(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User and User ID cannot be null for session creation.");
        }
        String token = "SESSION-" + UUID.randomUUID().toString();
        sessions.put(user.getId(), user);
        sessionTokens.put(token, user.getId());
        System.out.println("SessionManager: Created session for user " + user.getName() + " with token " + token);
        return token;
    }

    public User getSession(String token) {
        if (token == null)
            return null;
        String userId = sessionTokens.get(token);
        if (userId != null) {
            User user = sessions.get(userId);
            if (user != null) {
                System.out
                        .println("SessionManager: Retrieved session for token " + token + ", user: " + user.getName());
            } else {
                System.out.println("SessionManager: User not found in sessions map for userId " + userId + " (token: "
                        + token + ")");
            }
            return user;
        }
        System.out.println("SessionManager: No userId found for token " + token);
        return null;
    }

    public void endSession(String token) {
        if (token == null)
            return;
        String userId = sessionTokens.remove(token);
        if (userId != null) {
            User removedUser = sessions.remove(userId);
            if (removedUser != null) {
                System.out.println(
                        "SessionManager: Ended session for user " + removedUser.getName() + " (token: " + token + ")");
            } else {
                System.out.println("SessionManager: User was not in active sessions map for userId " + userId
                        + " (token: " + token + ")");
            }
        } else {
            System.out.println("SessionManager: No session to end for token " + token);
        }
    }

    public boolean isValidSession(String token) {
        boolean isValid = token != null && sessionTokens.containsKey(token)
                && sessions.containsKey(sessionTokens.get(token));
        System.out.println("SessionManager: Token " + token + " validity check: " + isValid);
        return isValid;
    }
}