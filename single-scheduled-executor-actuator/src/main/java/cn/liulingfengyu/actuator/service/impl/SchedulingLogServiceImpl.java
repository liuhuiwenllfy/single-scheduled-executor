package cn.liulingfengyu.actuator.service.impl;

import cn.liulingfengyu.actuator.bo.CallbackBo;
import cn.liulingfengyu.actuator.bo.TaskInfoBo;
import cn.liulingfengyu.actuator.dto.SchedulingLogDto;
import cn.liulingfengyu.actuator.entity.SchedulingLog;
import cn.liulingfengyu.actuator.mapper.SchedulingLogMapper;
import cn.liulingfengyu.actuator.service.ISchedulingLogService;
import cn.liulingfengyu.actuator.vo.SchedulingLogVo;
import cn.liulingfengyu.mybatisplus.entity.PageInfo;
import cn.liulingfengyu.mybatisplus.utils.PageUtils;
import cn.liulingfengyu.rabbitmq.bind.SchedulingLogBind;
import cn.liulingfengyu.redis.constant.RedisConstant;
import cn.liulingfengyu.redis.utils.RedisUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 调度日志表 服务实现类
 * </p>
 *
 * @author LLFY
 * @since 2023-11-23
 */
@Service
@DS("scheduled_task")
public class SchedulingLogServiceImpl extends ServiceImpl<SchedulingLogMapper, SchedulingLog> implements ISchedulingLogService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<SchedulingLogVo> getByPage(PageInfo pageInfo, SchedulingLogDto schedulingLogDto) {
        Page<SchedulingLogVo> page = new PageUtils<SchedulingLogVo>().getPage(pageInfo);
        return page.setRecords(baseMapper.getByPage(page, schedulingLogDto));
    }

    @RabbitListener(queues = SchedulingLogBind.SCHEDULING_LOG_QUEUE_NAME)
    public void insertItem(CallbackBo callbackBo, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // 消息幂等处理
        String redisKey = RedisConstant.CALLBACK_IDEMPOTENT.concat(callbackBo.getUuId());
        if (Boolean.FALSE.equals(redisUtil.setIfAbsentEx(redisKey, "1", 1, TimeUnit.DAYS))) {
            channel.basicAck(deliveryTag, false);
            return;
        }
        try {
            TaskInfoBo taskInfoBo = callbackBo.getTaskInfoBo();
            SchedulingLog schedulingLog = new SchedulingLog();
            schedulingLog.setTaskId(taskInfoBo.getId());
            schedulingLog.setAppName(taskInfoBo.getAppName());
            schedulingLog.setTaskParam(taskInfoBo.getTaskParam());
            schedulingLog.setDone(taskInfoBo.isDone());
            schedulingLog.setCreateTime(new Date());
            this.save(schedulingLog);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
        }
    }
}
