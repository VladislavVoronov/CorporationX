package faang.school.analytics.redis.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.event.CommentEvent;
import faang.school.analytics.service.AnalyticsEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCreateListenerTest {

    @Mock
    private AnalyticsEventService analyticService;
    @Mock
    private AnalyticsEventMapper analyticsEventMapper;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CommentCreateListener commentCreateListener;

    private CommentEvent commentEvent;

    private final String jsonEvent = "{\"id\":111,\"authorId\":222,\"commentId\":333,\"viewTime\":\"2024-11-07T10:01:02\"}";

    @BeforeEach
    void setUp() {
        Long id = 111l;
        Long authorId = 222l;
        Long commentId = 333l;
        CommentEvent commentEvent = new CommentEvent(id, authorId, commentId, LocalDateTime.parse("2024-11-07T10:01:02"));
    }

    @Test
    public void handleMessage_shouldProcessEventSuccessfully() throws JsonProcessingException {
        when(objectMapper.readValue(jsonEvent, CommentEvent.class)).thenReturn(commentEvent);
        commentCreateListener.handleMessage(jsonEvent);
        verify(analyticService).saveCommentEvent(commentEvent);
    }

    @Test
    public void handleMessage_shouldLogErrorWhenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        when(objectMapper.readValue(jsonEvent, CommentEvent.class)).thenThrow(new JsonProcessingException("Error") {
        });
        assertThrows(RuntimeException.class, () -> commentCreateListener.handleMessage(jsonEvent));
        verify(analyticService, never()).saveCommentEvent(any());
    }
}