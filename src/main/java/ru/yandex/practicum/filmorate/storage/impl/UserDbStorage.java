package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        long id = simpleJdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(user)).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        int row = jdbcTemplate.update("UPDATE USERS SET email=?, login=?, name=? ,birthday=? WHERE user_id=?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (row == 0) {
            throw new NotFoundException("Пользователь с id: " + user.getId() + " не был найден.");
        }
        return getUser(user.getId());
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", ((rs, rowNum) -> userMapper(rs)));
    }

    @Override
    public User getUser(Long idUser) {
        return jdbcTemplate.query("SELECT * FROM USERS WHERE user_id=?", ((rs, rowNum) -> userMapper(rs)), idUser).
                stream().
                findAny().
                orElseThrow(() -> new NotFoundException("Пользователь с id: " + idUser + "не был найден."));
    }

    @Override
    public Set<User> getListFriends(Long userId) {
        getUser(userId);
        return new HashSet<>(jdbcTemplate.
                query("SELECT * FROM USERS WHERE user_id IN(SELECT friend_id FROM USER_FRIEND WHERE user_id=?)",
                        ((rs, rowNum) -> userMapper(rs)), userId));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update("INSERT INTO USER_FRIEND (friend_id,user_id) VALUES (?,?)", friendId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM USER_FRIEND WHERE user_id=? AND friend_id=?", userId, friendId);
    }

    @Override
    public void deleteUser(Long userId) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId);
    }

    private User userMapper(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}
