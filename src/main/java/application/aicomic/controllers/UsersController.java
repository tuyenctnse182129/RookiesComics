package application.aicomic.controllers;

import application.aicomic.dataAccess.UsersDTO;
import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.models.Users;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.UsersRepository;
import application.aicomic.services.CustomOAuth2UserService;
import application.aicomic.services.UsersService;
import application.aicomic.services.WalletsService;
import application.aicomic.services.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;
    private final WalletsService walletsService;
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final String googleClientSecret;
    private final String webClientId;
    private final String androidClientId;

    public UsersController(
            UsersService usersService,
            WalletsService walletsService,
            UsersRepository usersRepository,
            JwtService jwtService,
            CustomOAuth2UserService customOAuth2UserService,
            @Value("${spring.security.oauth2.client.registration.google-web.client-secret}") String googleClientSecret,
            @Value("${spring.security.oauth2.client.registration.google-web.client-id}") String webClientId,
            @Value("${spring.security.oauth2.client.registration.google-android.client-id}") String androidClientId) {
        this.usersService = usersService;
        this.walletsService = walletsService;
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.googleClientSecret = googleClientSecret;
        this.webClientId = webClientId;
        this.androidClientId = androidClientId;
    }


    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable String id) {
        return usersService.getUserById(id);
    }

    @GetMapping("/{email}")
    public Users getUserByEmail(@PathVariable String email) {
        return usersService.getByEmail(email);
    }

    @PostMapping
    public Users createUser(@RequestBody Users user) {
        return usersService.saveUser(user);
    }

    @PutMapping("/{id}")
    public Users updateUsers(@PathVariable String id, @RequestBody UsersDTO usersDTO) {
        return usersService.updateUsers(id, usersDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        usersService.deleteUser(id);
    }

    @GetMapping("/admin-staff")
    public List<Users> getAdminAndStaffUsers() {
        return usersService.getAdminAndStaffUsers();
    }

    @GetMapping("/customers")
    public List<Users> getCustomerUsers() {
        return usersService.getCustomerUsers();
    }


    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable String id, @RequestParam byte role) {
        boolean updated = usersService.updateUserRole(id, role);
        if (updated) {
            return ResponseEntity.ok("Cập nhật vai trò thành công");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể cập nhật vai trò khách hàng");
    }

    @GetMapping("/logout")
    public String logout() {
        return "You have been logged out successfully!";
    }

    @PostMapping("/auth/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        try {
            String credential = request.get("credential");
            if (credential == null || credential.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Missing credential"));
            }

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(webClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Google token"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Sử dụng CustomOAuth2UserService để tạo user nếu chưa tồn tại
            OAuth2UserRequest userRequest = new OAuth2UserRequest(
                    ClientRegistration.withRegistrationId("google")
                            .clientId(webClientId)
                            .clientSecret(googleClientSecret)
                            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                            .tokenUri("https://oauth2.googleapis.com/token")
                            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                            .userNameAttributeName("email")
                            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                            .scope("email", "profile")
                            .build(),
                    new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, credential, null, null)
            );

            OAuth2User oAuth2User = customOAuth2UserService.loadUser(userRequest);

            // Lấy thông tin user
            String email = oAuth2User.getAttribute("email");
            Users user = usersRepository.findByEmail(email).orElseThrow();

            // Tạo JWT
            String token = jwtService.generateToken(user.getEmail(), String.valueOf(user.getRole()));

            String redirectUrl = switch (user.getRole()) {
                case 1, 2 -> "/admin";
                case 3 -> "/moderator";
                case 4 -> "/staffpage";
                case 5, 6, 7, 8 -> "/";
                default -> null;
            };

            if (redirectUrl == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Role not recognized"));
            }

            return ResponseEntity.ok(Map.of("token", token, "redirectUrl", redirectUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/auth/google/android")
    public ResponseEntity<?> loginWithGoogleAndroid(@RequestBody Map<String, String> request) {
        try {
            String credential = request.get("credential");
            if (credential == null || credential.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Missing credential"));
            }

            // Xác thực Firebase ID Token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(credential);
            String email = decodedToken.getEmail();
            String firstName = decodedToken.getName();
            String lastName = ""; // Firebase không cung cấp last name

            // Kiểm tra và tạo user mới nếu chưa tồn tại
            Users user = usersRepository.findByEmail(email).orElseGet(() -> {
                Users newUser = new Users();
                newUser.setEmail(email);
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setRole((byte) 5); // Mặc định role là 5 cho user mới
                return usersRepository.save(newUser);
            });

            // Tạo JWT token
            String token = jwtService.generateToken(user.getEmail(), String.valueOf(user.getRole()));

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", user.getEmail(),
                    "role", user.getRole()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

}
