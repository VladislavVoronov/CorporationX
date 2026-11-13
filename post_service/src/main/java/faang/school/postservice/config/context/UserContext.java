package faang.school.postservice.config.context;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
