package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.feedevent.FeedEvent;
import ru.yandex.practicum.filmorate.service.FeedEventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {
    private final FeedEventService feedEventService;

    @GetMapping("/users/{id}/feed")
    public List<FeedEvent> getFeedEvents(@PathVariable Long id) {
        return feedEventService.getUserFeedEvents(id);
    }
}
