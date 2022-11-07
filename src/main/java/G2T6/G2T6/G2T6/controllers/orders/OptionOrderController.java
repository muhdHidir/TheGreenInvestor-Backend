package G2T6.G2T6.G2T6.controllers.orders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import G2T6.G2T6.G2T6.exceptions.OptionOrderIdInvalidException;
import G2T6.G2T6.G2T6.models.orders.OptionOrder;
import G2T6.G2T6.G2T6.repository.OptionOrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class OptionOrderController {
    private OptionOrderRepository optionOrderRepository;

    @Autowired
    public OptionOrderController(final OptionOrderRepository optionOrderRepository){
        this.optionOrderRepository = optionOrderRepository;
    }

    @GetMapping("/optionOrder")
    public List<OptionOrder> getOptionOrder() {
        return optionOrderRepository.findAll();
    }

    // @ResponseStatus(HttpStatus.CREATED)
    // @PostMapping("/optionOrder")
    // public OptionOrder addOptionOrder(){

    //     // create list of 6 randomly ordered numbers
    //     ArrayList<Integer> indexArray = new ArrayList<>();
    //     Collections.addAll(indexArray, 1, 2, 3, 4, 5, 6);
    //     Collections.shuffle(indexArray);

    //     // remove last 2 elements of ArrayList
    //     indexArray.remove(5);
    //     indexArray.remove(4);


    //     // create new optionOrder with order in indexArray (not alr in repo)
    //     OptionOrder optionOrder = new OptionOrder();

    //     // add to optionOrderRepository
    //     optionOrderRepository.save(optionOrder);

    //     return optionOrder;
    // }
}
