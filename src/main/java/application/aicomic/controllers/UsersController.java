package application.aicomic.controllers;

import application.aicomic.dataAccess.UsersDTO;
import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.models.Users;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.UsersRepository;
import application.aicomic.services.UsersService;
import application.aicomic.services.WalletsService;
import application.aicomic.services.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private final UsersService usersService;
    private final WalletsService walletsService;
    private final UsersRepository usersRepository;
    private final JwtService jwtService;

    @Autowired
    public UsersController(UsersService usersService, WalletsService walletsService, UsersRepository usersRepository, JwtService jwtService) {
        this.usersService = usersService;
        this.walletsService = walletsService;
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public UsersController(UsersService usersService, UsersRepository usersRepository, WalletsService walletsService, JwtService jwtService) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.walletsService = walletsService;
        this.jwtService = jwtService;
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
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Google token"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            Users user = processUser(payload);


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

    private Users processUser(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        Optional<Users> userOptional = usersRepository.findByEmail(email);
        return userOptional.orElseGet(() -> {
            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);

            newUser.setRole((byte) 5);
            return usersRepository.save(newUser);
        });
    }

    @GetMapping("/logout")
    public String logout() {
        return "You have been logged out successfully!";
    }

}

