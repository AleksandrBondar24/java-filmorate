package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.util.enums.EventType;
import ru.yandex.practicum.filmorate.util.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getFeed(Long id) {
        return jdbcTemplate.query("SELECT * " +
                "FROM FEED " +
                "WHERE user_id = ? " +
                "ORDER BY time_stamp", ((rs, rowNum) -> mapperFeed(rs)), id);
    }

    private Feed mapperFeed(ResultSet rs) throws SQLException {
        Feed feed = new Feed();
        feed.setTimestamp(rs.getLong("time_stamp"));
        feed.setUserId(rs.getLong("user_id"));
        feed.setEventType(EventType.valueOf(rs.getString("event_type")));
        feed.setOperation(Operation.valueOf(rs.getString("operation")));
        feed.setEventId(rs.getLong("event_id"));
        feed.setEntityId(rs.getLong("entity_id"));
        return feed;
    }

    @Override
    public void saveFeed(Long userId, EventType ev, Operation op, Long entityId) {
        jdbcTemplate.update("INSERT INTO FEED (time_stamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?,?,?,?,?)", System.currentTimeMillis(), userId, String.valueOf(ev), String.valueOf(op), entityId);
    }
}

