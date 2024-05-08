package cn.liulingfengyu.tools;

import org.springframework.scheduling.support.CronExpression;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class CronUtils {

    /**
     * 获取下一个执行延迟时间
     *
     * @param cron 时间
     * @return 返回当前时间到执行时间之间的毫秒数，如果返回-1则表示时间过期或者不合法
     */
    public static long getNextTimeDelayMilliseconds(String cron) {
        try {
            //获取字符串长度
            String[] split = cron.split(" ");
            int lastIndex = -1;
            if (split.length == 7) {
                //获取最后一个空格下标
                lastIndex = cron.lastIndexOf(' ');
                //验证年份格式是否合法
                // 正则表达式，匹配只包含数字、星号、斜杠、减号和逗号的字符串
                String pattern = "^[0-9*,/-]+$";
                if (!Pattern.matches(pattern, cron.substring(lastIndex + 1))) {
                    return -1;
                }
            }
            //获取下一执行时间
            LocalDateTime now = LocalDateTime.now();
            CronExpression cronSequence = CronExpression.parse(lastIndex == -1 ? cron : cron.substring(0, lastIndex));
            LocalDateTime nextTime = cronSequence.next(now);
            //获取延迟毫秒数
            Duration nowToNextTimeduration = Duration.between(now, nextTime);
            return nowToNextTimeduration.toMillis();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 验证cron是否过期
     *
     * @param cron 时间
     * @return 返回过期状态值 false-未过期；true-已过过期
     */
    public static boolean isExpired(String cron) {
        String[] split = cron.split(" ");
        if (split.length == 7) {
            //验证当前任务是否在年份范围内
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            if (split[6].contains("-")) {
                String[] yearSection = split[6].split("-");
                if (yearSection.length == 2) {
                    return Integer.parseInt(yearSection[0]) >= year || Integer.parseInt(yearSection[1]) <= year;
                }
            } else if (split[6].equals("*")) {
                return false;
            } else {
                return split[6].contains(String.valueOf(year));
            }

        }
        return false;
    }
}
