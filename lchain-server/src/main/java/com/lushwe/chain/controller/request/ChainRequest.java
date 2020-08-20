package com.lushwe.chain.controller.request;

import lombok.Data;

/**
 * 说明：请求对象
 *
 * @author Jack Liu
 * @date 2020-08-13 10:15
 * @since 0.1
 */
@Data
public class ChainRequest {

    /**
     * 业务线
     */
    private String biz;

    /**
     * 过期时间（时间戳，毫秒）
     */
    private Long  expireTime;

    /**
     * 原始链接
     */
    private String originalUrl;
}
