/**
 *
 */
package com.suixingpay.datas.manager.service.impl;

import com.suixingpay.datas.common.statistics.TaskPerformance;
import com.suixingpay.datas.manager.core.entity.MrJobTasksMonitor;
import com.suixingpay.datas.manager.core.mapper.MrJobTasksMonitorMapper;
import com.suixingpay.datas.manager.service.MrJobTasksMonitorService;
import com.suixingpay.datas.manager.web.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务泳道实时监控表 服务实现类
 *
 * @author: FairyHood
 * @date: 2018-03-07 13:40:30
 * @version: V1.0-auto
 * @review: FairyHood/2018-03-07 13:40:30
 */
@Service
public class MrJobTasksMonitorServiceImpl implements MrJobTasksMonitorService {

    @Autowired
    private MrJobTasksMonitorMapper mrJobTasksMonitorMapper;

    @Override
    public Integer insert(MrJobTasksMonitor mrJobTasksMonitor) {
        return mrJobTasksMonitorMapper.insert(mrJobTasksMonitor);
    }

    @Override
    public Integer update(Long id, MrJobTasksMonitor mrJobTasksMonitor) {
        return mrJobTasksMonitorMapper.update(id, mrJobTasksMonitor);
    }

    @Override
    public Integer delete(Long id) {
        return mrJobTasksMonitorMapper.delete(id);
    }

    @Override
    public MrJobTasksMonitor selectById(Long id) {
        return mrJobTasksMonitorMapper.selectById(id);
    }

    @Override
    public Page<MrJobTasksMonitor> page(Page<MrJobTasksMonitor> page) {
        Integer total = mrJobTasksMonitorMapper.pageAll(1);
        if (total > 0) {
            page.setTotalItems(total);
            page.setResult(mrJobTasksMonitorMapper.page(page, 1));
        }
        return page;
    }

    @Override
    public void dealTaskPerformance(TaskPerformance performance) {
        
    }
}
