package G2T6.G2T6.G2T6.controllers;

import G2T6.G2T6.G2T6.exceptions.GameStatsNotFoundException;
import G2T6.G2T6.G2T6.exceptions.NotEnoughGameStatsException;
import G2T6.G2T6.G2T6.exceptions.UserNotFoundException;
import G2T6.G2T6.G2T6.misc.State;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.repository.GameStatsRepository;
import G2T6.G2T6.G2T6.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class GameStatsController {
    private GameStatsRepository gameStateRepo;
    private UserRepository userRepo;

    @Autowired
    public GameStatsController(final GameStatsRepository gameStateRepo, final UserRepository userRepo){
        this.gameStateRepo = gameStateRepo;
        this.userRepo = userRepo;
    }

    /**
     * List all game stats in the system
     * @return list of all game stats
     */
    @GetMapping("/gameStats")
    public List<GameStats> getAllGameStats(){
        return gameStateRepo.findAll();
    }

    @GetMapping("/gameStats/allUnique")
    public List<GameStats> getAllUniqueGameStats(){
        List<GameStats> completedStats = filterAllCompletedGameStats(getAllGameStats());
        Collections.sort(completedStats);
        List<GameStats> completedStatsWithOutDuplicate = filterUniqueGameStats(completedStats);
        return removeZeroOrLowerScoreGameStats(completedStatsWithOutDuplicate);
    }

    /**
     * List all top n game stats in the system
     * First it filter through all game stats and only select those that have completed state
     * Next we Sort them and return count number of stats in descending order
     * Remove duplicate user's stats only keep the highest
     * @param count a long value
     * @return return count number of people in term of total game stats score
     */
    @GetMapping("/gameStats/{count}")
    public List<GameStats> getAllTopNGameStats(@PathVariable (value = "count") final int count){
        List<GameStats> completedStats = filterAllCompletedGameStats(getAllGameStats());
        enoughCount(count, completedStats.size());

        Collections.sort(completedStats);

        List<GameStats> completedStatsWithOutDuplicate = filterUniqueGameStats(completedStats);

        enoughCount(count, completedStatsWithOutDuplicate.size());

        return  completedStatsWithOutDuplicate.subList(0, count);
    }

    /**
     * check if theres enough count
     * @param expectedCount a integer value
     * @param count a integer value
     */
    public void enoughCount(final int expectedCount,final int count){
        if(expectedCount > count) throw new NotEnoughGameStatsException(count);
    }

    /**
     * filter to get all completed game stats
     * @param gameStats a list of GameStats object
     * @return  all completed game stats
     */
    public List<GameStats> filterAllCompletedGameStats(final List<GameStats> gameStats){
        List<GameStats> completedStats = new ArrayList<>();
        for(GameStats gs: gameStats){
            if(gs.getCurrentState() != null && gs.getCurrentState().getCurrentState() == State.completed){
                completedStats.add(gs);
            }
        }
        return  completedStats;
    }

    /**
     * Only keep one game stats from each user
     * @param gameStats a list of GameStats object
     * @return one game stats from each user
     */
    public List<GameStats> filterUniqueGameStats(final List<GameStats> gameStats){
        List<GameStats> completedStatsWithOutDuplicate = new ArrayList<>();List<Long> userIds = new ArrayList<>();
        for(int i = 0; i < gameStats.size(); i++){
            Long userId = gameStats.get(i).getUser().getId();
            if(!userIds.contains(userId)){
                completedStatsWithOutDuplicate.add(gameStats.get(i));
                userIds.add(userId);
            }
        }
        return  completedStatsWithOutDuplicate;
    }

    /**
     * remove all the zero and negataive score game stats
     * @param gameStats a list of GameStats object
     * @return all gameStats that does not have > than zero
     */

    public List<GameStats> removeZeroOrLowerScoreGameStats(List<GameStats> gameStats){
        List<GameStats> nonZeroTotal = new ArrayList<>();
        for(GameStats gs: gameStats){
            if(gs.getTotal() > 0){
                nonZeroTotal.add(gs);
            }
        }
        return  nonZeroTotal;
    }



    /**
     * searching for all selected user's game stats
     * @param userId a long value
     * @return return all selected user's game stats
     **/
    @GetMapping("/id/{userId}/gameStats")
    public List<GameStats> getAllSelectedUserGameStats(@PathVariable (value = "userId") final Long userId){
        if(!userRepo.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return gameStateRepo.findByUserId(userId);
    }

    /**
     * Get game states of selected user and selected game stats id
     * @param userId a long value
     * @param id a long value
     * @return game states of selected user and selected game stats id
     */
    @GetMapping("/id/{userId}/gameStats/{id}")
    public Optional<GameStats> getGameStats(@PathVariable (value = "userId") final Long userId,
                                            @PathVariable (value = "id") final Long id){
        if(!userRepo.existsById(userId)){
            throw new UserNotFoundException(userId);
        }
        return gameStateRepo.findByIdAndUserId(id, userId);
    }

    /**
     * add a new game stats to selected user
     * @param userId a long value
     * @param gameStats a GameStats object
     * @return the new game stats added to selected user
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/id/{userId}/gameStats")
    public GameStats addGameStats(@PathVariable (value = "userId") final Long userId, @Valid @RequestBody final GameStats gameStats){
        return userRepo.findById(userId).map(user ->{
            gameStats.setUser(user);
            return gameStateRepo.save(gameStats);
        }).orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * update selected users game stats with selected id
     * @param id a long value
     * @param newStats a GameStats object
     * @return the updated game stats
     */
    @PutMapping("/gameStats/{id}")
    public GameStats updateGameStats(
            @PathVariable (value = "id") final Long id,
            @Valid @RequestBody final GameStats newStats){
        return gameStateRepo.findById(id).map(gameStats ->{
            gameStats.setChangeInIncomeVal(newStats.getChangeInIncomeVal());
            gameStats.setChangeInSustainabilityVal(newStats.getChangeInSustainabilityVal());
            gameStats.setChangeInMoraleVal(newStats.getChangeInMoraleVal());
            gameStats.setChangeInCashVal(newStats.getChangeInCashVal());

            gameStats.setCurrentIncomeVal(newStats.getCurrentIncomeVal());
            gameStats.setCurrentSustainabilityVal(newStats.getCurrentSustainabilityVal());
            gameStats.setCurrentMoraleVal(newStats.getCurrentMoraleVal());
            gameStats.setCurrentCashInHand(newStats.getCurrentCashInHand());

            gameStats.setMultiplier(newStats.getMultiplier());
            gameStats.setTotalScore(newStats.getTotalScore());
            return gameStateRepo.save(gameStats);
        }).orElseThrow(() -> new GameStatsNotFoundException(id));
    }

    /**
     * delete selected game stat
     * @param id a long value
     * @return ResponseEntity of the operation
     */
    @DeleteMapping("/gameStats/{id}")
    public void deleteGameStates(@PathVariable (value = "id") final Long id){
        try{
            gameStateRepo.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new GameStatsNotFoundException(id);
        }
    }

}
