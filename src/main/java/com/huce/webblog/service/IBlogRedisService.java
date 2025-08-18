package com.huce.webblog.service;

import com.huce.webblog.service.base.BaseRedisServiceV2;

public interface IBlogRedisService extends BaseRedisServiceV2<String, String, Integer> {
    boolean isUserViewed(String uid, String pid);
}