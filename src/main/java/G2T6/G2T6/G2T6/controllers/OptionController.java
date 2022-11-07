package G2T6.G2T6.G2T6.controllers;

import G2T6.G2T6.G2T6.exceptions.OptionExistsException;
import G2T6.G2T6.G2T6.exceptions.OptionNotFoundException;
import G2T6.G2T6.G2T6.exceptions.OptionOrderIdInvalidException;
import G2T6.G2T6.G2T6.exceptions.QuestionNotFoundException;
import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.repository.OptionRepository;
import G2T6.G2T6.G2T6.repository.QuestionRepository;
import G2T6.G2T6.G2T6.services.OptionService;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/questions")
@RestController
public class OptionController {
    private OptionService optionService;

    @Autowired
    public OptionController(OptionService optionService){
        this.optionService = optionService;
    }

    // Returns all Options specified by Question Id
    @GetMapping("/{questionId}/options")
    public List<Option> getAllOptionsByQuestionId(@PathVariable (value = "questionId") final Long questionId) 
            throws QuestionNotFoundException {

        return optionService.listOptions(questionId);
    }

    // Add an Option to specified Question
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{questionId}/options")
    public Option addOption(@PathVariable (value = "questionId") final Long questionId, 
            @Valid @RequestBody Option option) throws QuestionNotFoundException, OptionExistsException {

       return optionService.addOption(questionId, option);
    }

    // Update an Existing Option
    @PutMapping("/{questionId}/options/{optionId}")
    public Option updateOption(@PathVariable (value = "questionId") final Long questionId, 
            @PathVariable (value = "optionId") Long optionId,
            @Valid @RequestBody Option newOption) {
        
        return optionService.updateOption(questionId, optionId, newOption);
    }

    //Delete an Existing Option
    @DeleteMapping("/{questionId}/options/{optionId}")
    public void deleteOption(@PathVariable (value = "questionId") final Long questionId,
            @PathVariable (value = "optionId") Long optionId) throws EmptyResultDataAccessException {
        
        // attempt delete, throw OptionNotFoundException if delete fails
        try{
            optionService.deleteOption(questionId, optionId);
        } catch(EmptyResultDataAccessException e) {
            throw new OptionNotFoundException(optionId);
        }

    }

    
}
