package com.naumovich.network;

import com.naumovich.domain.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzmitry on 29.4.17.
 */
@Slf4j
public class StatisticsCollector {

    private static final String EACH_NODE_STATS_FILE = "statistics.txt";
    private static final String AVERAGE_STATS_FILE = "statistics2.txt";
    private static final String UTF_8_CODING = "utf-8";

    public static void collectStatistics(List<Node> nodes) {
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        for (Node n : nodes) {
            ArrayList<Integer> row = new ArrayList<>();
            row.add(0, n.getPersNum()); // first column - node's number
            row.add(1, n.getChunkStorage().size()); // second column - number of storing chunks
            row.add(2, n.getAmountOfRetransmitted()); // third column - number of retransmissions made
            list.add(row);
        }
        writeToFile(list, EACH_NODE_STATS_FILE);

        long path = 0;
        long msg = 0;
        long status = 0;
        for (Node n : nodes) {
            path += n.getAmountOfFindingPath();
            msg += n.getAmountOfMsgChecks();
            status += n.getAmountOfNodeStatusChecks();
        }
        writeToFile(new long[]{path / nodes.size(), msg / nodes.size(), status / nodes.size()}, AVERAGE_STATS_FILE);
    }

    private static void writeToFile(ArrayList<ArrayList<Integer>> list, String fileName) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), UTF_8_CODING))) {
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.get(0).size(); j++) {
                    writer.append(list.get(i).get(j).toString() + ";");
                }
                writer.append("\r\n");

            }
        } catch (IOException ex) {
            log.error("Error during writing to the file, fileName = " + fileName);
        }
    }

    private static void writeToFile(long[] array, String fileName) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), UTF_8_CODING))) {
            for (int i = 0; i < array.length; i++) {
                writer.append(String.valueOf(array[i]) + ";");
            }
        } catch (IOException ex) {
            log.error("Error during writing to the file, fileName = " + fileName);
        }
    }
}