package G2T6.G2T6.G2T6;

import G2T6.G2T6.G2T6.misc.CONSTANTVARIABLES;
import G2T6.G2T6.G2T6.misc.State;
import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.models.security.User;
import G2T6.G2T6.G2T6.repository.GameStatsRepository;
import G2T6.G2T6.G2T6.repository.StateRepository;
import G2T6.G2T6.G2T6.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StateIntegrationTest {
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
    private UserRepository userRepo;

    private User regularUser;


    @AfterEach
    void tearDown() {
        // clear the database after each test
        stateRepo.deleteAll();
        userRepo.deleteAll();
        gameStatsRepo.deleteAll();
    }
    @BeforeEach
    void setUp(){
        regularUser = userRepo.save(new User("ckasdasd123", "ck123@gmail.com", "Password1232", "GUEST"));
    }


     @Test
     public void getState_Success() throws Exception {
         URI uri = new URI(baseUrl + port + "/api/states");
         stateRepo.save(new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, State.completed));

         ResponseEntity<CurrentState[]> result = restTemplate.getForEntity(uri, CurrentState[].class);
         CurrentState[] states = result.getBody();

         assertEquals(200, result.getStatusCode().value());
         assertEquals(1, states.length);

     }

      @Test
      public void getUserState_ValidUser_Success() throws Exception{
          List<CurrentState> ckStates = new ArrayList<>();
          CurrentState currentState01 = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, State.completed, regularUser);
          CurrentState currentState02 = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE, regularUser);
          ckStates.add(currentState01);
          ckStates.add(currentState02);


          List<GameStats> ckStats = new ArrayList<>();
          GameStats stats01 = new GameStats(0, 0, 0, regularUser, currentState01);
          ckStats.add(stats01);

          currentState01.setGameStats(stats01);

          regularUser.setCurrentState(ckStates);
          regularUser.setGameStats(ckStats);

          stateRepo.save(currentState01);
          stateRepo.save(currentState02);

          gameStatsRepo.save(stats01);

          URI uri = new URI(baseUrl + port + "/api/id/" + regularUser.getId() + "/states");
          ResponseEntity<CurrentState[]> result = restTemplate.getForEntity(uri, CurrentState[].class);
          CurrentState[] results = result.getBody();
          assertEquals(200, result.getStatusCode().value());
          assertEquals(2, results.length);
          assertEquals(0, results[0].getYearValue());
     }

     @Test
     public void getUserState_InvalidUser_Failure() throws Exception{
         List<CurrentState> ckStates = new ArrayList<>();
         CurrentState currentState01 = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE, regularUser);
         ckStates.add(currentState01);

         List<GameStats> ckStats = new ArrayList<>();
         GameStats stats01 = new GameStats(0, 0, 0, regularUser, currentState01);
         ckStats.add(stats01);

         currentState01.setGameStats(stats01);

         regularUser.setCurrentState(ckStates);
         regularUser.setGameStats(ckStats);

         stateRepo.save(currentState01);

         URI uri = new URI(baseUrl + port + "/api/id/" + regularUser.getId()+100 + "/states");
         ResponseEntity<CurrentState> result = restTemplate.getForEntity(uri, CurrentState.class);

         assertEquals(404, result.getStatusCode().value());
     }

    @Test
    public void getState_ValidUserAndId_Success() throws Exception {
        CurrentState currentState = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);
        List<CurrentState> currentStates = new ArrayList<>();

        currentState.setUser(regularUser);
        currentStates.add(currentState);

        regularUser.setCurrentState(currentStates);

        Long currentStateId = stateRepo.save(currentState).getId();

        URI uri = new URI(baseUrl + port + "/api/id/" + regularUser.getId() + "/states/" + currentStateId);

        ResponseEntity<CurrentState> result = restTemplate.getForEntity(uri, CurrentState.class);


        assertEquals(200, result.getStatusCode().value());
        assertEquals(currentState.getCurrentState(), result.getBody().getCurrentState());
        assertEquals(currentState.getYearValue(), result.getBody().getYearValue());
    }

    @Test
    public void getState_ValidUserInvalidId_Success() throws Exception {
        Long userId = regularUser.getId();

        URI uri = new URI(baseUrl + port + "/api/id/" + userId + "/states/" + 1);

        ResponseEntity<CurrentState> result = restTemplate.getForEntity(uri, CurrentState.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(null, result.getBody());
    }

    @Test
    public void getState_InvalidUserValidId_Success() throws Exception {
        CurrentState currentState = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);

        Long currentStateId = stateRepo.save(currentState).getId();

        URI uri = new URI(baseUrl + port + "/api/id/" + 100 + "/states/" + currentStateId);

        ResponseEntity<CurrentState> result = restTemplate.getForEntity(uri, CurrentState.class);

        assertEquals(404, result.getStatusCode().value());
    }

     @Test
     public void addState_ValidUser_Success() throws Exception {
         CurrentState currentState = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE, regularUser);
         List<CurrentState> currentStates = new ArrayList<>();
         currentStates.add(currentState);

         regularUser.setCurrentState(currentStates);

         URI uri = new URI(baseUrl + port + "/api/id/" +  regularUser.getId() + "/states");

         ResponseEntity<CurrentState> result = restTemplate.postForEntity(uri, currentState, CurrentState.class);

         assertEquals(201, result.getStatusCode().value());
         assertEquals(currentState.getCurrentState(), result.getBody().getCurrentState());
         assertEquals(currentState.getYearValue(), result.getBody().getYearValue());
     }

    @Test
    public void addState_InvalidUser_Failure() throws Exception {
        CurrentState currentState = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);

        List<CurrentState> currentStates = new ArrayList<>();
        currentState.setUser(regularUser);

        currentStates.add(currentState);
        regularUser.setCurrentState(currentStates);
        URI uri = new URI(baseUrl + port + "/api/id/" + (regularUser.getId() + 100) + "/states");
        System.out.println(regularUser.getId() + " --------------- " + uri);

        // do this
        ResponseEntity<CurrentState> result = restTemplate.postForEntity(uri, currentState, CurrentState.class);

        assertEquals(404, result.getStatusCode().value());
    }

     @Test
     public void deleteState_ValidId_Success() throws Exception{
         CurrentState state = stateRepo.save(new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE));
         URI uri = new URI(baseUrl + port + "/api/states/" + state.getId());

         ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);

         assertEquals(200, result.getStatusCode().value());
         Optional<CurrentState> emptyValue = Optional.empty();
         assertEquals(emptyValue, stateRepo.findById(state.getId()));
     }

    @Test
    public void deleteState_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/states/1");
        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateState_InvalidId_Failure() throws Exception{
        CurrentState currentState = stateRepo.save(new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE));
        List<CurrentState> currentStates = new ArrayList<>();
        currentState.setUser(regularUser);
        currentStates.add(currentState);

        regularUser.setGameStats(new ArrayList<GameStats>());
        regularUser.setCurrentState(currentStates);


        CurrentState newState = new CurrentState(10, State.completed);
        URI uri = new URI(baseUrl + port + "/api/states/" + (currentState.getId()+1) );

        ResponseEntity<CurrentState> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newState), CurrentState.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateState_ValidId_Success() throws Exception{
        CurrentState currentState = stateRepo.save(new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE));
        List<CurrentState> currentStates = new ArrayList<>();

        currentState.setUser(regularUser);
        currentStates.add(currentState);


        regularUser.setGameStats(new ArrayList<GameStats>());
        regularUser.setCurrentState(currentStates);

        CurrentState newState = new CurrentState(10, State.completed);
        URI uri = new URI(baseUrl + port + "/api/states/" + currentState.getId());

        ResponseEntity<CurrentState> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newState), CurrentState.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(newState.getYearValue(), result.getBody().getYearValue());
        assertEquals(newState.getCurrentState(), result.getBody().getCurrentState());
    }




//        GameStats gamestat = new GameStats();

//    CurrentState currentState = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);

//        currentState.setGameStats(gamestat);
//        gamestat.setCurrentState(currentState);

    //List<GameStats> gameStatsList = new ArrayList<>();
//    List<CurrentState> currentStates = new ArrayList<>();

    //gamestat.setUser(regularUser);
//        currentState.setUser(regularUser);

    //.add(gamestat);
//        currentStates.add(currentState);

    //regularUser.setGameStats(gameStatsList);
//        regularUser.setCurrentState(currentStates);

    //save user to database
//    Long userId = userRepo.save(regularUser).getId();
//    Long currentStateId = stateRepo.save(currentState).getId();
    //gameStatsRepo.save(gamestat);
    // stateRepo.save(currentState);

//
//    URI uri = new URI(baseUrl + port + "/api/id/" + userId + "/states/" + currentStateId);
//
//    ResponseEntity<CurrentState> result = restTemplate.getForEntity(uri, CurrentState.class);
//
//
//    assertEquals(200, result.getStatusCode().value());
//    assertEquals(currentState.getCurrentState(), result.getBody().getCurrentState());
//    assertEquals(currentState.getYearValue(), result.getBody().getYearValue());
}
