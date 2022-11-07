package G2T6.G2T6.G2T6.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import G2T6.G2T6.G2T6.exceptions.OptionExistsException;
import G2T6.G2T6.G2T6.exceptions.OptionNotFoundException;
import G2T6.G2T6.G2T6.exceptions.QuestionNotFoundException;
import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.repository.OptionRepository;
import G2T6.G2T6.G2T6.repository.QuestionRepository;

@Service
public class OptionServiceImplementation implements OptionService {
    private OptionRepository options;
    private QuestionRepository questions;

    public OptionServiceImplementation(final OptionRepository options, final QuestionRepository questions) {
        this.options = options;
        this.questions = questions;
    }

    // get list of options for question ID
    public List<Option> listOptions(final long questionId) throws QuestionNotFoundException {
        // check if Question Exists
        if(!questions.existsById(questionId)) {
            throw new QuestionNotFoundException(questionId);
        }
        return options.findByQuestionId(questionId);
    }

    // add option to question specified by questionId
    public Option addOption(final long questionId, final Option option) throws QuestionNotFoundException, OptionExistsException {

         // find question by questionId
         return questions.findById(questionId).map(question -> {
            // check if option exists by question and option IDs
            if (options.findByIdAndQuestionId(option.getId(), questionId).isPresent()) {
                System.out.println("invalid Id Pair " + questionId + "|" + option.getId());
                throw new OptionExistsException(option.getOption());
            }
            // set question for option
            option.setQuestion(question);
            return options.save(option);
        }).orElseThrow(() -> new QuestionNotFoundException(questionId));
    }

    // update option to newOption
    public Option updateOption(final long questionId, final long optionId, final Option newOption)  throws QuestionNotFoundException, OptionNotFoundException {
        // check if question exists
        if(!questions.existsById(questionId)) {
            throw new QuestionNotFoundException(questionId);
        }

        // replace option (method defined in Option.java)
        return options.findByIdAndQuestionId(optionId, questionId).map(option -> {
            option.replaceOption(newOption);
            return options.save(option);
        }).orElseThrow(() -> new OptionNotFoundException(optionId));
    }

    // delete option indicated by questionId and optionId
    public void deleteOption(final long questionId, final long optionId) {
        if(!questions.existsById(questionId)) {
            throw new QuestionNotFoundException(questionId);
        }

        // delete option specified by questionId and optionId 
        options.findByIdAndQuestionId(optionId, questionId).map(option -> {
            options.delete(option);
            return void.class;
        }).orElseThrow(() -> new OptionNotFoundException(optionId));
    }
}
