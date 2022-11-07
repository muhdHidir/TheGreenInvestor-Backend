package G2T6.G2T6.G2T6;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.models.Question;
import G2T6.G2T6.G2T6.repository.OptionRepository;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.*;

import G2T6.G2T6.G2T6.repository.RefreshTokenRepository;
import G2T6.G2T6.G2T6.repository.UserRepository;
import G2T6.G2T6.G2T6.payload.request.*;
import G2T6.G2T6.G2T6.payload.response.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OptionIntegrationTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private QuestionRepository questions;

	@Autowired
	private OptionRepository options;

	@Autowired
	private UserRepository usersRepo;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private RefreshTokenRepository refreshRepo;
	
	@BeforeEach()
	void createUser() {
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

    // @GetMapping("/{questionId}/options")
    @Test
    public void getAllOptionsByQuestionId_invalidQuestionId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/questions/" + 1 + "/options");

		ResponseEntity<Option> result = restTemplate.getForEntity(uri, Option.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getAllOptionsByQuestionId_validQuestionId_Success() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
		
        Option option1 = options.save(new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0));
        Option option2 = options.save(new Option("Option 2", "Okay Feedback", question, 0, 0, 0, 0));
        Option option3 = options.save(new Option("Option 3", "Negative Feedback", question, 0, 0, 0, 0));

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options");

		ResponseEntity<Option[]> result = restTemplate.getForEntity(uri, Option[].class);
        Option[] savedOptions = result.getBody();

		assertEquals(200, result.getStatusCode().value());
        assertEquals(3, savedOptions.length);

        assertEquals(option1.getOption(), savedOptions[0].getOption());
        assertEquals(option1.getFeedback(), savedOptions[0].getFeedback());

        assertEquals(option2.getOption(), savedOptions[1].getOption());
        assertEquals(option2.getFeedback(), savedOptions[1].getFeedback());

        assertEquals(option3.getOption(), savedOptions[2].getOption());
        assertEquals(option3.getFeedback(), savedOptions[2].getFeedback());
    }

    // @PostMapping("/{questionId}/options")
    @Test
    public void addOption_existingOption_Failure() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option option = options.save(new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0));

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options");
        HttpHeaders headers = generateAuthAdmin();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(option, headers), Option.class);

        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    public void addOption_newOptionIsAdmin_Success() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option option = new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0);

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options");
        HttpHeaders headers = generateAuthAdmin();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(option, headers), Option.class);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(option.getOption(), result.getBody().getOption());
        assertEquals(option.getFeedback(), result.getBody().getFeedback());
        assertEquals(option.getSustainabilityImpact(), result.getBody().getSustainabilityImpact());
        assertEquals(option.getMoraleImpact(), result.getBody().getMoraleImpact());
        assertEquals(option.getIncomeImpact(), result.getBody().getIncomeImpact());
        assertEquals(option.getCostImpact(), result.getBody().getCostImpact());
    }

    @Test
    public void addOption_invalidQuestionIsAdmin_Failure() throws Exception {
        Question question = new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true);
        Option option = new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0);

        URI uri = new URI(baseUrl + port + "/api/questions/88/options");
        HttpHeaders headers = generateAuthAdmin();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(option, headers), Option.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addOption_newOptionNotAdmin_Success() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option option = new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0);

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options");
        HttpHeaders headers = generateAuthNormal();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(option, headers), Option.class);

        assertEquals(403, result.getStatusCode().value());
    }

    // @PutMapping("/{questionId}/options/{optionId}")
    @Test
    public void updateOption_validOptionIsAdmin_Success() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option oldOption = options.save(new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0));
        Option newOption = new Option(question.getId(), "Option 2", "Trash Feedback", 0, 0, 0, 0, null); // set to null, since @jsonignore doesn't return question

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options/" + oldOption.getId());
        HttpHeaders headers = generateAuthAdmin();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newOption, headers), Option.class);

        assertEquals(200, result.getStatusCode().value()); 
    }

    @Test
    public void updateOption_invalidOptionIsAdmin_Failure() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option oldOption = options.save(new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0));
        Option newOption = new Option("Option 2", "Trash Feedback", question, 0, 0, 0, 0);

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options/404");
        HttpHeaders headers = generateAuthAdmin();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newOption, headers), Option.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateOption_validOptionNotAdmin_Failure() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option oldOption = options.save(new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0));
        Option newOption = new Option(question.getId(), "Option 2", "Trash Feedback", 0, 0, 0, 0, null); // set to null, since @jsonignore doesn't return question

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options/" + oldOption.getId());
        HttpHeaders headers = generateAuthNormal();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newOption, headers), Option.class);

        assertEquals(403, result.getStatusCode().value());
    }

    // @DeleteMapping("/{questionId}/options/{optionId}")
    @Test
    public void deleteOption_validOptionIsAdmin_Success() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option oldOption = options.save(new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0));

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options/" +  + oldOption.getId());
        HttpHeaders headers = generateAuthAdmin();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers), Option.class);

        assertEquals(200, result.getStatusCode().value());
        assertNull(result.getBody());
    }

    @Test
    public void deleteOption_invalidOptionIsAdmin_Failure() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options/404");
        HttpHeaders headers = generateAuthAdmin();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers), Option.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteOption_validOptionNotAdmin_Failure() throws Exception {
        Question question = questions.save(new Question("Question 1", "https://tgi-bucket.s3.ap-southeast-1.amazonaws.com/img11.jpg", true));
        Option oldOption = options.save(new Option("Option 1", "Positive Feedback", question, 0, 0, 0, 0));

        URI uri = new URI(baseUrl + port + "/api/questions/" + question.getId() + "/options/" +  + oldOption.getId());
        HttpHeaders headers = generateAuthNormal();
        ResponseEntity<Option> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers), Option.class);

        assertEquals(403, result.getStatusCode().value());
        assertNull(result.getBody());
    }
}
