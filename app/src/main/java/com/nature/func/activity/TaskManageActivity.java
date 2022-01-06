package com.nature.func.activity;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.nature.common.activity.BaseListActivity;
import com.nature.common.enums.TaskStatus;
import com.nature.common.enums.TaskType;
import com.nature.func.manager.TaskInfoManager;
import com.nature.func.model.TaskInfo;
import com.nature.common.util.CommonUtil;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 任务管理
 * @author nature
 * @version 1.0.0
 * @since 2020/12/6 12:43
 */
public class TaskManageActivity extends BaseListActivity<TaskInfo> {

    private final TaskInfoManager taskInfoManager = InstanceHolder.get(TaskInfoManager.class);
    private Selector<TaskInfo> taskSel;
    private Selector<String> typeSel, statusSel;
    private final List<ExcelView.D<TaskInfo>> ds = Arrays.asList(
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(TaskInfo::getCode)),
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, C, CommonUtil.nullsLast(TaskInfo::getName)),
            new ExcelView.D<>("类型", d -> TextUtil.text(d.getType()), C, C, CommonUtil.nullsLast(TaskInfo::getType)),
            new ExcelView.D<>("执行开始", d -> TextUtil.text(d.getStartTime()), C, C, CommonUtil.nullsLast(TaskInfo::getStartTime)),
            new ExcelView.D<>("执行结束", d -> TextUtil.text(d.getEndTime()), C, C, CommonUtil.nullsLast(TaskInfo::getEndTime)),
            new ExcelView.D<>("状态", d -> TextUtil.text(d.getStatus()), C, C, CommonUtil.nullsLast(TaskInfo::getStatus)),
            new ExcelView.D<>("修改", d -> TextUtil.text("+"), C, C, this.edit()),
            new ExcelView.D<>("删除", d -> TextUtil.text("-"), C, C, this.delete())
    );
    private Button add, startTime, endTime;

    @Override
    protected List<ExcelView.D<TaskInfo>> define() {
        return ds;
    }

    @Override
    protected List<TaskInfo> listData() {
        return taskInfoManager.list();
    }


    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(add = template.button("+", 30, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        add.setOnClickListener(v -> PopUtil.confirm(context, "添加任务", this.popAddWindow(), this::doSave));
    }

    private View popAddWindow() {
        LinearLayout layout = template.block(800, 1200);
        //layout.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        layout.addView(this.line("任务", taskSel = template.selector(150, 30)));
        layout.addView(this.line("类型", typeSel = template.selector(150, 30)));
        layout.addView(this.line("时间", startTime = template.button("00:00:00", 75, 30),
                endTime = template.button("00:00:00", 75, 30)));
        layout.addView(this.line("状态", statusSel = template.selector(150, 30)));
        List<TaskInfo> items = taskInfoManager.listAll();
        taskSel.mapper(TaskInfo::getName).init().refreshData(items);
        typeSel.mapper(TaskType::codeToName).init().refreshData(TaskType.codes());
        statusSel.mapper(TaskStatus::codeToName).init().refreshData(TaskStatus.codes());
        startTime.setOnClickListener(v -> template.timePiker(startTime));
        endTime.setOnClickListener(v -> template.timePiker(endTime));
        return layout;
    }

    @SuppressLint("RtlHardcoded")
    private LinearLayout line(String title, View... views) {
        LinearLayout line = template.line(300, 30);
        line.addView(template.textView(title, 80, 30));
        for (View view : views) {
            line.addView(view);
        }
        line.setGravity(Gravity.LEFT);
        return line;
    }

    private Consumer<TaskInfo> edit() {
        return d -> {
            View view = this.popAddWindow();
            taskSel.setValue(d);
            typeSel.setValue(d.getType());
            statusSel.setValue(d.getStatus());
            startTime.setText(d.getStartTime());
            endTime.setText(d.getEndTime());
            PopUtil.confirm(context, "修改任务", view, this::doSave);
        };
    }

    private Consumer<TaskInfo> delete() {
        return d -> PopUtil.confirm(context, "删除任务", "确定删除吗？", () -> {
            taskInfoManager.delete(d.getCode(), d.getStartTime());
            PopUtil.alert(context, "删除成功");
            this.refreshData();
        });
    }

    private void doSave() {
        TaskInfo taskInfo = taskSel.getValue();
        if (taskInfo == null) {
            throw new RuntimeException("任务不可为空");
        }
        String type = typeSel.getValue();
        if (StringUtils.isBlank(type)) {
            throw new RuntimeException("请选择类型");
        }
        String status = statusSel.getValue();
        if (StringUtils.isBlank(status)) {
            throw new RuntimeException("请选择状态");
        }
        String timeStart = startTime.getText().toString();
        String timeEnd = endTime.getText().toString();
        if (timeStart.compareTo(timeEnd) >= 0) {
            throw new RuntimeException("结束时间必须大于开始时间");
        }
        TaskInfo ti = new TaskInfo();
        ti.setCode(taskInfo.getCode());
        ti.setName(taskInfo.getName());
        ti.setType(type);
        ti.setStartTime(timeStart);
        ti.setEndTime(timeEnd);
        ti.setStatus(status);
        taskInfoManager.merge(ti);
        PopUtil.alert(context, "保存成功");
        this.refreshData();
    }

}
