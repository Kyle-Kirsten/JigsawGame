//常用的一些函数
package game;

import game.*;
import java.util.Random;

public class Algorithm{

	public static final int HASH_RANGE = Algorithm.factorial(12)-1;
	public static final int DYNAMIC_HASH_RANGE = Algorithm.factorial(7)-1;

	public static int max(int a, int b){
		return a>b ? a: b;
	}

	public static int min(int a, int b){
		return a<b ? a: b;
	}

	public static int hash(int[][] perm, int range){
		long res = 0;
		for (int[] arr : perm){
			for (int n : arr){
				res = ((res*37)%range+n)%range;
				// System.out.println(res);
			}
		}
		return (int) res;
	}

	public static int factorial(int n){
		int res = 1;
		for (int i=0; i<n; i++){
			res *= (i+1);
		}
		return res;
	}

	public static boolean equals(int[][] arr1, int[][] arr2){
		if (arr1==null||arr2==null||arr1.length!=arr2.length) 
			return false;
		for (int i=0; i<arr1.length; i++){
			if (arr1[i].length!=arr2[i].length) 
				return false;
			for (int j=0; j<arr1[i].length; j++){
				if (arr1[i][j]!=arr2[i][j])
					return false;
			}
		}
		return true;
	}

	public static int[][] deepClone(int[][] src){ //深拷贝
		int[][] dst = new int[src.length][];
		int[] tmp;
		for (int i=0; i<src.length; i++){
			tmp = new int[src[i].length];
			for (int j=0; j<src[i].length; j++){
				tmp[j] = src[i][j];
			}
			dst[i] = tmp;
		}
		return dst;
	}

