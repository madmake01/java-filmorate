package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum SortDirectorFilms {
    LIKES("likes"),
    YEAR("year");

    private final String textView;

    SortDirectorFilms(String textView) {
        this.textView = textView;
    }

    public static SortDirectorFilms getSortByName(String sortName) {
        if (sortName == null || sortName.isEmpty())
            throw new IllegalStateException("Значение сортировки не долно быть пустым.");
        SortDirectorFilms sortDirectorFilms = null;

        try {
            sortDirectorFilms = SortDirectorFilms.valueOf(sortName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Сортировки '%s' не существует.", sortName));
        }

        return sortDirectorFilms;
    }
}
