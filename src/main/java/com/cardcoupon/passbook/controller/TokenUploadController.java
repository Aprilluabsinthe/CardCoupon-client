package com.cardcoupon.passbook.controller;

/**
 * PassTemplate Token Upload Controller
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
public class TokenUploadController {
    /** redis client side */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public TokenUploadController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Write token to redis
     * @param path {@link Path}
     * @param key redis key
     * @return true/false
     */
    private boolean writeTokeToRedis(Path path, String key) {
        Set<String> tokens;
        // each line is a token
        try(Stream<String> stream = Files.lines(path)){
            tokens = stream.collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // store tokens to redis
        // not empty && size > 0
        if(!CollectionUtils.isEmpty(tokens)){
            // single pipeline
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for(String token : tokens){
                    connection.sAdd(key.getBytes(),token.getBytes());
                }
                return null;
            });
            return true;
        }

        return false;
    }
}
