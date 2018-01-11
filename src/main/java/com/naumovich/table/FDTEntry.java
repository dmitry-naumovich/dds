package com.naumovich.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FDTEntry {

    private int orderNum;
    private String chunk;
    private String node;
    private int metric;

}
