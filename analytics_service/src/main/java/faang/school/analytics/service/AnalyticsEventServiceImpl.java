package faang.school.analytics.service;

import faang.school.analytics.dto.event.CommentEvent;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.redis.listener.AnalyticsEventMapper;
import faang.school.analytics.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AnalyticsEventServiceImpl implements AnalyticsEventService {

    private final AnalyticsEventRepository repository;
    private final AnalyticsEventMapper analyticsEventMapper;

    @Transactional(readOnly = true)
    public Stream<AnalyticsEvent> getAnalytics(long receiverId, EventType eventType) {
        return repository.findByReceiverIdAndEventType(receiverId, eventType);
    }

    @Override
    public void saveCommentEvent(CommentEvent event) {
        AnalyticsEvent analyticsEvent = analyticsEventMapper.toAnalyticsEvent(event);
        analyticsEvent.setEventType(EventType.POST_COMMENT);
        repository.save(analyticsEvent);
    }
}
