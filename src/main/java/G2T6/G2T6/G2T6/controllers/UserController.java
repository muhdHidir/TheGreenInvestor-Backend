package G2T6.G2T6.G2T6.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import G2T6.G2T6.G2T6.payload.response.MessageResponse;
import G2T6.G2T6.G2T6.payload.response.ProfileResponse;
import G2T6.G2T6.G2T6.repository.UserRepository;
import G2T6.G2T6.G2T6.exceptions.UserNotFoundException;
import G2T6.G2T6.G2T6.models.security.User;
import G2T6.G2T6.G2T6.security.AuthHelper;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    // POST: create user
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        userRepository.save(user);
        // return status 201 with message
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered successfully!"));
    }

    // GET: get all users
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET: get user by username
    @GetMapping("/username")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserByUsername(@RequestBody String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    // GET: get user by email
    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserByEmail(@RequestBody String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    // GET: get user by id
    @GetMapping("/id")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@RequestBody Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    // PUT: update user
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }

    // Get User profile image index
    @GetMapping("/profileImageIndex")
    public ResponseEntity<?> getProfileImageIndex() {
        UserDetails userDetails = AuthHelper.getUserDetails();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));
        return new ResponseEntity<>(user.getProfileImageIndex(), HttpStatus.OK);
    }

    // Set User profile image index
    @PutMapping("/profileImageIndex/{id}")
    public ResponseEntity<?> setProfileImageIndex(@PathVariable(value = "id") int id) {
        UserDetails userDetails = AuthHelper.getUserDetails();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));
        user.setProfileImageIndex(id);
        userRepository.save(user);
        return new ResponseEntity<>(user.getProfileImageIndex(), HttpStatus.OK);
    }

    // get user high score
    @GetMapping("/highScore")
    public ResponseEntity<?> getHighScore() {
        UserDetails userDetails = AuthHelper.getUserDetails();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));
        return new ResponseEntity<>(user.getHighScore(), HttpStatus.OK);
    }

    // get user games played
    @GetMapping("/gamesPlayed")
    public ResponseEntity<?> getGamesPlayed() {
        UserDetails userDetails = AuthHelper.getUserDetails();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));
        return new ResponseEntity<>(user.getGamesPlayed(), HttpStatus.OK);
    }

    // get profileresponse
    @GetMapping("/getProfile")
    public ResponseEntity<?> getProfile() {
        UserDetails userDetails = AuthHelper.getUserDetails();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));

        // return profileresponse dto
        return new ResponseEntity<>(
                new ProfileResponse(user.getHighScore(), user.getGamesPlayed(), user.getProfileImageIndex()),
                HttpStatus.OK);
    }

    // Get all users subscribed to email notifications
    @GetMapping("/subscribedlist")
    public ResponseEntity<?> getSubscribedUserEmailList() {

        try {

            List<String> subscribedUserEmailList = userRepository.findAll().stream()
                    .filter(user -> user.isSubscribedEmail()).map(user -> user.getEmail()).collect(Collectors.toList());

            return new ResponseEntity<>(subscribedUserEmailList, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    // change user subscription status
    @PutMapping("/subscribe")
    public ResponseEntity<?> changeUserSubscriptionStatus() {

        try {

            User currUser = userRepository.findByUsername(AuthHelper.getUserDetails().getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (currUser == null) {
                return new ResponseEntity<>(new MessageResponse("Error: User not found"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            currUser.setSubscribedEmail(!currUser.isSubscribedEmail());

            userRepository.save(currUser);

            return new ResponseEntity<>(new MessageResponse("User subscription status changed successfully!"),
                    HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(new MessageResponse("Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}