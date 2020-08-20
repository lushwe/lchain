package com.lushwe.chain.controller;

import com.lushwe.chain.controller.request.ChainRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 说明：短链接
 *
 * @author Jack Liu
 * @date 2020-08-13 10:13
 * @since 0.1
 */
@RestController
public class ChainController {

    private static ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();

    private static AtomicLong count = new AtomicLong(100000000L);

    /**
     * 随机打乱
     */
    private static String BASE = "MhvOi0c71ULaSPyegXEkts3Rf8Wj9xbT6Y2NpAlKzFr4GJHCouQqnVZ5dBImDw";

    private static final String url_prefix = "http://localhost:8888/";


    /**
     * 生成短链接
     *
     * @param request
     * @return
     */
    @PostMapping("/chains")
    public String createShortUrl(@RequestBody ChainRequest request) {
        long id = count.getAndIncrement();
        String code = toBase62(insertRandomBitPer5Bits(id));
        data.put(code, request.getOriginalUrl());
        return url_prefix + code;
    }

    /**
     * 重定向到原始链接
     *
     * @param code
     * @param response
     */
    @GetMapping("/{code}")
    public void redirectToOriginalUrl(@PathVariable("code") String code, HttpServletResponse response) {
        String originalUrl = data.get(code);

//        response.setStatus(HttpStatus.FOUND.value());
//        response.setHeader("Location", longUrl);

        try {
            response.sendRedirect(originalUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入随机位
     *
     * @param val
     * @return
     */
    private static long insertRandomBitPer5Bits(long val) {
        Random random = new Random();
        long result = val;
        long high = val;
        for (int i = 0; i < 10; i++) {
            if (high == 0) {
                break;
            }
            int pos = 5 + 5 * i + i;
            high = result >> pos;
            result = ((high << 1 | random.nextInt(2)) << pos) | (result & (-1L >>> (64 - pos)));
        }
        return result;
    }

    /**
     * 10进制转62进制
     *
     * @param num
     * @return
     */
    public static String toBase62(long num) {
        StringBuilder sb = new StringBuilder();
        int targetBase = BASE.length();
        do {
            int i = (int) (num % targetBase);
            sb.append(BASE.charAt(i));
            num /= targetBase;
        } while (num > 0);

        return sb.reverse().toString();
    }

    /**
     * 62进制转10进制
     *
     * @param input
     * @return
     */
    public static long toBase10(String input) {
        int srcBase = BASE.length();
        long id = 0;
        String r = new StringBuilder(input).reverse().toString();

        for (int i = 0; i < r.length(); i++) {
            int charIndex = BASE.indexOf(r.charAt(i));
            id += charIndex * (long) Math.pow(srcBase, i);
        }

        return id;
    }
}
