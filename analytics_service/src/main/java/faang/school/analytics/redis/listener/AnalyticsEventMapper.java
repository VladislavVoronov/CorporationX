package faang.school.analytics.redis.listener;

import faang.school.analytics.dto.event.AnalyticsEventDto;
import faang.school.analytics.dto.event.CommentEvent;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnalyticsEventMapper {
    @Mapping(target = "eventType", source = "postId", qualifiedByName = "eventType")
    @Mapping(source = "authorId", target = "actorId")
    @Mapping(source = "postId", target = "receiverId")
    @Mapping(source = "createdAt", target = "receivedAt")

    AnalyticsEvent toAnalyticsEvent(CommentEvent commentEvent);
    List<AnalyticsEventDto> toDtoList(List<AnalyticsEvent> events);

    @Named(value = "eventType")
    default EventType getEventType(long postId) {
        return EventType.POST_COMMENT;
    }
}
