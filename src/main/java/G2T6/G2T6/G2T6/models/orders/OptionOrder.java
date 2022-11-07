package G2T6.G2T6.G2T6.models.orders;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
public class OptionOrder {
    // id is the primary key of option order in optionOrderRepository
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;

    // verify indexArray is 4
    @Size(min = 4, max = 4)
    private ArrayList<Integer> indexArray;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionOrder questionOrder;
    
    public OptionOrder() {
        // generate arraylist
        ArrayList<Integer> generatedArray = new ArrayList<>();
        Collections.addAll(generatedArray, 0, 1, 2, 3, 4, 5); 
        
        // set indexArray as manipulate array
        this.indexArray = manipulateArray(generatedArray);
    }
    
    public ArrayList<Integer> manipulateArray(ArrayList<Integer> array){
        // store input array as manipulatedArray
        ArrayList<Integer> manipulatedArray = array;

        // shuffle their orders
        Collections.shuffle(manipulatedArray);

        // remove the first two elements in shuffled array 
        manipulatedArray.remove(0);
        manipulatedArray.remove(0);

        // resize the indexArray size to 4
        manipulatedArray.trimToSize();

        return manipulatedArray;
    }
}
