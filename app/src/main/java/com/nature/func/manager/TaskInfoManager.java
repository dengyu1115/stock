package com.nature.func.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.func.mapper.TaskInfoMapper;
import com.nature.func.model.TaskInfo;
import com.nature.common.util.TaskHolder;

import java.util.List;

/**
 * 任务信息
 * @author nature
 * @version 1.0.0
 * @since 2020/12/6 14:42
 */
public class TaskInfoManager {

    @Injection
    private TaskInfoMapper taskInfoMapper;

    public List<TaskInfo> listAll() {
        return TaskHolder.listAll();
    }

    public List<TaskInfo> list() {
        return taskInfoMapper.list();
    }

    public int merge(TaskInfo d) {
        return taskInfoMapper.merge(d);
    }

    public int delete(String code, String startTime) {
        return taskInfoMapper.delete(code, startTime);
    }
}
