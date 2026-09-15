package com.product.CommanClasses;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Bean
    public CacheErrorHandler cacheErrorHandler() {

        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(
                    RuntimeException exception,
                    Cache cache,
                    Object key) {

                System.out.println(
                    "Redis GET failed. Using database. Error: "
                    + exception.getMessage()
                );
                
                System.out.println("Using DB....");
            }

            @Override
            public void handleCachePutError(
                    RuntimeException exception,
                    Cache cache,
                    Object key,
                    Object value) {

                System.out.println(
                    "Redis PUT failed. Continuing without cache."
                );
                System.out.println("Using DB....");
            }

            @Override
            public void handleCacheEvictError(
                    RuntimeException exception,
                    Cache cache,
                    Object key) {

                System.out.println(
                    "Redis EVICT failed. Continuing."
                );
                System.out.println("Using DB....");
            }

            @Override
            public void handleCacheClearError(
                    RuntimeException exception,
                    Cache cache) {

                System.out.println(
                    "Redis CLEAR failed. Continuing."
                );
                System.out.println("Using DB....");
            }
        };
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return cacheErrorHandler();
    }
}