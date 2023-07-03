package com.Reboot.Minty.utils;

import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

public class OrderByNull extends OrderSpecifier {

    private static final OrderByNull DEFAULT = new OrderByNull();

    private OrderByNull() {
        super(Order.DESC, NullExpression.DEFAULT, NullHandling.Default);
    }

    public static OrderByNull getDefault() {
        return OrderByNull.DEFAULT;
    }
}