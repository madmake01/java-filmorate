package ru.yandex.practicum.filmorate.model;

public record Friendship(Long userIdOne, Long userIdTwo) {
    public Friendship {
        if (userIdOne.equals(userIdTwo)) {
            throw new IllegalArgumentException("Cannot create a friendship with the same user ID");
        }
    }
}

