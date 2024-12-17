package faang.school.analytics.redis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> implements MessageListener {

    private final ObjectMapper objectMapper;

    public abstract void saveEvent(T event);

    public abstract Class<T> getEventType();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            T event = objectMapper.convertValue(messageBody, getEventType());
            saveEvent(event);
        } catch (Exception e) {
            log.error("Error message during processing: {}", new String(message.getBody()), e);
        }
    }
}
