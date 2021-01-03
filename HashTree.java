//用来BFS遍历所有可能的拼图
package game;

import game.*;
import java.util.Arrays;

public class HashTree{
	public int level; //层级数
	public int [][] plate; //对应的拼图
	public HashTree hashNext; //hash冲突用链表法处理
	// public HashTree anct;	  //拼图的上一级，其实也没必要，只用记录到上一级的操作就可以
	// public HashTree[] subTree;//拼图的下一级,只有难度洗牌要用
	public Direction op;		//到上一级所需要的操作
	public Ordinate blank;     //空白拼图坐标

	public HashTree(int[][] plate, int level, Direction op){ //由拼图直接构造，空白拼图需要外部调用指定
		this.plate = plate.clone();
		this.level = level;
		this.op = op;
	}

	public HashTree(int row, int col){ //只给定拼图的行列数
		this.plate = new int[row][col];
	}

	public HashTree(){}

	public HashTree(HashTree src, Direction op){ //src拼图进行一次op操作后的子拼图
		//this.plate = src.plate.clone(); clone()只能进行浅拷贝！！！小心被这个坑死
		this.plate = Algorithm.deepClone(src.plate);
		int newBlankX = src.blank.x+op.reverse().x;
		int newBlankY = src.blank.y+op.reverse().y;

		Algorithm.swap(this.plate, src.blank.x, src.blank.y, newBlankX, newBlankY);

		this.blank = new Ordinate(newBlankX, newBlankY);
		this.op = op.reverse();
		this.level = src.level+1;

	}

}