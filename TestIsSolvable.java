package test;

import game.*;

public class TestIsSolvable{
	public static void main(String[] args) {
		int [][] jigsaw = {
			{5, 4, 2},
			{0, 8, 1},
			{6, 3, 7}
		};
		Ordinate blank = new Ordinate(1, 1);
		System.out.println( (Algorithm.isSolvable(jigsaw)) );
		System.out.println(Algorithm.getInverseNumber(jigsaw));
		System.out.println((Algorithm.isSolvable(jigsaw, blank)));

	}
}