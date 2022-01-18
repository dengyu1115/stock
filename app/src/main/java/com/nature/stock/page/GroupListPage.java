package com.nature.stock.page;

import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.nature.common.enums.ItemType;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.page.ListPage;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.ViewTemplate;
import com.nature.stock.manager.GroupManager;
import com.nature.stock.model.Group;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupListPage extends ListPage<Group> {

    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private EditText keyword;
    private LinearLayout page;
    private EditText code, name;
    private Button add;
    private final List<ExcelView.D<Group>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Group::getName)),
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, C, CommonUtil.nullsLast(Group::getCode)),
            new ExcelView.D<>("明细", d -> TextUtil.text("跳转"), C, C, this.toItemGroup()),
            new ExcelView.D<>("编辑", d -> "+", C, C, this.edit()),
            new ExcelView.D<>("删除", d -> "-", C, C, this.delete())
    );

    @Override
    protected List<ExcelView.D<Group>> define() {
        return ds;
    }

    @Override
    protected List<Group> listData() {
        List<Group> list = groupManager.list();
        String keyword = this.keyword.getText().toString();
        if (StringUtils.isNotBlank(keyword)) {
            list = list.stream().filter(i -> i.getName().contains(keyword)).collect(Collectors.toList());
        }
        list = list.stream().filter(i -> !ItemType.codes().contains(i.getCode())).collect(Collectors.toList());
        return list;
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(add = template.button("+", 30, 30));
        searchBar.addConditionView(keyword = template.editText(100, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        add.setOnClickListener(v -> this.add());
    }

    private void add() {
        this.makeWindowStructure();
        PopUtil.confirm(context, "新增分组", page, this::doAdd);
    }

    private Consumer<Group> edit() {
        return d -> {
            this.makeWindowStructure();
            this.code.setText(d.getCode());
            this.name.setText(d.getName());
            PopUtil.confirm(context, "编辑分组-" + d.getName(), page, this::doEdit);
        };
    }

    private Consumer<Group> toItemGroup() {
        return d -> this.show(ItemGroupPage.class, d.getCode());
    }

    private void doAdd() {
        this.doEdit((c) -> groupManager.findByCode(c) != null);
    }

    private void doEdit() {
        this.doEdit((c) -> false);
    }

    private void doEdit(Function<String, Boolean> fun) {
        String code = this.code.getText().toString();
        if (code.isEmpty()) {
            throw new RuntimeException("请填写编号");
        }
        if (fun.apply(code)) {
            throw new RuntimeException("分组已存在");
        }
        String name = this.name.getText().toString();
        if (name.isEmpty()) {
            throw new RuntimeException("请填写名称");
        }
        Group group = new Group();
        group.setCode(code);
        group.setName(name);
        groupManager.merge(group);
        this.refreshData();
        PopUtil.alert(context, "编辑成功！");
    }

    private Consumer<Group> delete() {
        return d -> PopUtil.confirm(context, "删除分组-" + d.getName(), "确认删除吗？", () -> {
            groupManager.delete(d.getCode());
            this.refreshData();
            PopUtil.alert(context, "删除成功！");
        });
    }

    private void makeWindowStructure() {
        ViewTemplate template = ViewTemplate.build(context);
        page = template.linearPage();
        page.setGravity(Gravity.CENTER);
        LinearLayout cl = template.line(300, 30);
        LinearLayout nl = template.line(300, 30);
        LinearLayout el = template.line(300, 30);
        cl.addView(template.textView("分组编号：", 100, 30));
        cl.addView(code = template.editText(200, 30));
        nl.addView(template.textView("分组名称：", 100, 30));
        nl.addView(name = template.editText(200, 30));
        page.addView(cl);
        page.addView(nl);
        page.addView(el);
    }


}
