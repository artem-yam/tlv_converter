package com.itresource.converter;

public class TLVElement {
    private final TAGS tag;
    private final int length;
    private Object value;

    public TLVElement(TAGS tag, int length, Object value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }
    
    public TAGS getTag() {
        return tag;
    }
    
    public int getLength() {
        return length;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        Object valueToPrint = value;
        if (value instanceof Double || value instanceof Float) {
            
            Double doubleValue = new Double(valueToPrint.toString());
            if (doubleValue == doubleValue.longValue()) {
                valueToPrint = doubleValue.longValue();
            }
        }
        
        String outputString;
        if (valueToPrint instanceof String) {
            outputString = String.format("\"%s\":\"%s\"", tag.getName(),
                valueToPrint);
        } else {
            outputString = String.format("\"%s\":%s", tag.getName(),
                valueToPrint);
        }
        
        return outputString;
    }
}
