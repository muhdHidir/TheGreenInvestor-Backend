package G2T6.G2T6.G2T6.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import G2T6.G2T6.G2T6.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserRepository userRepository;

    public List<String> getEmailSubscribers() {
        try {

            List<String> subscribedUserEmailList = userRepository.findAll().stream()
                    .filter(user -> user.isSubscribedEmail()).map(user -> user.getEmail()).collect(Collectors.toList());

            return subscribedUserEmailList;

        } catch (Exception e) {

            return null;

        }
    }
}
