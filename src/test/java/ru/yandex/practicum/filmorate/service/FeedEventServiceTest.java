package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feedevent.EventType;
import ru.yandex.practicum.filmorate.model.feedevent.FeedEvent;
import ru.yandex.practicum.filmorate.model.feedevent.Operation;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FeedEventServiceTest {
    private final FeedEventService eventService;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final FilmService filmService;
    private final LikeService likeService;
    private final ReviewService reviewService;

    private User makeUser(String name) {
        User user = new User();
        user.setEmail("same@email.com");
        user.setLogin("login");
        user.setName(name);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    private Film makeFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        Rating rating = new Rating();
        rating.setId(1L);
        film.setRating(rating);
        return film;
    }

    private Review makeReview(String content, boolean positive, Long userId, Long filmId) {
        return new Review(null, content, positive, userId, filmId, 0);
    }

    @Test
    void shouldAddFriendshipEventToFeed() {
        User user1 = userService.save(makeUser("name1"));
        User user2 = userService.save(makeUser("name2"));

        friendshipService.addFriend(user1.getId(), user2.getId());

        List<FeedEvent> events = eventService.getUserFeedEvents(user1.getId());
        assertFalse(events.isEmpty(), "Feed should not be empty after adding a friend");
        boolean found = events.stream().anyMatch(event ->
                event.getEventType() == EventType.FRIEND &&
                        event.getOperation() == Operation.ADD &&
                        Objects.equals(event.getUserId(), user1.getId()) &&
                        Objects.equals(event.getEntityId(), user2.getId())
        );
        assertTrue(found, "Expected FRIEND ADD event not found in user feed");
    }

    @Test
    void shouldRecordFriendshipRemovalInFeed() {
        User user1 = userService.save(makeUser("name1"));
        User user2 = userService.save(makeUser("name2"));
        friendshipService.addFriend(user1.getId(), user2.getId());
        List<FeedEvent> eventsBefore = eventService.getUserFeedEvents(user1.getId());

        friendshipService.removeFriend(user1.getId(), user2.getId());

        List<FeedEvent> eventsAfter = eventService.getUserFeedEvents(user1.getId());
        assertEquals(eventsBefore.size() + 1, eventsAfter.size(), "Feed size should increase by 1 after removing friend");
        boolean removeFound = eventsAfter.stream().anyMatch(event ->
                event.getEventType() == EventType.FRIEND &&
                        event.getOperation() == Operation.REMOVE &&
                        Objects.equals(event.getUserId(), user1.getId()) &&
                        Objects.equals(event.getEntityId(), user2.getId())
        );
        assertTrue(removeFound, "Expected FRIEND REMOVE event not found in user feed");
    }

    @Test
    void shouldAddLikeEventToFeed() {
        User user = userService.save(makeUser("name1"));
        Film film = filmService.save(makeFilm("Test Film"));

        likeService.addLike(user.getId(), film.getId());

        List<FeedEvent> events = eventService.getUserFeedEvents(user.getId());
        assertFalse(events.isEmpty(), "Feed should not be empty after liking a film");
        boolean likeFound = events.stream().anyMatch(event ->
                event.getEventType() == EventType.LIKE &&
                        event.getOperation() == Operation.ADD &&
                        Objects.equals(event.getUserId(), user.getId()) &&
                        Objects.equals(event.getEntityId(), film.getId())
        );
        assertTrue(likeFound, "Expected LIKE ADD event not found in user feed");
    }

    @Test
    void shouldRecordLikeRemovalInFeed() {
        User user = userService.save(makeUser("name1"));
        Film film = filmService.save(makeFilm("Test Film1"));
        likeService.addLike(user.getId(), film.getId());
        List<FeedEvent> eventsBefore = eventService.getUserFeedEvents(user.getId());

        likeService.removeLike(user.getId(), film.getId());

        List<FeedEvent> eventsAfter = eventService.getUserFeedEvents(user.getId());
        assertEquals(eventsBefore.size() + 1, eventsAfter.size(), "Feed size should increase by 1 after removing like");
        boolean removeFound = eventsAfter.stream().anyMatch(event ->
                event.getEventType() == EventType.LIKE &&
                        event.getOperation() == Operation.REMOVE &&
                        Objects.equals(event.getUserId(), user.getId()) &&
                        Objects.equals(event.getEntityId(), film.getId())
        );
        assertTrue(removeFound, "Expected LIKE REMOVE event not found in user feed");
    }

    @Test
    void shouldAddReviewEventToFeed() {
        User user = userService.save(makeUser("user"));
        Film film = filmService.save(makeFilm("film"));
        Review review = makeReview("Great movie!", true, user.getId(), film.getId());

        Review savedReview = reviewService.addReview(review);

        List<FeedEvent> events = eventService.getUserFeedEvents(user.getId());
        assertFalse(events.isEmpty(), "Feed should not be empty after adding a review");
        boolean found = events.stream().anyMatch(event ->
                event.getEventType() == EventType.REVIEW &&
                        event.getOperation() == Operation.ADD &&
                        Objects.equals(event.getUserId(), user.getId()) &&
                        Objects.equals(event.getEntityId(), savedReview.getReviewId())
        );
        assertTrue(found, "Expected REVIEW ADD event not found in user feed");
    }

    @Test
    void shouldUpdateReviewEventInFeed() {
        User user = userService.save(makeUser("user"));
        Film film = filmService.save(makeFilm("film"));
        Review review = makeReview("Nice", true, user.getId(), film.getId());
        Review saved = reviewService.addReview(review);

        saved.setContent("Updated content");
        Review updated = reviewService.updateReview(saved);

        List<FeedEvent> events = eventService.getUserFeedEvents(user.getId());
        boolean found = events.stream().anyMatch(event ->
                event.getEventType() == EventType.REVIEW &&
                        event.getOperation() == Operation.UPDATE &&
                        Objects.equals(event.getUserId(), user.getId()) &&
                        Objects.equals(event.getEntityId(), updated.getReviewId())
        );
        assertTrue(found, "Expected REVIEW UPDATE event not found in user feed");
    }

    @Test
    void shouldRemoveReviewEventInFeed() {
        User user = userService.save(makeUser("user"));
        Film film = filmService.save(makeFilm("film"));
        Review review = makeReview("Nice", true, user.getId(), film.getId());
        Review saved = reviewService.addReview(review);
        List<FeedEvent> eventsBefore = eventService.getUserFeedEvents(user.getId());

        reviewService.deleteReview(saved.getReviewId());

        List<FeedEvent> eventsAfter = eventService.getUserFeedEvents(user.getId());
        assertEquals(eventsBefore.size() + 1, eventsAfter.size(), "Feed size should increase by 1 after deleting review");
        boolean found = eventsAfter.stream().anyMatch(event ->
                event.getEventType() == EventType.REVIEW &&
                        event.getOperation() == Operation.REMOVE &&
                        Objects.equals(event.getUserId(), user.getId()) &&
                        Objects.equals(event.getEntityId(), saved.getReviewId())
        );
        assertTrue(found, "Expected REVIEW REMOVE event not found in user feed");
    }

}