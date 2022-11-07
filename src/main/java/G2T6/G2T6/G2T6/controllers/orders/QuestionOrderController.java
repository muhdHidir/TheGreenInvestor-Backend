package G2T6.G2T6.G2T6.controllers.orders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import G2T6.G2T6.G2T6.exceptions.QuestionOrderIdInvalidException;
import G2T6.G2T6.G2T6.models.orders.QuestionOrder;
import G2T6.G2T6.G2T6.repository.QuestionOrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class QuestionOrderController {
    private QuestionOrderRepository questionOrderRepository;

    @Autowired
    public QuestionOrderController(final QuestionOrderRepository questionOrderRepository){
        this.questionOrderRepository = questionOrderRepository;
    }

    @GetMapping("/questionOrder")
    public List<QuestionOrder> getQuestionOrder() {
        return questionOrderRepository.findAll();
    }

    // @ResponseStatus(HttpStatus.CREATED)
    // @PostMapping("/questionOrder")
    // public QuestionOrder addQuestionOrder(){

    //     // create list of 10 randomly ordered numbers
    //     ArrayList<Integer> indexArray = new ArrayList<>();
    //     Collections.addAll(indexArray, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10); // since ids are 1 - 10
    //     Collections.shuffle(indexArray);

    //     // check if order exists in questionOrderRepository
    //     // while (questionOrderRepository.findByIndexArray(indexArray) != null) {
    //     //     Collections.shuffle(indexArray);
    //     // }

    //     // create new questionOrder with order in indexArray (not alr in repo)
    //     QuestionOrder questionOrder = new QuestionOrder(indexArray);

    //     // add to questionOrderRepository
    //     questionOrderRepository.save(questionOrder);

    //     return questionOrder;
    // }
}
