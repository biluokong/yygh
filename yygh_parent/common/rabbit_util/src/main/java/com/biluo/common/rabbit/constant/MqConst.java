package com.biluo.common.rabbit.constant;

public interface MqConst {
    /**
     * 预约下单
     */
    String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";
    String ROUTING_ORDER = "order";
    //队列
    String QUEUE_ORDER  = "queue.order";

    /**
     * 短信
     */
    String EXCHANGE_DIRECT_MSM = "exchange.direct.msm";
    String ROUTING_MSM_ITEM = "msm.item";
    //队列
    String QUEUE_MSM_ITEM  = "queue.msm.item";

    /**
     * 定时任务
     */
    String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    String ROUTING_TASK_8 = "task.8";
    //队列
    String QUEUE_TASK_8 = "queue.task.8";


}

