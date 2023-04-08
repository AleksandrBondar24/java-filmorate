package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.util.enums.EventType;
import ru.yandex.practicum.filmorate.util.enums.Operation;


import java.util.List;

public interface FeedStorage {

    List<Feed> getFeed(Long id);

    void saveFeed(Long userId, EventType ev, Operation op, Long entityId);
}
