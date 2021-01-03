package test;

import game.*;
import java.util.Arrays;

public class TestShuffle{
	public static void main(String[] args) {
		int[] array = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		System.out.println(Arrays.toString(array));
		Algorithm.shuffle(array);
		System.out.println(Arrays.toString(array));
		System.out.println(Algorithm.getInverseNumber(array));
		array = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		System.out.println(Arrays.toString(array));
		Algorithm.rotShuffle(array);
		System.out.println(Arrays.toString(array));
		System.out.println(Algorithm.getInverseNumber(array));
		array = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		System.out.println(Arrays.toString(array));
		Algorithm.evenShuffle(array, array.length);
		System.out.println(Arrays.toString(array));
		System.out.println(Algorithm.getInverseNumber(array));
		array = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		System.out.println(Arrays.toString(array));
		Algorithm.oddShuffle(array);
		System.out.println(Arrays.toString(array));
		System.out.println(Algorithm.getInverseNumber(array));
		int [][] jigsaw = {
			{0, 1, 2},
			{3, 4, 5},
			{6, 7, 8}
		};
		Ordinate blank = new Ordinate(jigsaw[0].length-1, jigsaw.length-1);
		System.out.println(Arrays.deepToString(jigsaw));
		Algorithm.wanderShuffle(jigsaw, blank);
		System.out.println(Arrays.deepToString(jigsaw));
		System.out.println(Algorithm.isSolvable(jigsaw, blank));
	}
}