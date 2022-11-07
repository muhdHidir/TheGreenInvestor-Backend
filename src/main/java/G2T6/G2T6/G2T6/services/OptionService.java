package G2T6.G2T6.G2T6.services;

import java.util.List;

import G2T6.G2T6.G2T6.models.Option;

public interface OptionService {
    List<Option> listOptions(final long questionId);
    Option addOption(final long questionId, final Option option);
    Option updateOption(final long questionId, final long optionId, final Option newOption);
    void deleteOption(final long questionId, final long optionId);
}
