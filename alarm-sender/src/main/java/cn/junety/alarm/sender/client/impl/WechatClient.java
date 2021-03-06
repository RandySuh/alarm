package cn.junety.alarm.sender.client.impl;

import cn.junety.alarm.base.common.ConfigKey;
import cn.junety.alarm.base.entity.QueueMessage;
import cn.junety.alarm.sender.client.Client;
import cn.junety.alarm.sender.wechat.WechatClientProxy;
import com.alibaba.fastjson.JSON;

/**
 * Created by caijt on 2017/3/29.
 */
public class WechatClient extends Client {

    private WechatClientProxy client;

    public WechatClient(String name, String queueName) {
        super(name, queueName, "wechat");
        client = new WechatClientProxy();
    }

    @Override
    protected int send(String message) {
        try {
            logger.debug("start send wechat alarm, body:{}", message);
            QueueMessage queueMessage = JSON.parseObject(message, QueueMessage.class);
            client.send(queueMessage.getReceivers(), queueMessage.getContent());
            this.markDeliveryStatus(queueMessage.getLogId(), channel);
            return queueMessage.getReceivers().size();
        } catch (Exception e) {
            logger.error("handle mail message error, caused by", e);
            return 0;
        }
    }

    @Override
    protected String getPushQuantityKey() {
        return ConfigKey.WECHAT_PUSH_QUANTITY.value();
    }

    @Override
    protected String getPushDailyKey() {
        return ConfigKey.WECHAT_PUSH_DAILY.value();
    }

    public static void main(String[] args) {
        WechatClient wechatClient = new WechatClient("wechat", ConfigKey.WECHAT_QUEUE.value());
        wechatClient.start();
    }
}
