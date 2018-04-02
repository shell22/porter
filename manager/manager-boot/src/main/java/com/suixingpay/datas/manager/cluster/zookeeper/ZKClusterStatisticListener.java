/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月15日 10:09
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.suixingpay.datas.manager.cluster.zookeeper;

import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suixingpay.datas.common.cluster.ClusterListenerFilter;
import com.suixingpay.datas.common.cluster.event.ClusterEvent;
import com.suixingpay.datas.common.cluster.impl.zookeeper.ZookeeperClusterEvent;
import com.suixingpay.datas.common.cluster.impl.zookeeper.ZookeeperClusterListener;
import com.suixingpay.datas.common.cluster.impl.zookeeper.ZookeeperClusterListenerFilter;
import com.suixingpay.datas.common.statistics.NodeLog;
import com.suixingpay.datas.common.statistics.TaskPerformance;
import com.suixingpay.datas.manager.core.util.ApplicationContextUtil;
import com.suixingpay.datas.manager.service.MrJobTasksMonitorService;
import com.suixingpay.datas.manager.service.MrLogMonitorService;
import com.suixingpay.datas.manager.service.MrNodesMonitorService;
import com.suixingpay.datas.manager.service.impl.MrJobTasksMonitorServiceImpl;
import com.suixingpay.datas.manager.service.impl.MrLogMonitorServiceImpl;
import com.suixingpay.datas.manager.service.impl.MrNodesMonitorServiceImpl;

/**
 * 统计信息下载
 * 
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月15日 10:09
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月15日 10:09
 */
public class ZKClusterStatisticListener extends ZookeeperClusterListener {
    private static final String ZK_PATH = BASE_CATALOG + "/statistic";
    private static final Pattern LOG_PATTERN = Pattern.compile(ZK_PATH + "/log/.*");
    private static final Pattern TASK_PATTERN = Pattern.compile(ZK_PATH + "/task/.*");

    @Override
    public String listenPath() {
        return ZK_PATH;
    }

    @Override
    public void onEvent(ClusterEvent event) {
        ZookeeperClusterEvent zkEvent = (ZookeeperClusterEvent) event;
        LOGGER.debug("StatisticListener:{},{},{}", zkEvent.getPath(), zkEvent.getData(), zkEvent.getEventType());
        String zkPath = zkEvent.getPath();

        if (zkEvent.isOnline()) {
            // 日志
            if (LOG_PATTERN.matcher(zkPath).matches()) {
                NodeLog log = JSONObject.parseObject(zkEvent.getData(), NodeLog.class);
                System.err.println("3-NodeLog....."+JSON.toJSON(log));
                // do something
                MrLogMonitorService mrLogMonitorService = ApplicationContextUtil.getBean(MrLogMonitorServiceImpl.class);
                mrLogMonitorService.dealNodeLog(log);
            }

            // 性能指标数据
            if (TASK_PATTERN.matcher(zkPath).matches()) {
                TaskPerformance performance = JSONObject.parseObject(zkEvent.getData(), TaskPerformance.class);
                System.out.println("3-TaskPerformance....."+JSON.toJSON(performance));
                // do something
                //任务泳道实时监控表 服务接口类
                MrJobTasksMonitorService mrJobTasksMonitorService = ApplicationContextUtil.getBean(MrJobTasksMonitorServiceImpl.class);
                mrJobTasksMonitorService.dealTaskPerformance(performance);
                //节点任务实时监控表
                MrNodesMonitorService mrNodesMonitorService = ApplicationContextUtil.getBean(MrNodesMonitorServiceImpl.class);
                mrNodesMonitorService.dealTaskPerformance(performance);
            }
            // 删除已获取的事件
            client.delete(zkPath);
        }
    }

    @Override
    public ClusterListenerFilter filter() {
        return new ZookeeperClusterListenerFilter() {
            @Override
            protected String getPath() {
                return listenPath();
            }

            @Override
            protected boolean doFilter(ZookeeperClusterEvent event) {
                return false;
            }
        };
    }

    @Override
    public void start() {
        client.createWhenNotExists(ZK_PATH + "/log", false, true, null);
        client.createWhenNotExists(ZK_PATH + "/task", false, true, null);
    }
}