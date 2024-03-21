package com.thuctap.bookmanage.common;

public class Constants {

  public static final String SERVICE_NAME = "book-manage-service";

  public static final class CACHE {
    public static final Long TIMEOUT_DEFAULT_MINUTE = 10l;
    public static final Long TIMEOUT_SET_MINUTE = 10l;
    public static final Long TIMEOUT_HASH_MINUTE = 10l;
    public static final Long TIMEOUT_VALUE_MINUTE = 10l;

    public static final Long TIMEOUT_1_MINUTE_IN_MINUTE = 1l;
    public static final Long TIMEOUT_1_MINUTE_IN_SECOND = 1L * 60;
    public static final Long TIMEOUT_5_MINUTE_IN_MINUTE = 5l;
    public static final Long TIMEOUT_5_MINUTE_IN_MINUTE_SECOND = 5l * 60;
    public static final Long TIMEOUT_10_MINUTE_IN_MINUTE = 10l;
    public static final Long TIMEOUT_30_MINUTE_IN_MINUTE = 30l;
    public static final Long TIMEOUT_1_HOUR_IN_MINUTE = 60l;
    public static final Long TIMEOUT_1_DAY_IN_MINUTE = 1440l;

    public static final String REDIS_GET_ALL_BOOK =
        Constants.SERVICE_NAME + ":get:all-book";

    public static final String REDIS_GET_ALL_TYPE =
        Constants.SERVICE_NAME + ":get:all-type";
  }

}
