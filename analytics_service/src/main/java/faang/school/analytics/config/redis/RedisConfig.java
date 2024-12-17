package faang.school.analytics.config.redis;

import faang.school.analytics.redis.listener.CommentCreateListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public ChannelTopic postCommentChannelTopic() {
        return new ChannelTopic(redisProperties.getChannels().get("post-comment"));
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            CommentCreateListener commentCreateListener,
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(commentEventListener(commentCreateListener), postCommentChannelTopic());
        return container;
    }

    @Bean
    public MessageListenerAdapter commentEventListener(CommentCreateListener commentCreateListener) {
        return new MessageListenerAdapter(commentCreateListener);
    }
}
