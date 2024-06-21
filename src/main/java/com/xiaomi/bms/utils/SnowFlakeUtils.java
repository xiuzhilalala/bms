package com.xiaomi.bms.utils;

import java.util.Date;
import java.util.UUID;

/**
 * 雪花算法工具类，用于生成唯一的ID。
 */
public class SnowFlakeUtils {

    private static SnowFlakeUtils instance = new SnowFlakeUtils(0);

    /**
     * 初始化默认实例。
     *
     * @param machineId 机器ID。
     * @return SnowFlakeUtils 实例。
     */
    public static SnowFlakeUtils initDefaultInstance(int machineId) {
        instance = new SnowFlakeUtils(machineId);
        return instance;
    }

    /**
     * 获取当前实例。
     *
     * @return SnowFlakeUtils 实例。
     */
    public static SnowFlakeUtils getInstance() {
        return instance;
    }

    /**
     * 生成新的ID。
     *
     * @return long 唯一的ID。
     */
    public static long generateId() {
        return instance.nextId();
    }

    private final static long MACHINE_BIT = 5; // 最大 31
    private final static long SEQUENCE_BIT = 8; // 每毫秒最大 256

    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTMP_LEFT = MACHINE_BIT + SEQUENCE_BIT;

    private long machineId;
    private long sequence = 0L;
    private long lastStmp = -1L;

    /**
     * 构造函数，传入机器ID。
     *
     * @param machineId 机器ID。
     */
    public SnowFlakeUtils(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(
                    "machineId can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        this.machineId = machineId;
    }

    /**
     * 生成新的ID。
     *
     * @return long 唯一的ID。
     */
    public synchronized long nextId() {
        long currStmp = getTimestamp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currStmp = getNextTimestamp();
            }
        } else {
            sequence = 0L;
        }

        lastStmp = currStmp;

        return currStmp << TIMESTMP_LEFT //
                | machineId << MACHINE_LEFT //
                | sequence;
    }

    private long getNextTimestamp() {
        long mill = getTimestamp();
        while (mill <= lastStmp) {
            mill = getTimestamp();
        }
        return mill;
    }

    private long getTimestamp() {
        // 每10毫秒
        return System.currentTimeMillis() / 10;
    }

    /**
     * 解析ID的时间戳部分。
     *
     * @param id 唯一ID。
     * @return Date 时间对象。
     */
    public static Date parseIdTimestamp(long id) {
        return new Date((id >>> TIMESTMP_LEFT) * 10);
    }

    /**
     * 生成UUID字符串。
     *
     * @return String UUID字符串。
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 主函数测试。
     *
     * @param args 参数。
     */

}
