package ru.yandex.practicum.filmorate.model;

import java.util.Objects;


public record Friendship(Long userIdOne, Long userIdTwo) {

    public Friendship {
        if (userIdOne.equals(userIdTwo)) {
            throw new IllegalArgumentException("Cannot create a friendship with the same user ID");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friendship other)) {
            return false;
        }

        return (Objects.equals(userIdOne, other.userIdOne()) && Objects.equals(userIdTwo, other.userIdTwo())) ||
                (Objects.equals(userIdOne, other.userIdTwo()) && Objects.equals(userIdTwo, other.userIdOne()));
    }


    @Override
    public int hashCode() {
        return Objects.hash(
                Math.min(userIdOne, userIdTwo),
                Math.max(userIdOne, userIdTwo)
        );
    }
}

