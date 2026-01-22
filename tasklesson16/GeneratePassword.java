import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("user password: " + encoder.encode("user"));
        System.out.println("admin password: " + encoder.encode("admin"));
    }
}
