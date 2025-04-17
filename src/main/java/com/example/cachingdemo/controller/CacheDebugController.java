package com.example.cachingdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentMap;

@RestController
@Slf4j
public class CacheDebugController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/cache/products")
    public Object getCachedProducts() {
        Cache cache = cacheManager.getCache("products");
        if (cache != null && cache.getNativeCache() instanceof ConcurrentMap) {
            log.info("the value is retrieve from the cache");
            return ((ConcurrentMap<?, ?>) cache.getNativeCache()).toString();
        }
        return "No data in cache or unsupported cache type.";
    }
}