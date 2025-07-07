package com.example.udtbe.global.util;

import static com.example.udtbe.global.exception.code.RedisErrorCode.REDIS_DELETE_ERROR;
import static com.example.udtbe.global.exception.code.RedisErrorCode.REDIS_EXPIRED_ERROR;
import static com.example.udtbe.global.exception.code.RedisErrorCode.REDIS_EXPIRE_ERROR;
import static com.example.udtbe.global.exception.code.RedisErrorCode.REDIS_FIND_ERROR;
import static com.example.udtbe.global.exception.code.RedisErrorCode.REDIS_SAVE_ERROR;

import com.example.udtbe.global.exception.RestApiException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String key, String data) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(key, data);
        } catch (Exception e) {
            throw new RestApiException(REDIS_SAVE_ERROR);
        }

    }

    public void setValues(String key, String data, Duration duration) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(key, data, duration);
        } catch (Exception e) {
            throw new RestApiException(REDIS_SAVE_ERROR);
        }
    }

    public String getValues(String key) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            if (values.get(key) == null) {
                return "false";
            }
            return (String) values.get(key);
        } catch (Exception e) {
            throw new RestApiException(REDIS_FIND_ERROR);
        }
    }

    public void deleteValues(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RestApiException(REDIS_DELETE_ERROR);
        }
    }

    public void expireValues(String key, int timeout) {
        try {
            redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            throw new RestApiException(REDIS_EXPIRE_ERROR);
        }
    }

    public boolean validateData(String key, String data) {
        String findValues = this.getValues(key);
        if (Objects.equals(findValues, "false")) {
            throw new RestApiException(REDIS_FIND_ERROR);
        }

        return Objects.equals(findValues, data);
    }

    public void validateExpiredFromKey(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (ttl == null || ttl <= 0) {
            throw new RestApiException(REDIS_EXPIRED_ERROR);
        }
    }

}
