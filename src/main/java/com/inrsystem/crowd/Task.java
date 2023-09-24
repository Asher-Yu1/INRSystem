package com.inrsystem.crowd;

import cn.crowdos.kernel.constraint.Constraint;
import cn.crowdos.kernel.resource.SimpleTask;

import java.util.List;

public class Task extends SimpleTask {
    private int taskId;

    public Task(List<Constraint> constraints, cn.crowdos.kernel.resource.Task.TaskDistributionType taskDistributionType) {
        super(constraints, taskDistributionType);
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}