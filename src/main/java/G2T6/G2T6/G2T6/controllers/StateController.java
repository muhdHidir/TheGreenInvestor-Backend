package G2T6.G2T6.G2T6.controllers;

import G2T6.G2T6.G2T6.exceptions.UserNotFoundException;
import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.repository.UserRepository;
import G2T6.G2T6.G2T6.services.StateService;
import G2T6.G2T6.G2T6.exceptions.StateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class StateController {
    private UserRepository userRepository;
    private StateService stateService;

    @Autowired
    public StateController(StateService ss, UserRepository userRepository){
        this.userRepository = userRepository;
        this.stateService = ss;
    }

    /**
     * get all states
     * @return all states in the database
     */
    @GetMapping("/states")
    public List<CurrentState> getAllState(){
        return stateService.listCurrentState();
    }


    /**
     * get selected user states
     * @param userId a long value
     * @return all stats of selected user
     */
    @GetMapping("/id/{userId}/states")
    public List<CurrentState> getUserState(@PathVariable (value = "userId") Long userId){
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return stateService.listCurrentStateByUserId(userId);
    }

    /**
     * get selected user and selected state id states
     * @param userId a long value
     * @param stateId a long value
     * @return selected user and selected state id states
     */
    @GetMapping("/id/{userId}/states/{id}")
    public Optional<CurrentState> getSelectedUserState(@PathVariable (value = "userId") Long userId, @PathVariable (value = "id") Long stateId){
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return stateService.getStateByIdAndUserId(stateId, userId);
    }

    /**
     * add a state to a selected user
     * @param userId a Long value
     * @param state a CurrentState object
     * @return the newly added state
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/id/{userId}/states")
    public CurrentState addState(@PathVariable (value = "userId") Long userId, @Valid @RequestBody CurrentState state){
        return userRepository.findById(userId).map(user->{
            state.setUser(user);
            return stateService.addCurrentState(state);
        }).orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * update selected state id's state
     * @param id a Long value
     * @param state a CurrentState object
     * @return updated state
     */
    @PutMapping("/states/{id}")
    public CurrentState updateState(@PathVariable Long id, @Valid @RequestBody CurrentState state){
        CurrentState currentState = stateService.updateCurrentState(id, state);
        if(currentState == null)  throw new StateNotFoundException(id);
        return currentState;
    }

    /**
     * delete the selected state
     * @param id a Long value
     */
    @DeleteMapping("/states/{id}")
    public void deleteState(@PathVariable Long id){
        try{
            stateService.deleteCurrentState(id);
        }catch (EmptyResultDataAccessException e){
            throw new StateNotFoundException(id);
        }
    }

}
