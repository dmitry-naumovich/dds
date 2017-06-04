package com.naumovich.util;

import java.util.List;
import java.util.Random;

import com.naumovich.domain.Node;
import com.naumovich.util.tuple.TwoTuple;

public final class MathOperations {

	private static final Random rand = new Random();

	public static String getRandomHexString(int numchars) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
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
		if (a.equals(b)) {
			return 0;
		}
		
		int metrics = 0;
		for (int i = 0; i < a.length(); i++) {
			metrics += a.charAt(i) ^ b.charAt(i);
		}
		return metrics;
	}
	
	public static TwoTuple<String, Integer> findMin(List<TwoTuple<String, Integer>> list) {
		if (list.isEmpty()) {
			return null;
		} else {
			String node = list.get(0).first;
			int minVal = list.get(0).second;

			for (TwoTuple<String, Integer> el : list) {
				if (el.second <= minVal) {
					node = el.first;
					minVal = el.second;
				}
			}
			return new TwoTuple<>(node, minVal);
		}
	}

	public static void printEdgesMatrix(int[][] edgesMatrix) {
		for (int i = 0; i < edgesMatrix.length; i++) {
			for (int j = 0; j < edgesMatrix[i].length; j++) {
				if (i == j) {
					System.out.print("- ");
				}
				else {
					System.out.print(edgesMatrix[i][j] + " ");
				}
			}
			System.out.print("\n");
		}
	}
}
