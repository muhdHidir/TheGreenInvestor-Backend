package G2T6.G2T6.G2T6;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.models.Question;
import G2T6.G2T6.G2T6.repository.QuestionRepository;
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

import java.util.*;

import G2T6.G2T6.G2T6.repository.RefreshTokenRepository;
import G2T6.G2T6.G2T6.repository.UserRepository;
import G2T6.G2T6.G2T6.payload.request.*;
import G2T6.G2T6.G2T6.payload.response.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class QuestionIntegrationTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private QuestionRepository questions;

	@Autowired
	private UserRepository usersRepo;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private RefreshTokenRepository refreshRepo;
	
	@BeforeEach()
	void createUser() throws URISyntaxException {
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
		questions.deleteAll();
		usersRepo.deleteAll();
	}

	// called to authenticate as Admin User
	public HttpHeaders generateAuthAdmin() throws URISyntaxException {
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
	public HttpHeaders generateAuthNormal() throws URISyntaxException {
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

	// @GetMapping("/questions")
	@Test
	public void listQuestions_NoQuestions_ReturnEmptyList() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/questions");

		ResponseEntity<Question[]> result = restTemplate.getForEntity(uri, Question[].class);
		Question[] savedQuestions = result.getBody();

		assertEquals(200, result.getStatusCode().value());
		assertEquals(0, savedQuestions.length);
	}

	@Test
	public void listQuestions_HaveQuestions_ReturnQuestions() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/questions");
		Question q1 = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
		Question q2 = questions.save(new Question("Question 2", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", false));

		ResponseEntity<Question[]> result = restTemplate.getForEntity(uri, Question[].class);
		Question[] savedQuestions = result.getBody();

		assertEquals(200, result.getStatusCode().value());
		assertEquals(2, savedQuestions.length);

		q1.setOptions(new ArrayList<Option>()); // for verification (otherwise will be null)
		q2.setOptions(new ArrayList<Option>()); // for verification (otherwise will be null)

		assertEquals(q1, savedQuestions[0]);
		assertEquals(q2, savedQuestions[1]);
	}


	// GetMapping("/questions/{id}")
	@Test
	public void getQuestion_ValidQuestionId_Success() throws Exception {
		Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
		URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId());
		
		ResponseEntity<Question> result = restTemplate.getForEntity(uri, Question.class);
		
		question.setOptions(new ArrayList<Option>()); // for verification (otherwise will be null)

		assertEquals(200, result.getStatusCode().value());
		assertEquals(question, result.getBody());
	}

	@Test
	public void getQuestion_InvalidQuestionId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/questions/1");

		ResponseEntity<Question> result = restTemplate.getForEntity(uri, Question.class);

		assertEquals(404, result.getStatusCode().value());
	}

	// PostMapping("/questions")
	@Test
	public void addQuestion_newQuestionIsAdmin_Success() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/questions");
		Question question = new Question(1L, "Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", null, true, null);

		HttpHeaders headers = generateAuthAdmin();
		ResponseEntity<Question> result = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(question, headers), Question.class);

		assertEquals(201, result.getStatusCode().value());
		assertEquals(question.getQuestion(), result.getBody().getQuestion());
		assertEquals(question.getImageLink(), result.getBody().getImageLink());
		assertEquals(question.isOpenEnded(), result.getBody().isOpenEnded());
	}

	@Test
	public void addQuestion_newQuestionNonAdmin_Failure() throws Exception {
		// Use Headers to Authenticate and Test
		URI uri = new URI(baseUrl + port + "/api/questions");
		Question question = new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true);

		HttpHeaders headers = generateAuthNormal();
		ResponseEntity<Question> result = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(question, headers), Question.class);

		assertEquals(403, result.getStatusCode().value());
	}

	@Test
	public void addQuestion_existingQuestionIsAdmin_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/questions");
		Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
		
		HttpHeaders headers = generateAuthAdmin();
		ResponseEntity<Question> result = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(question, headers), Question.class);

		assertEquals(409, result.getStatusCode().value());
	}

	// @PutMapping("/questions/{id}")
	@Test
	public void updateQuestion_invalidQuestionIsAdmin_Failure() throws Exception {
		Question question = new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true);
		URI uri = new URI(baseUrl + port + "/api/questions/" + 1);
		
		HttpHeaders headers = generateAuthAdmin();
		ResponseEntity<Question> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(question, headers), Question.class);

		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void updateQuestion_validQuestionIsAdmin_Success() throws Exception {
		Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
		Question updatedQuestion = new Question(question.getId(), "Question 2", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img12.jpg", new ArrayList<Option>(), false, null);
		
		URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId());
		HttpHeaders headers = generateAuthAdmin();
		ResponseEntity<Question> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(updatedQuestion, headers), Question.class);

		assertEquals(200, result.getStatusCode().value());
		assertEquals(updatedQuestion, result.getBody());
	}

	@Test
	public void updateQuestion_validQuestionNotAdmin_Failure() throws Exception {
		Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
		Question updatedQuestion = new Question(question.getId(), "Question 2", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img12.jpg", new ArrayList<Option>(), false, null);
		
		URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId());
		HttpHeaders headers = generateAuthNormal();
		ResponseEntity<Question> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(updatedQuestion, headers), Question.class);

		assertEquals(403, result.getStatusCode().value());
	}

	// @DeleteMapping("/questions/{id}")
	@Test
	public void deleteQuestion_validQuestionIsAdmin_Success() throws Exception {
		Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));

		URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId());
		HttpHeaders headers = generateAuthAdmin();
		ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

		assertEquals(200, result.getStatusCode().value());
		assertNull(result.getBody());
	}

	@Test
	public void deleteQuestion_invalidQuestionIsAdmin_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/questions/1");
		
		HttpHeaders headers = generateAuthAdmin();
		ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void deleteQuestion_validQuestionNotAdmin_Success() throws Exception {
		Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));

		URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId());
		HttpHeaders headers = generateAuthNormal();
		ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

		assertEquals(403, result.getStatusCode().value());
		assertNull(result.getBody());
	}
}