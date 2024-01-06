package org.nature.biz.page;

import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import org.apache.commons.lang3.StringUtils;
import org.nature.biz.manager.*;
import org.nature.biz.model.Group;
import org.nature.biz.model.Item;
import org.nature.common.ioc.annotation.Injection;
import org.nature.common.ioc.annotation.PageView;
import org.nature.common.page.ListPage;
import org.nature.common.util.ClickUtil;
import org.nature.common.util.CommonUtil;
import org.nature.common.util.PopUtil;
import org.nature.common.util.TextUtil;
import org.nature.common.view.ExcelView;
import org.nature.common.view.SearchBar;
import org.nature.common.view.Selector;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 项目维护
 * @author Nature
 * @version 1.0.0
 * @since 2023/12/29
 */
@PageView(name = "项目", group = "ETF", col = 1, row = 2)
public class ItemListPage extends ListPage<Item> {

    @Injection
    private ItemManager itemManager;
    @Injection
    private GroupManager groupManager;
    @Injection
    private KlineManager klineManager;
    @Injection
    private RuleManager ruleManager;
    @Injection
    private HoldManager holdManager;

    /**
     * 项目分组
     */
    private Selector<Group> itemGroup;
    /**
     * 关键字
     */
    private EditText keyword;
    /**
     * 编辑弹窗
     */
    private LinearLayout editPop;
    /**
     * 编号、名称
     */
    private EditText code, name;
    /**
     * 类型下拉选项
     */
    private Selector<String> type;
    /**
     * 分组下拉选项
     */
    private Selector<Group> group;
    /**
     * 新增、加载K线、重新加载K线、计算规则按钮
     */
    private Button add, loadKline, reloadKline, calcRule;
    /**
     * 分组信息map
     */
    private Map<String, String> groupMap;
    /**
     * 表头
     */
    private final List<ExcelView.D<Item>> ds = Arrays.asList(
            ExcelView.row("", C, Arrays.asList(
                    ExcelView.row("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Item::getName)),
                    ExcelView.row("编号", d -> TextUtil.text(d.getCode()), C, C, CommonUtil.nullsLast(Item::getCode)))
            ),
            ExcelView.row("类型", d -> TextUtil.text(d.getType()), C, C, CommonUtil.nullsLast(Item::getType)),
            ExcelView.row("分组", d -> TextUtil.text(groupMap.get(d.getGroup())), C, C, CommonUtil.nullsLast(d -> groupMap.get(d.getGroup()))),
            ExcelView.row("编辑", d -> "+", C, C, this.edit()),
            ExcelView.row("删除", d -> "-", C, C, this.delete()),
            ExcelView.row("K线加载", d -> "加载", C, C, this.loadKline()),
            ExcelView.row("K线重载", d -> "加载", C, C, this.reloadKline()),
            ExcelView.row("K线查看", d -> "查看", C, C, this.showKline()),
            ExcelView.row("规则查看", d -> "查看", C, C, this.showRule())
    );

    @Override
    protected List<ExcelView.D<Item>> define() {
        return ds;
    }

    @Override
    protected List<Item> listData() {
        List<Item> list = itemManager.listAll();
        String keyword = this.keyword.getText().toString();
        if (StringUtils.isNotBlank(keyword)) {
            list = list.stream().filter(i -> i.getName().contains(keyword)).collect(Collectors.toList());
        }
        Group group = this.itemGroup.getValue();
        if (StringUtils.isNotBlank(group.getCode())) {
            list = list.stream().filter(i -> group.getCode().equals(i.getGroup())).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(add = template.button("+", 30, 30));
        searchBar.addConditionView(itemGroup = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(100, 30));
        searchBar.addConditionView(loadKline = template.button("K线加载", 80, 30));
        searchBar.addConditionView(reloadKline = template.button("K线重载", 80, 30));
        searchBar.addConditionView(calcRule = template.button("规则计算", 80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        add.setOnClickListener(v -> this.add());
        ClickUtil.onAsyncClick(loadKline, this::loadKlineAll);
        ClickUtil.onAsyncClick(reloadKline, this::reloadKlineAll);
        ClickUtil.onAsyncClick(calcRule, this::calcHold);
        List<Group> groups = groupManager.listAll();
        groupMap = groups.stream().collect(Collectors.toMap(Group::getCode, Group::getName));
        groups.sort(Comparator.comparing(Group::getCode));
        groups.add(0, new Group());
        itemGroup.mapper(Group::getName).init().refreshData(groups);
    }

    @Override
    protected int getExcelColumns() {
        return 10;
    }

    /**
     * 添加
     */
    private void add() {
        this.makeWindowStructure();
        PopUtil.confirm(context, "新增项目", editPop, () -> this.doEdit(itemManager::save));
    }

    /**
     * 编辑
     * @return 编辑操作
     */
    private Consumer<Item> edit() {
        return d -> {
            this.makeWindowStructure();
            this.code.setText(d.getCode());
            this.name.setText(d.getName());
            this.type.setValue(d.getType());
            PopUtil.confirm(context, "编辑项目-" + d.getName(), editPop, () -> this.doEdit(itemManager::edit));
        };
    }

    /**
     * 编辑操作
     * @param consumer 编辑逻辑
     */
    private void doEdit(Consumer<Item> consumer) {
        String code = this.code.getText().toString();
        if (code.isEmpty()) {
            throw new RuntimeException("请填写编号");
        }
        String name = this.name.getText().toString();
        if (name.isEmpty()) {
            throw new RuntimeException("请填写名称");
        }
        String type = this.type.getValue();
        if (type.isEmpty()) {
            throw new RuntimeException("请选择类型");
        }
        Group group = this.group.getValue();
        if (group == null) {
            throw new RuntimeException("请选择分组");
        }
        Item item = new Item();
        item.setCode(code);
        item.setName(name);
        item.setType(type);
        item.setGroup(group.getCode());
        consumer.accept(item);
        this.refreshData();
        PopUtil.alert(context, "编辑成功！");
    }

    /**
     * 删除操作
     * @return 删除操作
     */
    private Consumer<Item> delete() {
        return d -> PopUtil.confirm(context, "删除项目-" + d.getName(), "确认删除吗？", () -> {
            itemManager.delete(d);
            this.refreshData();
            PopUtil.alert(context, "删除成功！");
        });
    }

    /**
     * 加载K线
     * @return 加载K线操作
     */
    private Consumer<Item> loadKline() {
        return d -> PopUtil.alert(context, "K线加载完成，数据量：" + klineManager.loadByItem(d));
    }

    /**
     * 重载K线
     * @return 重载K线操作
     */
    private Consumer<Item> reloadKline() {
        return d -> PopUtil.alert(context, "K线重载完成，数据量：" + klineManager.reloadByItem(d));
    }

    /**
     * 加载全部K线
     * @return 提示信息
     */
    private String loadKlineAll() {
        return "所有K线加载完成，数据量：" + klineManager.load();
    }

    /**
     * 重载全部K线
     * @return 提示信息
     */
    private String reloadKlineAll() {
        return "所有K线重载完成，数据量：" + klineManager.reload();
    }

    /**
     * 计算持仓数据
     * @return 提示信息
     */
    private String calcHold() {
        return "计算完成，数据量：" + holdManager.calc();
    }

    /**
     * 显示K线
     * @return 显示K线操作
     */
    private Consumer<Item> showKline() {
        return d -> this.show(KlineListPage.class, d);
    }

    /**
     * 显示规则
     * @return 显示规则操作
     */
    private Consumer<Item> showRule() {
        return d -> this.show(RuleListPage.class, d);
    }

    /**
     * 构建弹窗结构
     */
    private void makeWindowStructure() {
        editPop = template.linearPage();
        editPop.setGravity(Gravity.CENTER);
        LinearLayout cl = template.line(300, 30);
        LinearLayout nl = template.line(300, 30);
        LinearLayout tl = template.line(300, 30);
        LinearLayout gl = template.line(300, 30);
        LinearLayout el = template.line(300, 30);
        cl.addView(template.textView("编号：", 100, 30));
        cl.addView(code = template.editText(200, 30));
        nl.addView(template.textView("名称：", 100, 30));
        nl.addView(name = template.editText(200, 30));
        tl.addView(template.textView("类型：", 100, 30));
        tl.addView(type = template.selector(200, 30));
        gl.addView(template.textView("分组：", 100, 30));
        gl.addView(group = template.selector(200, 30));
        type.mapper(i -> i).init().refreshData(Arrays.asList("0", "1"));
        group.mapper(Group::getName).init().refreshData(groupManager.listAll());
        editPop.addView(cl);
        editPop.addView(nl);
        editPop.addView(tl);
        editPop.addView(gl);
        editPop.addView(el);
    }

}
