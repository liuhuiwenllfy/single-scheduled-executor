package cn.liulingfengyu.mybatisplus.handler;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动补全参数配置
 *
 * @author LLFY
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        JSONObject jsonObject = JSONUtil.toBean(JSONUtil.toJsonStr(metaObject.getOriginalObject()), JSONObject.class);
        //创建时间
        if (!jsonObject.containsKey("createTime") || jsonObject.get("createTime") == null) {
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        JSONObject jsonObject = JSONUtil.toBean(JSONUtil.toJsonStr(metaObject.getOriginalObject()), JSONObject.class);
        //修改时间
        if (!jsonObject.containsKey("updateTime") || jsonObject.get("updateTime") == null) {
            this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        }
    }
}
