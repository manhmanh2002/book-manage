package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.common.Constants;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

  @Autowired
  private RedisTemplate redisTemplate;

  @Override
  public Object get(Object key) {
    return redisTemplate.opsForValue().get(key);
  }

  @Override
  public Object getValueFromHash(Object hashName, Object Key) {
    return redisTemplate.opsForHash().get(hashName, Key);
  }

  @Override
  public boolean isMemberOfSet(Object setName, Object value) {
    return redisTemplate.opsForSet().isMember(setName, value);
  }

  @Override
  public Set<Object> getAllMemberOfSet(Object setName) {
    return redisTemplate.opsForSet().members(setName);
  }

  @Override
  public Map<Object, Object> getAllEntriesOfMap(Object mapName) {
    return redisTemplate.opsForHash().entries(mapName);
  }

  @Override
  public void put(Object key, Object value) {
    redisTemplate.opsForValue().set(key, value);
    redisTemplate.expire(key, Constants.CACHE.TIMEOUT_VALUE_MINUTE, TimeUnit.MINUTES);
  }

  @Override
  public void put(Object key, Object value, long timeout) {
    redisTemplate.opsForValue().set(key, value);
    redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
  }

  @Override
  public void putValueHash(Object hashName, Object key, Object value, boolean isExpire) {
    redisTemplate.opsForHash().put(hashName, key, value);
    if (isExpire) {
      redisTemplate.expire(hashName, Constants.CACHE.TIMEOUT_HASH_MINUTE, TimeUnit.MINUTES);
    }
  }

  @Override
  public void putValueSet(Object setName, Object value, boolean isExpire) {
    redisTemplate.opsForSet().add(setName, value);
    if (isExpire) {
      redisTemplate.expire(setName, Constants.CACHE.TIMEOUT_SET_MINUTE, TimeUnit.MINUTES);
    }
  }

  @Override
  public Boolean removeValue(Object key) {
    return redisTemplate.delete(key);
  }

  @Override
  public Long removeValueFromHash(Object hashName, Object... Key) {
    return redisTemplate.opsForHash().delete(hashName, Key);
  }

  @Override
  public void removeHash(Object hashName) {
    redisTemplate.expire(hashName, Duration.ZERO.getSeconds(), TimeUnit.SECONDS);
//    redisTemplate.opsForHash().entries(hashName).keySet()
//        .forEach(haskKey -> redisTemplate.opsForHash().delete(hashName, haskKey));
  }

  @Override
  public Long removeMemberOfSet(Object setName, Object... value) {
    return redisTemplate.opsForSet().remove(setName, value);
  }

  @Override
  public void updateValue(Object key, Object value) {
    if (redisTemplate.opsForValue().get(key) != null) {
      redisTemplate.delete(key);
      redisTemplate.opsForValue().set(key, value);
      redisTemplate.expire(key, Constants.CACHE.TIMEOUT_VALUE_MINUTE, TimeUnit.MINUTES);
    }
  }

  @Override
  public void updateValueHash(Object hashName, Object key, Object value, boolean isExpire) {
    if (redisTemplate.opsForHash().hasKey(hashName, key)) {
      redisTemplate.opsForHash().delete(hashName, key);
      redisTemplate.opsForHash().put(hashName, key, value);
      if (isExpire) {
        redisTemplate.expire(hashName, Constants.CACHE.TIMEOUT_HASH_MINUTE, TimeUnit.MINUTES);
      }
    }
  }

  @Override
  public Long sizeOfSet(Object setName) {
    return redisTemplate.opsForSet().size(setName);
  }

  @Override
  public Long sizeOfHash(Object hashName) {
    return redisTemplate.opsForHash().size(hashName);
  }
}
