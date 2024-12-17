package faang.school.analytics.service;

import faang.school.analytics.dto.event.CommentEvent;

public interface AnalyticsEventService {
    void saveCommentEvent(CommentEvent event);
}
