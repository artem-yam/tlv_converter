package com.itresource.converter;

import java.util.Arrays;
import java.util.Optional;

public enum TAGS {
    DATE_TIME(1, "dateTime"),
    ORDER_NUMBER(2, "orderNumber"),
    CUSTOMER_NAME(3, "customerName"),
    ITEMS_BLOCK(4, "items"),
    ITEMS_NAME(11, "name"),
    ITEMS_PRICE(12, "price"),
    ITEMS_QUANTITY(13, "quantity"),
    ITEMS_SUM(14, "sum");
    
    private final int number;
    private final String name;
    
    TAGS(int number, String name) {
        this.number = number;
        this.name = name;
    }
    
    public static TAGS getByNumber(int searchNumber) {
        Optional<TAGS> tag = Arrays.stream(TAGS.values()).filter(
            x -> x.number == searchNumber).findFirst();
        
        return tag.orElse(null);
    }
    
    public int getNumber() {
        return number;
    }
    
    public String getName() {
        return name;
    }
}
