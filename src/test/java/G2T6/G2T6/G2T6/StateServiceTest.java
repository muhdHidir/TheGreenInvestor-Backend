package G2T6.G2T6.G2T6;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import G2T6.G2T6.G2T6.misc.CONSTANTVARIABLES;
import G2T6.G2T6.G2T6.misc.State;
import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.repository.StateRepository;
import G2T6.G2T6.G2T6.services.StateServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StateServiceTest {
    @Mock
    private StateRepository stateRepo;

    @InjectMocks
    private StateServiceImplementation stateServiceImplementation;


    @Test
    void updateState_NotFound_ReturnNull(){
        CurrentState state = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);
        Long id = 1L;
        when(stateRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        CurrentState updateState = stateServiceImplementation.updateCurrentState(id, state);

        assertNull(updateState);
        verify(stateRepo).findById(id);
    }

    @Test
    void updateState_Found_ReturnState(){
        Long id = 1L;
        CurrentState state = new CurrentState(id, CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);
        when(stateRepo.findById(any(Long.class))).thenReturn(Optional.of(state));
        when(stateRepo.save(any(CurrentState.class))).thenReturn(state);

        CurrentState updateState = stateServiceImplementation.updateCurrentState(id, state);

        assertNotNull(updateState);
        verify(stateRepo).findById(state.getId());
        verify(stateRepo).save(state);
    }

    @Test
    void getListCompletedState_NotFound_ReturnEmptyList(){
        CurrentState state01 = new CurrentState(1L, CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);
        CurrentState state02 = new CurrentState(2L, CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);
        CurrentState state03 = new CurrentState(3L, CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);

        stateServiceImplementation.addCurrentState(state01);
        stateServiceImplementation.addCurrentState(state02);
        stateServiceImplementation.addCurrentState(state03);

        List<CurrentState> currentCompleteState = stateServiceImplementation.listCompletedState();
        assertEquals(currentCompleteState, new ArrayList<CurrentState>());
    }

    @Test
    void getListCompletedState_Found_ReturnList(){
        CurrentState state01 = new CurrentState(1L, CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);
        CurrentState state02 = new CurrentState(2L, 10, State.completed);
        CurrentState state03 = new CurrentState(3L, 10, State.completed);

        List<CurrentState> currentStates = new ArrayList<>();
        currentStates.add(state01);
        currentStates.add(state02);
        currentStates.add(state03);

        when(stateRepo.findAll()).thenReturn(currentStates);

        List<CurrentState> currentCompleteState = stateServiceImplementation.listCompletedState();
        List<CurrentState> verifyArray = new ArrayList<>();
        verifyArray.add(state02);
        verifyArray.add(state03);

        assertEquals(currentCompleteState, verifyArray);
    }

    @Test
    void getUserResponse_Empty_ReturnNull(){
        CurrentState state01 = new CurrentState(1L, CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);

        when(stateRepo.findById(any(Long.class))).thenReturn(Optional.of(state01));

        CurrentState state = stateServiceImplementation.getCurrentState(1L);
        assertEquals(state.getUserAnswers(), null);
    }

    @Test
    void getUserResponse_Empty_ReturnListOfAnswer(){
        CurrentState state01 = new CurrentState(1L, CONSTANTVARIABLES.DEFAULTYEAR, CONSTANTVARIABLES.DEFAULTSTATE);
        state01.addNewUserResponse(1);
        state01.addNewUserResponse("open ended question etc etc");
        state01.addNewUserResponse(3);

        when(stateRepo.findById(any(Long.class))).thenReturn(Optional.of(state01));

        CurrentState state = stateServiceImplementation.getCurrentState(1L);
        System.out.println(state);
        List<String> verify = new ArrayList<>();
        verify.add(1+"");
        verify.add("open ended question etc etc");
        verify.add(3+"");

        assertEquals(state.getUserAnswers(), verify);
    }

}
