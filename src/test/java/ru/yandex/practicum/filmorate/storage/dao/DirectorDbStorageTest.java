package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class DirectorDbStorageTest {
    private final DirectorDbStorage directorDbStorage;
    private Director director;

    @BeforeEach
    void setUp() {
        director = new Director();
        director.setName("Петр");
    }

    @Test
    void createDirector() {
        Director addDirector = directorDbStorage.createDirector(director);

        assertThat(addDirector.getId()).isNotNull();
        assertThat(addDirector.getName()).isEqualTo(director.getName());

    }

    @Test
    void updateDirector() {
        Director addDirector = directorDbStorage.createDirector(director);
        addDirector.setName("Николай");

        Director updatedDirector = directorDbStorage.updateDirector(addDirector);

        assertThat(updatedDirector.getName()).isEqualTo("Николай");
    }

    @Test
    void getDirectorById() {
        Director addDirector = directorDbStorage.createDirector(director);

        Optional<Director> retrievedDirector = directorDbStorage.getDirectorById(addDirector.getId());

        assertThat(retrievedDirector)
                .isPresent()
                .hasValueSatisfying(dir ->
                        assertThat(dir).isEqualTo(addDirector)
                );
    }

    @Test
    void getListDirectors() {
        directorDbStorage.createDirector(director);

        assertThat(directorDbStorage.getListDirectors()).hasSize(1);
    }

    @Test
    void removeDirector() {
        Director addDirector = directorDbStorage.createDirector(director);
        directorDbStorage.removeDirector(addDirector.getId());

        assertThat(directorDbStorage.getDirectorById(addDirector.getId())).isEmpty();
    }

}
