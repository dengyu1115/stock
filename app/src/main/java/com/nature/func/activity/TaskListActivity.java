package com.nature.func.activity;

import android.widget.EditText;
import com.nature.common.activity.BaseListActivity;
import com.nature.func.manager.TaskManager;
import com.nature.func.manager.WorkdayManager;
import com.nature.func.model.TaskRecord;
import com.nature.common.util.CommonUtil;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务
 * @author nature
 * @version 1.0.0
 * @since 2020/3/7 17:32
 */
public class TaskListActivity extends BaseListActivity<TaskRecord> {

    private static final Map<String, String> TASK_STATUS_MAP = new HashMap<>();

    static {
        TASK_STATUS_MAP.put("0", "初始化");
        TASK_STATUS_MAP.put("1", "成功");
        TASK_STATUS_MAP.put("2", "异常");
    }

    private final TaskManager taskManager = InstanceHolder.get(TaskManager.class);

    private final WorkdayManager workDayManager = InstanceHolder.get(WorkdayManager.class);
    private final List<ExcelView.D<TaskRecord>> ds = Arrays.asList(
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, S, CommonUtil.nullsLast(TaskRecord::getCode)),
            new ExcelView.D<>("日期", d -> TextUtil.text(d.getDate()), C, C, CommonUtil.nullsLast(TaskRecord::getDate)),
            new ExcelView.D<>("开始时间", d -> TextUtil.text(d.getStartTime()), C, C, CommonUtil.nullsLast(TaskRecord::getStartTime)),
            new ExcelView.D<>("结束时间", d -> TextUtil.text(d.getEndTime()), C, C, CommonUtil.nullsLast(TaskRecord::getEndTime)),
            new ExcelView.D<>("执行状态", d -> TextUtil.text(TASK_STATUS_MAP.get(d.getStatus())), C, C, CommonUtil.nullsLast(TaskRecord::getStatus)),
            new ExcelView.D<>("异常", d -> TextUtil.text(d.getException()), C, C, CommonUtil.nullsLast(TaskRecord::getException))
    );
    private Selector<String> selector;
    private EditText editText;

    @Override
    protected List<ExcelView.D<TaskRecord>> define() {
        return ds;
    }

    @Override
    protected List<TaskRecord> listData() {
        List<TaskRecord> records = taskManager.records(this.selector.getValue());
        String keyWord = this.editText.getText().toString();
        if (StringUtils.isBlank(keyWord)) return records;
        return records.stream().filter(d -> d.getCode().contains(keyWord)).collect(Collectors.toList());
    }


    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(selector = template.selector(100, 30));
        searchBar.addConditionView(editText = template.editText(100, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        selector.mapper(s -> s).init().refreshData(workDayManager.list(workDayManager.getToday()));
    }

}
