package com.thuctap.bookmanage.service;

import java.util.Map;
import java.util.Set;

public interface RedisService {

  // get value function
  public Object get(Object key);

  public Object getValueFromHash(Object hashName, Object Key);

  public boolean isMemberOfSet(Object setName, Object value);

  public Set<Object> getAllMemberOfSet(Object setName);

  public Map<Object, Object> getAllEntriesOfMap(Object hashName);

  // put value function
  public void put(Object key, Object value);

  public void put(Object key, Object value, long timeout);

  public void putValueHash(Object hashName, Object key, Object value, boolean isExpire);

  public void putValueSet(Object setName, Object value, boolean isExpire);

  // delete value function
  public Boolean removeValue(Object key);

  public Object removeValueFromHash(Object hashName, Object... Key);

  public void removeHash(Object hashName);

  public Long removeMemberOfSet(Object setName, Object... value);

  // update value function
  public void updateValue(Object key, Object value);

  public void updateValueHash(Object hashName, Object key, Object value, boolean isExpire);

  // Size
  public Long sizeOfSet(Object setName);

  public Long sizeOfHash(Object hashName);

}

