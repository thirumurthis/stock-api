package com.stock.finance.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.model.InvestedStockFromDataSource;

/*
 * NOT USED AS OF NOW, if we have a json file with the input stocks
 * This class will be able to read the data from it
 */
//@Service
public class DataInputFromFileServiceImpl implements UserStockInputDataService{

    static ObjectMapper JSONPARSER = new ObjectMapper();
    
    public List<InvestedStockFromDataSource> getInputAsList(InvestedStockFromDataSource[] inputStockInfo){
    	return Arrays.asList(inputStockInfo).stream().collect(Collectors.toList());
    }
    
	public InvestedStockFromDataSource[] readFromInputJson(String inputFile) throws IOException {
		InvestedStockFromDataSource[] inputDetails = null;
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(inputFile);
        if (inputStream != null) {
            InputStreamReader inReader = new InputStreamReader(inputStream);
            try {
                inputDetails = JSONPARSER.readValue(inReader, InvestedStockFromDataSource[].class);
            } catch (IOException e) {
                System.err.println("FinanceSupport.readFromInputJson() : Exception - " + e.getMessage());
                throw new IOException(e);
            }
        } else {
            System.err.println("Error in reading the file");
            throw new IOException("FinanceSupport.readFromInputJson() : Exception - inputStreamReader is null, error in reading file ");
        }
        return inputDetails;
    }

}
