package georgetenev.usereventconsumer.dto;


public class UserLogin {
    private String id;
    private Long timestamp;

    // Explicit no-argument constructor
    public UserLogin() {
    }

    // Copy constructor
    public UserLogin(UserLogin other) {
        this.id = other.id;
        this.timestamp = other.timestamp;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
