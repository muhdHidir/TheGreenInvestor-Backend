package G2T6.G2T6.G2T6.services.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import G2T6.G2T6.G2T6.exceptions.QuestionOrderIdInvalidException;
import G2T6.G2T6.G2T6.models.orders.QuestionOrder;
import G2T6.G2T6.G2T6.repository.QuestionOrderRepository;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuestionOrderServiceImplementation implements QuestionOrderService {
    private QuestionOrderRepository questionOrders;

    @Autowired
    public QuestionOrderServiceImplementation(QuestionOrderRepository questionOrders) {
        this.questionOrders = questionOrders;
    }

    @Override
    public QuestionOrder getQuestionOrder() {
        // pick a questionOrder from 1 to 10 (we store 10 permutations of question orders)
        long questionOrderIdx = ThreadLocalRandom.current().nextLong(10) + 1;

        // get questionOrder corresponding to questionOrderIdx
        QuestionOrder questionOrder = questionOrders.findById(questionOrderIdx).map(opOrder -> opOrder).orElse(null);

        // returns questionOrder if it is not null
        if (questionOrder == null) {
            throw new QuestionOrderIdInvalidException(questionOrderIdx);
        }

        return questionOrder;
    }
}
