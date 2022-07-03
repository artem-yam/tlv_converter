package com.itresource.converter;

public class ConverterRunner {
    private static final String DEFAULT_INPUT_FILENAME = "data-1.bin";
    private static final String DEFAULT_OUTPUT_FILENAME = "data-1.json";
    private static final String PROJECT_ROOT_PATH = System.getProperty(
        "user.dir");
    
    public static void main(String[] args) {
        String inputFilePath = PROJECT_ROOT_PATH + "/" + DEFAULT_INPUT_FILENAME;
        String outputFilePath =
            PROJECT_ROOT_PATH + "/" + DEFAULT_OUTPUT_FILENAME;
        if (args.length > 1) {
            outputFilePath = PROJECT_ROOT_PATH + "/" + args[1];
        } else if (args.length > 0) {
            inputFilePath = PROJECT_ROOT_PATH + "/" + args[0];
        }
        
        new ConverterProcessor().convert(inputFilePath, outputFilePath);
    }
}
