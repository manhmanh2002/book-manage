package com.thuctap.bookmanage.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@EnableRedisRepositories
public class RedisConfig extends CachingConfigurerSupport {

  @Value("${redis.server}")
  private String redisServer;

  @Value("${redis.port}")
  private Integer redisPort;

  @Value("${redis.password}")
  private String redisPassword;

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    jedisConnectionFactory.setHostName(redisServer);
    jedisConnectionFactory.setPort(redisPort);
    jedisConnectionFactory.setPassword(redisPassword);
    return jedisConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = null;
    try {
      template = new RedisTemplate<>();
      template.setConnectionFactory(jedisConnectionFactory());

      template.setKeySerializer(new StringRedisSerializer());
      template.setValueSerializer(valueRedisSerializer());

      template.afterPropertiesSet();
//      template.expire(Constants.CACHE.BEARER_USER, 10, TimeUnit.MINUTES);
//      template.expire(Constants.CACHE.PUBLISHER_INFO_HASH, 10, TimeUnit.MINUTES);
//      template.expire(Constants.CACHE.PUBLISHER_SERVICES_SET, 10, TimeUnit.MINUTES);
//      template.expire(Constants.CACHE.USER_INFO_HASH, 10, TimeUnit.MINUTES);
//      template.expire(Constants.CACHE.USER_UID_HASH, 10, TimeUnit.MINUTES);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return template;
  }

  private RedisSerializer valueRedisSerializer() {
    Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    serializer.setObjectMapper(mapper);
    return serializer;
  }
}
