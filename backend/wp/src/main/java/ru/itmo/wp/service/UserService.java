package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Message;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.RegisterForm;
import ru.itmo.wp.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByLoginAndPassword(String login, String password) {
        return login == null || password == null ? null : userRepository.findByLoginAndPassword(login, password);
    }

    public User findById(Long id) {
        return id == null ? null : userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByIdDesc();
    }

    public void writeMessage(User user, Message message) {
        user.addMessage(message);
        userRepository.save(user);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User save(RegisterForm registerForm) {
        User user = new User();
        user.setLogin(registerForm.getLogin());
        user.setName(registerForm.getName());
        user.setAdmin(false);
        // for some reason this '.save' generated two 'insert' queries and led to an exception on db-level
        // after I wrapped this section in try-catch - everything worked
        // Then I removed try-catch and from that moment everything was fine
        // might have been some issues with hot-swap (e.g. didn't see if hot-swap was successful)
        User savedUser = userRepository.save(user);
        userRepository.updatePasswordSha(savedUser.getId(), savedUser.getLogin(), registerForm.getPassword());
        return savedUser;
    }

    public boolean isLoginVacant(String login) {
        return userRepository.countByLogin(login) == 0;
    }
}
