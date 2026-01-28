package it.gov.pagopa.payhub.ionotification.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "cache")
@EnableCaching
@Data
@FieldNameConstants
public class CacheConfig {

    @NestedConfigurationProperty
    private CacheConfigurationProperties organizationApiKey;
    @NestedConfigurationProperty
    private CacheConfigurationProperties ioServices;
    @NestedConfigurationProperty
    private CacheConfigurationProperties ioProfiles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheConfigurationProperties {
        private long size;
        private long expireIn;
    }

    @Bean
    public CacheManager localCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache(Fields.organizationApiKey, buildCache(organizationApiKey));
        cacheManager.registerCustomCache(Fields.ioServices, buildCache(ioServices));
        cacheManager.registerCustomCache(Fields.ioProfiles, buildCache(ioProfiles));
        return cacheManager;
    }

    private Cache<Object, Object> buildCache(CacheConfigurationProperties cacheConfig) {
        return Caffeine.newBuilder()
                .maximumSize(cacheConfig.size)
                .expireAfterAccess(cacheConfig.expireIn, TimeUnit.MINUTES)
                .build();
    }
}
