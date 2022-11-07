package G2T6.G2T6.G2T6;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import G2T6.G2T6.G2T6.models.security.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.*;

import G2T6.G2T6.G2T6.repository.RefreshTokenRepository;
import G2T6.G2T6.G2T6.repository.*;
import G2T6.G2T6.G2T6.payload.request.*;
import G2T6.G2T6.G2T6.payload.response.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository usersRepo;

    @Autowired
    RefreshTokenRepository refreshRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @BeforeEach()
    void createUser() {

        // Making an admin user for test
        User newUser = new User("johnTheAdmin", "johnny@gmail.com",
                encoder.encode("myStrongPw"), "ROLE_ADMIN", false);
        usersRepo.save(newUser);
    }

    @AfterEach()
    void tearDown() {
        // clear the database after each test
        refreshRepo.deleteAll();
        usersRepo.deleteAll();
    }

    @Test
    public void login_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/api/auth/signin");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("johnTheAdmin");
        loginRequest.setPassword("myStrongPw");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<JwtResponse> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST, entity, JwtResponse.class);

        // ResponseEntity<JwtResponse> result = restTemplate.postForEntity(uri,
        // loginRequest, JwtResponse.class);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("johnTheAdmin", responseEntity.getBody().getUsername());
        // assertEquals(200, result.getStatusCodeValue());
        // assertEquals("johnTheAdmin", response.getUsername());
        assertNotNull(responseEntity.getBody().getAccessToken());
        assertNotNull(responseEntity.getBody().getRefreshToken());
    }

    @Test
    public void login_WrongPassword_Failure() throws Exception {

        URI uri = new URI(baseUrl + port + "/api/auth/signin");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("johnTheAdmin");
        loginRequest.setPassword("wrongPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<JwtResponse> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST, entity, JwtResponse.class);

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertEquals("Error: Invalid username or password!", responseEntity.getBody().getMessage());
    }

    @Test
    public void register_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/api/auth/signup");
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("johnNotTheAdmin");
        signUpRequest.setEmail("johnNotTheAdmin@gmail.com");
        signUpRequest.setPassword("myStrongPwAgain");
        signUpRequest.setRole("USER");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SignupRequest> entity = new HttpEntity<>(signUpRequest, headers);

        ResponseEntity<MessageResponse> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST, entity, MessageResponse.class);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("User registered successfully!", responseEntity.getBody().getMessage());

    }
}