	public static void swap(int[] array, int i,int j){
		if (i>=array.length||j>=array.length) return;	//角标超出范围时直接返回

		int tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

	public static void swap(Object[] array, int i, int j){ //交换对象数组中的两个对象

		if (i>=array.length||j>=array.length) return;	//角标超出范围时直接返回

		Object tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;

	}

	public static void swap(int[][] array, int x1, int y1, int x2, int y2){ //交换二维整数数组中的两个数

		if (x1<0||x1>=array[0].length||y1<0||y1>=array.length||
			x2<0||x2>=array[0].length||y2<0||y2>=array.length) return; //角标超出范围时直接返回

		int tmp = array[y1][x1];
		array[y1][x1] = array[y2][x2];
		array[y2][x2] = tmp;
	}

	public static int getInverseNumber(int[] array){ //计算总的逆序数，可以用归并写，不过没必要
		int res = 0;
		for (int i=0; i<array.length; i++){
			for (int j=i; j<array.length; j++){
				res += (array[i]>array[j]) ? 1: 0;
			}
		}
		return res;
	}

	public static Direction nextStep(Ordinate chip1, Ordinate chip2, Ordinate blank){	
	//给出2x3拼图为了拼好第一列的下一步
		if (chip1.x>0) { //chip1不在第一列时，先把chip1移回第一列
			if (blank.x==chip1.x) { //chip1与空格同列
				return Direction.RIGHT;
			}
			else if (blank.x>chip1.x) { //空格在chip1右侧
				return (blank.y==chip1.y) ? 
							(blank.y>0) ? Direction.DOWN : Direction.UP
						: Direction.RIGHT;
			}
			else { //空格在chip1左侧
				return (blank.y==chip1.y) ? Direction.LEFT 
						: (blank.y>0) ? Direction.DOWN : Direction.UP;
			}
		}
		else { //chip1在第一列时，分情况讨论
		 	if (chip1.y==0) { //chip1在第一行
		 		if (chip2.x==0) { //chip2也在第一列
		 			return Direction.NULL; //已经拼好第一列
		 		}
		 		else if (chip2.x==1) { //chip2在第二列时，若与chip1顺时针相邻，则直接拼好
		 							   //否则先将chip2移动到第三列，再将chip1移动到第二行
		 							   //再将chip2移动到第二列与chip1顺时针相邻，就可以直接拼好
		 			if (chip2.y==1) { 
		 				if (blank.x==0) {
		 					return Direction.LEFT;
		 				}
		 				else if (blank.x==1) {
		 					return Direction.LEFT;
		 				}
		 				else {
		 					return (blank.y==0) ? Direction.UP : Direction.RIGHT;
		 				}
		 			}
		 			else {
		 				if (blank.x<=1) {
		 					return Direction.LEFT;
		 				}
		 				else {
		 					return (blank.y==0) ? Direction.RIGHT : Direction.DOWN;
		 				}
		 			}
		 		}
		 		else { //chip2在第三列时，先将chip1移动到第二行再操作
		 			if (blank.y==0) {
		 				return Direction.UP;
		 			}
		 			else {
		 				return (blank.x==0) ? Direction.DOWN : Direction.RIGHT;
		 			}
		 		}
		 	}
		 	else { //chip1在第二行
		 		if (chip2.x==0) { //chip2也在第一列时，先把chip2移动到第三列
		 			if (blank.y==0) {
		 				return Direction.RIGHT;
		 			}
		 			else {
		 				return Direction.DOWN;
		 			}
		 		}
		 		else if (chip2.x==1) { //chip2在第二列时，若与chip1顺时针相邻，则直接拼好；否则尝试拼成顺时针相邻
		 			if (chip2.y==1) {
		 				if (blank.y==1) {
		 					return Direction.DOWN;
		 				}
		 				else {
		 					return (blank.x==0) ? Direction.UP : Direction.RIGHT;
		 				}
		 			}
		 			else {
		 				if (blank.x==1) {
		 					return Direction.DOWN;
		 				}
		 				else if (blank.x==0) {
		 					return Direction.UP;
		 				}
		 				else {
		 					return (blank.y==0) ? Direction.UP : Direction.RIGHT;
		 				}
		 			}
		 		}
		 		else { //chip2在第三列时，尝试将chip2移动到与chip1顺时针相邻的位置
		 			if (chip2.y==1) {
		 				if (blank.y==1) {
		 					return Direction.LEFT;
		 				}
		 				else {
		 					return (blank.x==0) ? Direction.LEFT
		 							: (blank.x==1) ? Direction.UP : Direction.RIGHT;
		 				}
		 			}
		 			else {
		 				if (blank.y==0) {
		 					return Direction.LEFT;
		 				}
		 				else {
		 					return Direction.DOWN;
		 				}
		 			}
		 		}
		 	}
		 } 
	}

	public static int getInverseNumber(int[][] array){
		int res = 0;
		int col = array[0].length;
		// System.out.println(col);
		int row = array.length;	
		// System.out.println(row);		
		for (int i=0; i<row; i++){
			for (int j=0; j<col; j++){
				for (int k=i; k<row; k++){
					int l = (k==i) ? (j+1) : 0;
					for (; l<col; l++){
						res += (array[i][j]>array[k][l]) ? 1 : 0;
						// System.out.println(res);
					}
				}
				// System.out.println(res);
			}
		}
		return res;
	}

	public static boolean isSolvable(int[][] array){ //标准情况下的逆序数奇偶性=原情况逆序数奇偶性
													 //						+(交换空白格次数=col-blank.x-1+row-blank.y-1)的奇偶性
		int col = array[0].length;					 //默认数组为0~row*col-1的排列，blank为最大的一项
		int row = array.length;						
		int blankX = 0;
		int blankY = 0;
		int blank = array[0][0];
		int[] tmp = new int[col*row];
		for (int i=0, num=0; i<row; i++){
			for (int j=0; j<col; j++){
				tmp[num++] = array[i][j];
				blankX = (blank<array[i][j]) ? j : blankX;
				blankY = (blank<array[i][j]) ? i : blankY;
				blank  = (blank<array[i][j]) ? array[i][j] : blank;
			}
		}
		int res = (getInverseNumber(tmp)+col-blankX-1+row-blankY-1)%2;
		return res==1 ? false : true;
	}

	public static boolean isSolvable(int[][] array, Ordinate blank){
		int col = array[0].length;
		int row = array.length;
		int res = (getInverseNumber(array)+col-blank.x-1+row-blank.y-1)%2;
		return res==1 ? false : true;
	}

	public static void shuffle(int[] array){//直接随机打乱，不考虑可解性
		Random r = new Random();
		for (int i=0; i<array.length; i++){
			Algorithm.swap(array, i, i+r.nextInt(array.length-i));
		}
	}

	public static void oddShuffle(int[] array){
		Random r = new Random();;
		int state = Algorithm.getInverseNumber(array)%2; //state记录剩余数组应该要交换奇数次还是偶数次
		state = (state==1) ? 0 : 1;
		int tmp;
		for (int i=0; i<array.length-1; i++){
			if (array.length-i==2){//base case
				if (state==1){
					Algorithm.swap(array, i, i+1);
				}
			}else{
				tmp  = r.nextInt(array.length-i);
				if (tmp!=0){//要交换
					Algorithm.swap(array, i, i+tmp);
					state = (state==1) ? 0 : 1;
				}
			}
		}
	}

	public static void evenShuffle(int[] array){
		Random r = new Random();;
		int state = Algorithm.getInverseNumber(array)%2; //state记录剩余数组应该要交换奇数次还是偶数次
		state = (state==1) ? 1 : 0;
		int tmp;
		for (int i=0; i<array.length-1; i++){
			if (array.length-i==2){//base case
				if (state==1){
					Algorithm.swap(array, i, i+1);
				}
			}else{
				tmp  = r.nextInt(array.length-i);
				if (tmp!=0){//要交换
					Algorithm.swap(array, i, i+tmp);
					state = (state==1) ? 0 : 1;
				}
			}
		}
	}

	public static void oddShuffle(int[] array, int range){//对0~range-1奇洗牌，保证洗完后为奇序
		if (range==2&&array[0]<array[1]) { //base case
			Algorithm.swap(array, 0, 1);
		} else if (range>2){
			Random r = new Random();
			Algorithm.swap(array, r.nextInt(range), range-1);
			int cnt = 0;
			for (int i=0; i<range-1; i++){ //统计前面的数关于最后一位数的逆序数
				cnt += array[i]>array[range-1] ? 1 : 0;
			}
			if (cnt%2==1){ //为奇数时取偶洗牌递归，实际上不用递归更简单，直接拿变量记录要奇序还是偶序
				Algorithm.evenShuffle(array, range-1);
			} else{
				Algorithm.oddShuffle(array, range-1);
			}
		}

	}

	public static void evenShuffle(int[] array, int range){//偶洗牌
		if (range==2&&array[0]>array[1]) { //base case
			Algorithm.swap(array, 0, 1);
		} else if (range>2){
			Random r = new Random();
			Algorithm.swap(array, r.nextInt(range), range-1);
			int cnt = 0;
			for (int i=0; i<range-1; i++){ //统计前面的数关于最后一位数的逆序数
				cnt += array[i]>array[range-1] ? 1 : 0;
			}
			if (cnt%2==1){ //为奇数时取奇洗牌递归
				Algorithm.oddShuffle(array, range-1);
			} else{
				Algorithm.evenShuffle(array, range-1);
			}
		}
	}

	public static void rotShuffle(int[] array, int times){//三交换法，times表示交换几次
		if (array.length<3) {
			System.out.println("RotShuffle's array's length should be longer than 2!");
			throw new IllegalArgumentException();
		}
		Random r = new Random();
		for (int i=0; i<times; i++){
			int r1 = r.nextInt(array.length);
			int r2 = r.nextInt(array.length-1);
			int r3;
			if (r2>=r1) {
				r2++;
			} else{
				r3 = r2;
				r2 = r1;
				r1 = r3;
			}
			r3 = r.nextInt(array.length-2);
			if (r3>=r1&&r3<r2-1){
				r3 += 1;
			} else if (r3>=r2-1){
				r3 += 2;
			}
			Algorithm.swap(array, r1, r2);
			Algorithm.swap(array, r2, r3);
		}
	}

	public static void rotShuffle(int[] array){ //默认取[length/3]^2次打乱效果最好
		Algorithm.rotShuffle(array, (int) (array.length/3)*(array.length/3));
	}

	public static void wanderShuffle(int[][] array, Ordinate blank, int times){ //随机游走洗牌
		Random r = new Random();
		Direction[] tmp = new Direction[4];
		int row = array.length;
		int col = array[0].length;
		int range = 0;
		for (int i=0; i<times; i++){
			range = 0;
			if (blank.x>0){ //这里的方向指示的是空白格的移动方向
				tmp[range++] = Direction.LEFT;
			}
			if (blank.x<col-1){
				tmp[range++] = Direction.RIGHT;
			}
			if (blank.y>0){
				tmp[range++] = Direction.UP;
			}
			if (blank.y<row-1){
				tmp[range++] = Direction.DOWN;
			}
			int r1 = r.nextInt(range);
			Algorithm.swap(array, blank.x, blank.y, blank.x+tmp[r1].x, blank.y+tmp[r1].y);
			blank.x += tmp[r1].x;
			blank.y += tmp[r1].y;
		}
	}

	public static void wanderShuffle(int[][] array, Ordinate blank){ //默认1000次
		wanderShuffle(array, blank, 1000);
	}
	
}


