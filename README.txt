Caching

https://docs.spring.io/spring-boot/reference/io/caching.html — check latest though

* Spring Boot auto-configures the cache infrastructure as long as caching support is enabled by using the @EnableCaching annotation.
* For implementing caching , we can directly use spring framework or the standard JSR 107 annotation such as @CacheResult ( JCache )
If you do not add any specific cache library, Spring Boot auto-configures a simple provider that uses concurrent maps in memory. When a cache is required (such as piDecimals in the preceding example), this provider creates it for you.
* caching.html
* If none of the other providers can be found, a simple implementation using a ConcurrentHashMap as the cache store is configured. This is the default if no caching library is present in your application. By default, caches are created as needed, but you can restrict the list of available caches by setting the cache-names property. For instance, if you want only cache1 and cache2 caches, set the cache-names property as follows:
* spring.cache.cache-names=cache1,cache2
* If you do so and your application uses a cache not listed, then it fails at runtime when the cache is needed, but not on startup. This is similar to the way the "real" cache providers behave if you use an undeclared cache.
* When @EnableCaching is present in your configuration, a suitable cache configuration is expected as well. If you have a custom ` org.springframework.cache.CacheManager`, consider defining it in a separate @Configuration class so that you can override it if necessary. None uses a no-op implementation that is useful in tests, and slice tests use that by default via @AutoConfigureCache.
* If you need to use a no-op cache rather than the auto-configured cache manager in a certain environment, set the cache type to none, as shown in the following example:
* spring.cache.type=none

Cache and CacheManager are interfaces.
The implementing classes are
￼
By default, ConcurrentMapCacheManager is used in Spring Boot.

If you didn’t explicitly configure a cache, then by default Spring Boot uses: org.springframework.cache.concurrent.ConcurrentMapCacheManager
So @Cacheable("products") will store data in an in-memory ConcurrentMap, and it uses the bean:

@Bean
public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
}

Caching dependecy

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

If we use the starter , it will automatically  bring the below dependency . If we are not using the starter, we will have to add this dependency.

<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-context-support</artifactId>
</dependency>

Use of spring context
Feature	Description
Dependency Injection (DI)	Core support for managing beans and wiring dependencies
ApplicationContext	Advanced container that builds on BeanFactory — supports lifecycle events, resource loading, internationalization, etc.
@Component, @Service, @Repository, @Controller	Enables annotation-based configuration and scanning
@Autowired / @Qualifier	Dependency injection annotations
@Value	Inject values from property files or system env
Event Publishing	Built-in event mechanism with ApplicationEventPublisher
Environment Abstraction	Access system properties, profiles, config files
Scheduling	Support for @Scheduled, TaskScheduler etc.
Caching Abstraction	Support for @Cacheable, @CachePut, @CacheEvict
Profile Support	@Profile("dev") for conditional bean registration
how can I check what data is saved in the cache?
Since Spring’s caching abstraction (like @Cacheable, @CachePut, etc.) hides the underlying implementation, you can't directly "see" the cached data unless you use a cache provider that allows inspection — like Caffeine, Ehcache, Redis, or even simple ConcurrentMapCacheManager (default in Spring Boot).

Option 1: Use CacheManager to Programmatically Inspect Cache
You can inject CacheManager and retrieve the cache content like this:

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentMap;

@RestController
public class CacheDebugController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/cache/products")
    public Object getCachedProducts() {
        Cache cache = cacheManager.getCache("products");
        if (cache != null && cache.getNativeCache() instanceof ConcurrentMap) {
            return ((ConcurrentMap<?, ?>) cache.getNativeCache()).toString();
        }
        return "No data in cache or unsupported cache type.";
    }
}
Besides, we can enable logging of the cache operation but note that it will not print the data in cache
logging.level.org.springframework.cache=DEBUG


How does this call flow works?

When a method is annotated with @Cacheable("products") , the call goes to ->

CacheInterceptor -> CacheAspectSupport -> ConcurrentMapCacheManager

If we are doing a GET call then initially spring boot will check whether any other caching library is implemented or not. If there is no clashing implemented,  default concurrent map is used.

When the data is fetched from the database before returning this data to the client, the map gets updated with this particular value.

