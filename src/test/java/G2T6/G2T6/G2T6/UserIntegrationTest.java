package G2T6.G2T6.G2T6;

import G2T6.G2T6.G2T6.misc.CONSTANTVARIABLES;
import G2T6.G2T6.G2T6.misc.State;
import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.models.security.User;
import G2T6.G2T6.G2T6.payload.request.LoginRequest;
import G2T6.G2T6.G2T6.payload.response.JwtResponse;
import G2T6.G2T6.G2T6.repository.GameStatsRepository;
import G2T6.G2T6.G2T6.repository.RefreshTokenRepository;
import G2T6.G2T6.G2T6.repository.StateRepository;
import G2T6.G2T6.G2T6.repository.UserRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StateRepository stateRepo;

    @Autowired
    private GameStatsRepository gameStatsRepo;

    @Autowired
    UserRepository usersRepo;

    @Autowired
    RefreshTokenRepository refreshRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepo;

    // private User regularUser;

    // private User adminUser;

    private String regularUserToken;

    private String adminUserToken;

    @BeforeEach()
    void createUser() throws Exception {
        // Creating an admin user for test
        User adminUser = new User("johnTheAdmin", "johnny@gmail.com",
                encoder.encode("myStrongPw"), "ROLE_ADMIN", false);
        usersRepo.save(adminUser);

        // Creating normal user for test
        User normalUser = new User("bobTheNormie", "bobby@gmail.com",
                encoder.encode("password"), "ROLE_USER", false);
        usersRepo.save(normalUser);
    }

    @AfterEach
    void tearDown() {
        // clear the database after each test
        refreshRepo.deleteAll();
        usersRepo.deleteAll();
    }

    // called to authenticate as Admin User
    public HttpHeaders generateAuthAdmin() throws Exception {
        // Generate Headers (Authentication as Admin User)
        URI uriLogin2 = new URI(baseUrl + port + "/api/auth/signin");
        LoginRequest loginRequest2 = new LoginRequest();
        loginRequest2.setUsername("johnTheAdmin");
        loginRequest2.setPassword("myStrongPw");
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<LoginRequest> entity2 = new HttpEntity<>(loginRequest2, headers2);
        ResponseEntity<JwtResponse> responseEntity2 = restTemplate.exchange(
                uriLogin2,
                HttpMethod.POST, entity2, JwtResponse.class);
        headers2.add("Authorization", "Bearer " + responseEntity2.getBody().getAccessToken());
        return headers2;
    }

    // called to authenticate as Normal User
    public HttpHeaders generateAuthNormal() throws Exception {
        // Generate Headers (Authentication as Normal User)
        URI uriLogin = new URI(baseUrl + port + "/api/auth/signin");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("bobTheNormie");
        loginRequest.setPassword("password");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<JwtResponse> responseEntity = restTemplate.exchange(
                uriLogin,
                HttpMethod.POST, entity, JwtResponse.class);
        headers.add("Authorization", "Bearer " + responseEntity.getBody().getAccessToken());
        return headers;
    }

    // @Test
    // void createUser_WhenUserIsAdmin_ThenReturn201() throws Exception {
    //     // Generate Headers (Authentication as Admin User)
    //     HttpHeaders headers = generateAuthAdmin();

    //     // Create a new user
    //     URI uri = new URI(baseUrl + port + "/api/user/create");
    //     User user = new User("testUser", "testUser@gmail.com",
    //             encoder.encode("password"), "ROLE_USER", false);

    //     // Send request
    //     HttpEntity<User> entity = new HttpEntity<>(user, headers);
    //     ResponseEntity<String> responseEntity = restTemplate.exchange(
    //             uri,
    //             HttpMethod.POST, entity, String.class);

    //     // Assert
    //     assertEquals(201, responseEntity.getStatusCodeValue());

    // }
}
