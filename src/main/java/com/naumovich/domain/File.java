package com.naumovich.domain;

import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class File {

    private static Random rand = new Random();
    private static int fileCount = 0;
    private String fileName;
    private long size;

    public static File generateRandom() {
        fileCount++;
        return new File("file " + fileCount, 100 + rand.nextInt(10000));
    }

}
