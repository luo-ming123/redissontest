package com.luom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
@Slf4j
@RestController
@RequestMapping("/redissonLock")
@RequiredArgsConstructor
public class RedissonController {

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_KEY = "redis_oa_01";

    private static final int MAX_SIZE = 1000;

    /**
     * 初始化库存
     */
    @GetMapping("/init")
    public void init(){
        stringRedisTemplate.opsForValue().set(REDIS_KEY,String.valueOf(MAX_SIZE));
    }

    /**
     * 扣库存业务
     */
    @PostMapping("/exportInventory")
    public void exportInventory(){

        String lockKey = "product001";
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();
            int s = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(REDIS_KEY)));
            s--;
            if(s >= 0){
                System.out.println("2号服务：库存当前为："+ s + "\n");
            }else{
                log.info("没有库存了");
                throw new Exception();
            }
            stringRedisTemplate.opsForValue().set(REDIS_KEY,String.valueOf(s));
        }catch (Exception e){
        }finally {
            lock.unlock();
        }

    }


}
