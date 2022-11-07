package G2T6.G2T6.G2T6.models.orders;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
public abstract class Orders {
    // id is the primary key of question order in questionOrderRepository
    private @Id @GeneratedValue (strategy = GenerationType.TABLE) Long id;

    // indexArray stores array of indexes
    private ArrayList<Integer> indexArray;

    // generates an array containing 0 to (size - 1)
    public ArrayList<Integer> generateArray(int size) {
        // generate arraylist
        ArrayList<Integer> generatedArray = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            generatedArray.add(i);
        }

        return generatedArray;
    }

    // defined in children classes how to manipulate array
    public abstract ArrayList<Integer> manipulateArray(ArrayList<Integer> array);
}
