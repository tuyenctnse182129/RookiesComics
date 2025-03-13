package application.aicomic.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class FirebaseConfig {
    private FirebaseApp firebaseApp;
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(
                        new ClassPathResource("rookiescomics-firebase-adminsdk-fbsvc-c681c2c375.json").getInputStream()))
                .setStorageBucket("rookiescomics.firebasestorage.app")
                .build();

        // Nếu chưa khởi tạo FirebaseApp nào, khởi tạo và trả về; nếu đã có, trả về instance hiện có.
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
