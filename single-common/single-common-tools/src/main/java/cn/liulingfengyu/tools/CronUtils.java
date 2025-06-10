package cn.liulingfengyu.tools;

import org.springframework.scheduling.support.CronExpression;

import java.time.Duration;
import java.time.LocalDateTime;

public class CronUtils {

    /**
     * 获取下一个执行延迟时间
     *
     * @param cron 时间表达式
     * @return 返回当前时间到执行时间之间的毫秒数，如果返回-1则表示时间过期或者不合法
     */
    public static long getNextTimeDelayMilliseconds(String cron) {
        // 校验 cron
        if (isExpired(cron)) {
            return -1;
        }
        try {
            // 获取下一执行时间
            LocalDateTime now = LocalDateTime.now();
            CronExpression cronSequence = CronExpression.parse(cron.substring(0, cron.lastIndexOf(' ')).trim());
            LocalDateTime nextTime = cronSequence.next(now);
            // 获取延迟毫秒数
            Duration nowToNextTimeduration = Duration.between(now, nextTime);
            return nowToNextTimeduration.toMillis();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 校验 cron 表达式是否合法
     *
     * @param cron cron 表达式
     * @return true - 合法；false - 不合法
     */
    public static boolean isValidCron(String cron) {
        if (cron == null || cron.trim().isEmpty()) {
            return false;
        }

        String[] split = cron.trim().split("\\s+");
        int length = split.length;

        // 只接受 6 位或 7 位表达式
        if (length != 6 && length != 7) {
            return false;
        }

        // 如果是 7 位，单独处理年份字段
        if (length == 7) {
            String yearField = split[6].trim();
            if (isValidYearField(yearField)) {
                return false;
            }
            // 去掉年份部分，使用前6位进行标准校验
            cron = cron.substring(0, cron.lastIndexOf(' ')).trim();
        }

        return CronExpression.isValidExpression(cron);
    }

    /**
     * 校验年份字段是否合法
     * 支持格式：
     * - 单个年份（如 "2025"）
     * - 通配符（如 "*"）
     * - 范围（如 "2020-2025"）
     * - 列表（如 "2020,2021,2022"）
     *
     * @param yearField 年份字段字符串
     * @return true - 合法；false - 不合法
     */
    private static boolean isValidYearField(String yearField) {
        if (yearField.equals("*")) {
            return false;
        }

        // 正则匹配：数字、逗号、短横线
        return !yearField.matches("^(\\d{4})(,(\\d{4}))*$") &&
                !yearField.matches("^\\d{4}-\\d{4}$");
    }

    /**
     * 验证 cron 是否过期
     *
     * @param cron 时间表达式
     * @return 返回过期状态值 false-未过期；true-已过期
     */
    public static boolean isExpired(String cron) {
        // 校验 cron
        if (!isValidCron(cron)) {
            return true;
        }
        String[] split = cron.trim().split("\\s+");
        int length = split.length;
        LocalDateTime now = LocalDateTime.now();

        try {
            //  获取下一次执行时间
            CronExpression cronExp = CronExpression.parse(cron.substring(0, cron.lastIndexOf(' ')).trim());
            LocalDateTime nextTime = cronExp.next(now);
            if (nextTime != null) {
                // 处理 7 位带年份的 cron
                if (length == 7) {
                    int nextTimeYear = nextTime.getYear();
                    String yearField = split[6].trim();
                    if (isValidYearField(yearField)) {
                        return true; // 年份字段非法视为过期
                    }

                    // 检查年份是否已过期
                    if (yearField.contains("-")) {
                        String[] range = yearField.split("-");
                        int start = Integer.parseInt(range[0]);
                        int end = Integer.parseInt(range[1]);
                        // 验证年份范围
                        if (start > end) {
                            return true;
                        }
                        // 检查年份是否已过
                        if (nextTimeYear > end || nextTimeYear < start) {
                            return true;
                        }
                    } else {
                        int year = Integer.parseInt(yearField);
                        // 检查年份是否已过
                        if (nextTimeYear > year) {
                            return true;
                        }
                    }
                }
            }
            return nextTime == null || nextTime.isBefore(now);
        } catch (Exception e) {
            return true;
        }
    }
}