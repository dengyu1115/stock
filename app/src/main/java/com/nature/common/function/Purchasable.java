package com.nature.common.function;

import com.nature.item.model.Item;

/**
 * 可购买的
 * @author nature
 * @version 1.0.0
 * @since 2020/12/19 14:19
 */
public interface Purchasable {

    String getCode();

    String getName();

    boolean pass(Item item, double price);
}
