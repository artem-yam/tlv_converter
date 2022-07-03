package com.itresource.converter;

import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ConverterProcessor {
    
    private int tagBytesCount = 2;
    private int lengthBytesCount = 2;
    
    public ConverterProcessor() {
    }
    
    public ConverterProcessor(int tagBytesCount, int lengthBytesCount) {
        this.tagBytesCount = tagBytesCount;
        this.lengthBytesCount = lengthBytesCount;
    }
    
    public void convert(String inputFilePath, String outputFilePath) {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        
        try {
            FileInputStream fis = new FileInputStream(inputFile);
            
            List<TLVElement> elements = readInputStream(fis);
            
            mergeItemsBlocks(elements);
            
            TLVStructure resultStructure = new TLVStructure(elements);
            
            System.out.println(resultStructure);
            
            FileWriter fw = new FileWriter(outputFile);
            fw.write(resultStructure.toString());
            fw.close();
        } catch (FileNotFoundException e) {
            throw new ConvertException("Файл не найден", e);
        } catch (ClassCastException e) {
            throw new ConvertException(
                "Ошибка при склеивании нескольких блоков items", e);
        } catch (IOException e) {
            throw new ConvertException("Ошибка записи в файл", e);
        }
        
    }
    
    public List<TLVElement> readInputStream(InputStream is) {
        List<TLVElement> elements = new ArrayList<>();
        
        try {
            while (is.available() > tagBytesCount + lengthBytesCount) {
                
                // Читаем байты тега
                byte[] tagBytes = new byte[tagBytesCount];
                int readerBytesCount = is.read(tagBytes);
                if (readerBytesCount != tagBytesCount) {
                    throw new ConvertException("Не удалось считать байты тега");
                }
                
                // Читаем байты длины
                byte[] lengthBytes = new byte[lengthBytesCount];
                readerBytesCount = is.read(lengthBytes);
                if (readerBytesCount != lengthBytesCount) {
                    throw new ConvertException(
                        "Не удалось считать байты длины");
                }
                
                byte[] valueBytes = new byte[lengthBytes[0]];
                readerBytesCount = is.read(valueBytes);
                if (readerBytesCount != lengthBytes[0]) {
                    throw new ConvertException(
                        "Не удалось считать байты длины");
                }
                
                TLVElement element = new TLVElement(
                    TAGS.getByNumber(tagBytes[0]), lengthBytes[0], valueBytes);
                
                correctValueByTag(element);

                elements.add(element);
            }
        } catch (IOException e) {
            throw new ConvertException("Ошибка чтения из файла", e);
        }
        
        return elements;
    }
    
    private void correctValueByTag(TLVElement element) {
        TAGS tag = element.getTag();
        byte[] valueBytes = (byte[]) element.getValue();
        
        try {
            switch (tag) {
                case DATE_TIME:
                    ArrayUtils.reverse(valueBytes);
                    Date date = new Date(
                        new BigInteger(valueBytes).intValue() * 1000L);
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String dateString = dateFormat.format(date);
                    
                    element.setValue(dateString);
                    break;
                case ORDER_NUMBER:
                case ITEMS_PRICE:
                case ITEMS_SUM:
                    List<Byte> tempList = new ArrayList<>();
                    for (Byte someByte : valueBytes) {
                        tempList.add(someByte);
                    }
                    
                    while ((tempList.size() % 4) != 0) {
                        tempList.add((byte) 0);
                    }
                    
                    valueBytes = ArrayUtils.toPrimitive(
                        tempList.toArray(new Byte[0]));
                    ArrayUtils.reverse(valueBytes);
                    
                    int intNumber = new BigInteger(valueBytes).intValue();
                    
                    element.setValue(intNumber);
                    break;
                case CUSTOMER_NAME:
                case ITEMS_NAME:
                    
                    String stringValue = new String(valueBytes,
                        Charset.forName("CP866"));
                    
                    element.setValue(stringValue);
                    break;
                
                case ITEMS_QUANTITY:
                    
                    tempList = new ArrayList<>();
                    for (Byte someByte : valueBytes) {
                        tempList.add(someByte);
                    }
                    
                    byte pointPosition = tempList.remove(0);
                    
                    do {
                        tempList.add(0, (byte) 0);
                    } while ((tempList.size() % 4) != 0);
                    
                    valueBytes = ArrayUtils.toPrimitive(
                        tempList.toArray(new Byte[0]));

                    float floatNumber = new BigDecimal(
                        new BigInteger(valueBytes), pointPosition).floatValue();

                    element.setValue(floatNumber);
                    
                    break;
                case ITEMS_BLOCK:
                    ByteArrayInputStream bais = new ByteArrayInputStream(
                        valueBytes);
                    
                    List<TLVStructure> itemsBlocksList = new ArrayList<>();
                    itemsBlocksList.add(
                        new TLVStructure(readInputStream(bais)));
                    element.setValue(itemsBlocksList);
                    
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new ConvertException(
                "Ошибка при преобразовании значения к нужному типу", e);
        }
    }
    
    private void mergeItemsBlocks(List<TLVElement> elements)
        throws ClassCastException {
        List<TLVElement> itemsBlocksElements = new ArrayList<>();
        for (TLVElement elem : elements) {
            if (elem.getTag() == TAGS.ITEMS_BLOCK) {
                itemsBlocksElements.add(elem);
            }
        }
        if (!itemsBlocksElements.isEmpty()) {
            TLVElement firstItemsBlockElem = itemsBlocksElements.get(0);
            itemsBlocksElements.remove(firstItemsBlockElem);
            
            for (TLVElement itemsBlockElem : itemsBlocksElements) {
                ((List<TLVStructure>) firstItemsBlockElem.getValue()).add(
                    ((List<TLVStructure>) itemsBlockElem.getValue()).get(0));
            }
            
            elements.removeAll(itemsBlocksElements);
        }
        
    }
    
}
