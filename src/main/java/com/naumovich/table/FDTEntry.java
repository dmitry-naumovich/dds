package com.naumovich.table;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by dzmitry on 4.5.17.
 */
@Data
@AllArgsConstructor
public class FDTEntry {

    private int orderNum;
    private String chunk;
    private String node;
    private int metric;

}
