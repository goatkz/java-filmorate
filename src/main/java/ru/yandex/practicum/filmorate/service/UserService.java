package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.update(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id " + id + " не найден"
                ));
    }

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException(
                    "Нельзя добавить самого себя в друзья"
            );
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getFriends(int userId) {
        User user = getUserById(userId);

        Set<Integer> friendIds = user.getFriends();

        return friendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);

        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toCollection(HashSet::new));
    }
}