import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String storedHash = "$2a$10$7EqJtq98hPqEX7fNZaFWoO5zQ4jJWOweAV/QXp1eml8n1uX9g6OCW";

        System.out.println("Testing password: " + rawPassword);
        System.out.println("Against hash: " + storedHash);
        System.out.println("Match result: " + encoder.matches(rawPassword, storedHash));

        // Generate a new hash for comparison
        String newHash = encoder.encode(rawPassword);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash matches password: " + encoder.matches(rawPassword, newHash));
    }
}