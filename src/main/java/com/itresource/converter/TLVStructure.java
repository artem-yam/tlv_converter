package com.itresource.converter;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class TLVStructure {
    List<TLVElement> elements;
    
    public TLVStructure(List<TLVElement> elements) {
        this.elements = elements;
    }
    
    @Override
    public String toString() {
        return "{" + StringUtils.join(elements, ", ") + "}";
    }
}

