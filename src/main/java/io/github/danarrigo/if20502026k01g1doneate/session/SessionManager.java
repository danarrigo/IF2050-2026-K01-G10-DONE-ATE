package io.github.danarrigo.if20502026k01g1doneate.session;

/**
 * Singleton to manage user session data across the JavaFX application.
 */
public class SessionManager {
    private static SessionManager instance;
    
    private String token;
    private String username;
    private String role;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public void clearSession() {
        this.token = null;
        this.username = null;
        this.role = null;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isLoggedIn() {
        return token != null;
    }
}
