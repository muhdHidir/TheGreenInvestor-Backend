package G2T6.G2T6.G2T6.services;


import G2T6.G2T6.G2T6.misc.CONSTANTVARIABLES;
import G2T6.G2T6.G2T6.misc.State;
import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.repository.StateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StateServiceImplementation implements StateService {
    private StateRepository stateRepository;

    @Autowired
    public StateServiceImplementation(final StateRepository rs){
        this.stateRepository = rs;
    }

    /**
     * get all state
     * @return get all state
     */
    @Override
    public List<CurrentState> listCurrentState() {
        return stateRepository.findAll();
    }

    /**
     * get selected state
     * @param id a Long value
     * @return selected states
     */
    @Override
    public CurrentState getCurrentState(final Long id) {
        return stateRepository.findById(id).orElse(null);
    }

    /**
     * add new state
     * @param state A CurrentState object
     * @return the newly added state
     */
    @Override
    public CurrentState addCurrentState(final CurrentState state) {
        return stateRepository.save(state);
    }

    /**
     * update current state value
     * @param id a Long value
     * @param state A CurrentState object
     * @return updated currentState
     */
    @Override
    public CurrentState updateCurrentState(final Long id, final CurrentState state) {
        return stateRepository.findById(id).map(newState -> {
                newState.changeState(state.getCurrentState());
                newState.setYearValue(state.getYearValue());
                newState.setUserResponse(state.getUserResponse());
            return stateRepository.save(newState );
        }).orElse(null);
    }

    /**
     * delete selected state
     * @param id a Long value
     */
    @Override
    public void deleteCurrentState(final Long id) { stateRepository.deleteById(id); }

    /**
     * Reset all state value
     * @param id a Long value
     */
    @Override
    public void factoryReset(final Long id) {
        stateRepository.findById(id).map(newState -> {
            newState.changeState(CONSTANTVARIABLES.DEFAULTSTATE);
            newState.setYearValue(CONSTANTVARIABLES.DEFAULTYEAR);
            return stateRepository.save(newState);
        });
    }

    /**
     * Get a default state
     * @return a default valued state
     */
    @Override
    public CurrentState getDefaultState() {
        //init default state
        CurrentState newState = new CurrentState();
        newState.changeState(CONSTANTVARIABLES.DEFAULTSTATE);
        newState.setYearValue(CONSTANTVARIABLES.DEFAULTYEAR);
        //set default game ID
        newState.setGameId(CONSTANTVARIABLES.DEFAULTGAMEID);
        return newState;
    }

    /**
     * find all state belong to selected user
     * @param userid a Long value
     * @return all state belonging to selectec user
     */
    @Override
    public List<CurrentState> listCurrentStateByUserId(final Long userid) {
        return stateRepository.findByUserId(userid);
    }

    /**
     * get selected user and selected state id's state
     * @param id
     * @param userId
     * @return state that belong to this user and have this id
     */
    @Override
    public Optional<CurrentState> getStateByIdAndUserId(final Long id, final Long userId) {
        return stateRepository.findByIdAndUserId(id, userId);
    }

    /**
     * get all state that have the the complete state
     * @return all completed states
     */
    @Override
    public List<CurrentState> listCompletedState() {
        List<CurrentState> allStats = listCurrentState();
        List<CurrentState> completedState = new ArrayList<>();
        for(CurrentState c: allStats){
            if(c.getCurrentState() == State.completed){
                completedState.add(c);
            }
        }
        return completedState;
    }

}
