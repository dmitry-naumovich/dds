package com.naumovich.util;

import java.util.ArrayList;
import java.util.Random;

import com.naumovich.domain.Node;
import com.naumovich.util.tuple.TwoTuple;

public final class MathOperations {
	
	public static String getRandomHexString(int numchars) {
		Random rand = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(rand.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }
	public static int defineChunksAmount(long size) {
		if (size == 0) return 0;
		else if (size <= 5120) return 8;
		else if (size <= 51200) return 16;
		else if (size <= 512000) return 32;
		else if (size <= 5242880) return 64;
		else if (size <= 52428800) return 128;
		else if (size <= 524288000) return 256;
		else if (size > 524288000) return 512;
		else return 0;
	}
	public static int findXORMetric(String a, String b) {
		if (a.equals(b)) return 0;
		int metrics = 0;
		for (int i = 0; i < a.length(); i++) {
			metrics += a.charAt(i) ^ b.charAt(i);
		}
		//System.out.println(metrics);
		return metrics;
	}
	public static TwoTuple<Node, Integer> findMin(ArrayList<TwoTuple<Node, Integer>> list) {
		Node node = list.get(0).first; int minVal = list.get(0).second;
		for (TwoTuple<Node, Integer> el : list) {
			if (el.second <= minVal) {
				node = el.first;
				minVal = el.second;
			}
		}
		return new TwoTuple<Node, Integer>(node, minVal);
	}
	
}
