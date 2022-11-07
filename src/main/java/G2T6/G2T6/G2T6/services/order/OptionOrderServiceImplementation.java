package G2T6.G2T6.G2T6.services.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import G2T6.G2T6.G2T6.exceptions.OptionOrderIdInvalidException;
import G2T6.G2T6.G2T6.models.orders.OptionOrder;
import G2T6.G2T6.G2T6.repository.OptionOrderRepository;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class OptionOrderServiceImplementation implements OptionOrderService {
    private OptionOrderRepository optionOrders;

    @Autowired
    public OptionOrderServiceImplementation(OptionOrderRepository optionOrders) {
        this.optionOrders = optionOrders;
    }

    @Override
    public OptionOrder getOptionOrder() {
        // pick a optionOrder from 1 to 10 (we store 10 permutations of option orders)
        long optionOrderIdx = ThreadLocalRandom.current().nextLong(10) + 1;

        // get optionOrder corresponding to questionOrderIdx
        OptionOrder optionOrder = optionOrders.findById(optionOrderIdx).map(qOrder -> qOrder).orElse(null);

        // returns questionOrder if it is not null
        if (optionOrder == null) {
            throw new OptionOrderIdInvalidException(optionOrderIdx);
        }

        return optionOrder;
    }
}