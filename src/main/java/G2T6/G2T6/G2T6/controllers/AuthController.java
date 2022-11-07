package G2T6.G2T6.G2T6.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import G2T6.G2T6.G2T6.exceptions.TokenRefreshException;
import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.models.security.RefreshToken;
import G2T6.G2T6.G2T6.models.security.User;
import G2T6.G2T6.G2T6.payload.request.LoginRequest;
import G2T6.G2T6.G2T6.payload.request.SignupRequest;
import G2T6.G2T6.G2T6.payload.request.TokenRefreshRequest;
import G2T6.G2T6.G2T6.payload.response.JwtResponse;
import G2T6.G2T6.G2T6.payload.response.MessageResponse;
import G2T6.G2T6.G2T6.payload.response.TokenRefreshResponse;
import G2T6.G2T6.G2T6.repository.UserRepository;
import G2T6.G2T6.G2T6.security.jwt.JwtUtils;
import G2T6.G2T6.G2T6.security.services.RefreshTokenService;
import G2T6.G2T6.G2T6.security.services.UserDetailsImpl;
import G2T6.G2T6.G2T6.services.StateServiceImplementation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  StateServiceImplementation stateServiceImplementation;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    try {

      Authentication authentication = authenticationManager
          .authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

      String jwt = jwtUtils.generateJwtToken(userDetails);

      List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
          .collect(Collectors.toList());

      RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

      return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
          userDetails.getUsername(), roles, "Logged In Successfully"));

    } catch (BadCredentialsException e) {

      JwtResponse responseBody = new JwtResponse();
      responseBody.setMessage("Error: Invalid username or password!");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);

    } catch (Exception e) {

      JwtResponse responseBody = new JwtResponse();
      responseBody.setMessage("Some other error at log in");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);

    }

  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()), false);

    String strRole = signUpRequest.getRole();

    if (strRole.equals("GUEST")) {

      user.setRole("ROLE_GUEST");

    } else if (strRole.equals("USER")) {

      user.setRole("ROLE_USER");

    } else {

      // user.setRole("ROLE_INVALID");
      return ResponseEntity.status(401).body(new MessageResponse("INVALID ROLE GIVEN"));

    }

    List<CurrentState> currentStates = new ArrayList<CurrentState>();
    CurrentState defaultState = stateServiceImplementation.getDefaultState();
    currentStates.add(defaultState);

    user.setCurrentState(currentStates);

    // user.setSubscribedEmail(false);

    defaultState.setUser(user);

    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        })
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
            "Refresh token is not in database!"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();
    refreshTokenService.deleteByUserId(userId);
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }

}