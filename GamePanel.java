//GamePanel类——功能:	1.初始化Cell数组[row*col](导入图标等)
//				 	2.让Cell响应键盘操作与鼠标点击, 检测是否拼好
//		//3~5主体在Algorithm中实现, GamePanel中只要把ID序列传过去就行
//					3.实现多种shuffle功能(包括难度shuffle，为此需要在初始化时计算以顺序排列为根的hashTable)
//					4.实现人工算法提示
//					5.实现BFS最短路算法提示(可以利用初始化的hashTable，也可以实时地计算以当前状态为根的hashTable)
//					//6.实现DFS找可行解(可选)
//					
package game;

import game.*;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.Arrays;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GamePanel extends JPanel 
						implements MouseListener, KeyListener{
	private int row;
	private int col; //设置行列
	private Cell[] cells; //cells只用于显示，主要计算用ord数组完成
	private Cell origin; //对比原图
	private int cellWidth;
	private int cellHeight;
	private Cell blank; //指向cells的最后一个元素，即空白
	private Ordinate[] ord; //ord[i]表示编号为i的cell的在panel中的顺序坐标
	private int[][] plate; //plate[i][j]表示位于(i, j)的cell编号
	private HashTable hashTable; //用来求最短步数
	private HashQueue hashQueue;  //用来BFS
	private int levelNum;		  //记录每一层级还剩下的未遍历的拼图数
	private HintMode hintMode;
	private HashTree ini;

	public GamePanel(int row, int col, File imageFile, File background){ //初始化主要游戏面板
		this.setLayout(null); //先把布局管理器ban了

		//接下来主要是切割图像与图像到图标的转化
		//还有初始化hashTable和hashQueue
		BufferedImage buf = null;
		this.row = row;
		this.col = col;
		cells = new Cell[row*col];
		ord = new Ordinate[row*col];
		plate = new int[row][col];
		hashTable = new HashTable(Algorithm.HASH_RANGE);
		hashQueue = new HashQueue(Algorithm.HASH_RANGE); 

		//把BFS的根设为顺序排列
		ini = new HashTree(row, col);
		ini.level = 0;
		ini.op    = Direction.NULL;
		ini.blank = new Ordinate(col-1, row-1);
		for (int i=0, num=0; i<row; i++){
			for (int j=0; j<col; j++){
				ini.plate[i][j]  = num;
				ord[num] = new Ordinate(j, i);
				plate[i][j] = num;
				num++;
			}
		}
		hashQueue.enQueue(ini);
		levelNum = 1;
		hashTable.insert(ini);

		//BFS初始化hashTable，用于查找最短路径
		//如果图片数超过10，太大了，则取消生成最短步数拼图的功能，而采用实时计算hashTable的方法
		if (row*col<=10) {
			hintMode = HintMode.STATIC;
			this.staticBFS(null);
		}
		else {
			hintMode = HintMode.DYNAMIC;
		}

		try{
			//从imageFile里读图片
			System.out.println("Image Source:"+imageFile.getAbsolutePath());

			buf = ImageIO.read(imageFile);

			cellWidth = buf.getWidth()/col;
			cellHeight = buf.getHeight()/row;
			Cell.setWidth(cellWidth);
			Cell.setHeight(cellHeight);

		}catch(Exception e){
			e.printStackTrace();
		}

		//初始化cells, num为每个cell的编号,并加上监听
		//用来记录位置信息的ord和plate也初始化一下
		BufferedImage subBuf;
		ImageIcon icon;
		icon = new ImageIcon(buf);
		origin = new Cell(icon, (int) (cellWidth*2.5),(int) (cellHeight*2.5));
		origin.setLocation(cellWidth*col+2, 0);
		this.add(origin);
		for (int i=0, num=0; i<row; i++){
			for (int j=0; j<col; j++){
				subBuf = buf.getSubimage(cellWidth*j, cellHeight*i, cellWidth, cellHeight);
				icon = new ImageIcon(subBuf);
				cells[num] = new Cell(icon, num);
				cells[num].setLocation(cellWidth*j, cellHeight*i);
				this.add(cells[num]);
				cells[num].addMouseListener(this);
				cells[num].addKeyListener(this);
				num++;
			}
		}

		//最后一个cell为背景,不需要监听
		//从background里读图片	
		blank = cells[cells.length-1];
		try{
			buf = ImageIO.read(background);
		} catch(Exception e){
			e.printStackTrace();
		}

		icon = new ImageIcon(buf);

		System.out.println("Background source:"+background.getAbsolutePath());

		blank.setIcon(icon);
		blank.removeMouseListener(this);
		// blank.addKeyListener(this);

	}

	public Direction getHint(){ //人工算法取得state状态下的下一步操作(保证row>1&&col>1)
		//不匹配位置总的可以分成3大类情况: 与右边界距离>=2且与下边界距离>=2、与下边界距离>=2且与右边界<2、与下边界距离<2
		//前几行按行查找第一个不符合的拼图
		//后两行按列查找第一个不符合的拼图
		int id = 0;
		int blankX = ord[blank.getID()].x;
		int blankY = ord[blank.getID()].y;
		int objX;
		int objY;
		for (int i=0, flag=1; i<row-2&&flag==1; i++){
			for (int j=0; j<col; j++, id++){
				if (plate[i][j]!=id) {
					flag = 0;
					break;
				}
			}
		}		
		if (id<(row-2)*col) { //id拼图在前几行中
			// System.out.println("不符合的拼图在前几行中");
			// System.out.printf("id=%d\nblank=(%d, %d)\n", id, blankX, blankY);
			//这时采用直接交换的策略
			objX = id%col;
			objY = id/col;
			if (col-1-objX>=2) { //id拼图在前几列中
				// System.out.println("不符合的拼图在前几列中");
				//按列的排列大小分类
				if (objX>ord[id].x) { //不符拼图在目标位置左侧
					if (blankY==ord[id].y) { //空白格与不符拼图同行
						if (blankX>ord[id].x) { //空白格在不符拼图右侧
							return Direction.RIGHT;
						}
						else { //空白格在不符拼图左侧
							return (blankY-1>objY) ? Direction.DOWN : Direction.UP;
						}
					}
					else if (blankY>ord[id].y) { //空白格在不符拼图下侧
						return (blankX>ord[id].x) ? Direction.DOWN : Direction.LEFT;
					}
					else { //空白格在不符拼图上侧
						return (blankX<ord[id].x) ? Direction.LEFT : Direction.UP;
					}
				}
				else if (objX==ord[id].x) { //不符拼图在目标位置正下侧
					if (blankY==ord[id].y) { //空白格与不符拼图同行
						if (blankX>ord[id].x) { //空白格在不符拼图右侧
							return Direction.DOWN;
						}
						else { //空白格在不符拼图左侧
							return (blankY-1>objY) ? Direction.DOWN : Direction.UP;
						}
					}
					else if (blankY>ord[id].y) { //空白格在不符拼图下侧
						return (blankX<=ord[id].x) ? Direction.LEFT : Direction.DOWN;
					}
					else { //空白格在不符拼图上侧
						if (blankX==ord[id].x) { //空白格与不符拼图同列
							return Direction.UP;
						}
						else { //空白格与不符拼图不同列
							return (blankX>ord[id].x) ? Direction.RIGHT : Direction.LEFT;
						}
					}
				}
				else { //不符拼图在目标位置右侧
					if (blankY==ord[id].y) { //空格与不符拼图同行
						if (blankX<ord[id].x) { //空格在不符拼图左侧
							return Direction.LEFT;
						}
						else { //空格在不符拼图右侧
							return (blankY>objY) ? Direction.DOWN : Direction.UP;
						}
					}
					else if (blankY<ord[id].y) { //空格在不符拼图上侧
						if (blankX==ord[id].x) { //空格与不符拼图同列
							return Direction.UP;
						}
						else { //不同列
							return (blankX>ord[id].x) ? Direction.RIGHT : Direction.LEFT;
						}
					}
					else { //空格在不符拼图下侧
						if (blankX<ord[id].x&&blankX>=objX){
							return Direction.DOWN;
						}
						else if (blankX<objX) {
							return Direction.LEFT;
						}
						else {
							return Direction.RIGHT;
						}
					}
				}
			}
			else { //id拼图在后两列中
				//id1、2分别指代后两列原来的拼图编号
				int id1 = (objX==col-1) ? id-1 : id;
				int id2 = id1+1;
				//先将id1移动到两个目标位置处
				if (ord[id1].x<col-2) { //先移x坐标
					if (blankY>ord[id1].y) {
						return (blankX>ord[id1].x) ? Direction.DOWN : Direction.LEFT;
					}
					else if (blankY==ord[id1].y) {
						return (blankX>ord[id1].x) ? Direction.RIGHT :
								(blankY-1>objY) ? Direction.DOWN : Direction.UP;
					}
					else {
						return (blankX>ord[id1].x) ? Direction.UP : Direction.LEFT;
					}
				}
				else {	//再移y坐标，这里要分情况
					if (ord[id1].y>objY) {
						if (ord[id1].x==col-1) {
							if (blankY>=ord[id1].y){
								return (blankX==ord[id1].x) ? Direction.RIGHT : Direction.DOWN;
							}
							else {
								return (blankX==ord[id1].x) ? Direction.UP : Direction.LEFT;
							}
						}
						else {
							if (blankY>ord[id1].y){
								return (blankX>ord[id1].x) ? Direction.DOWN : Direction.LEFT;
							}
							else if (blankY==ord[id1].y){
								if (blankX>ord[id1].x) {
									return Direction.DOWN;
								}
								else {
									return (blankY-1>objY) ? Direction.DOWN : Direction.UP;
								}
							}
							else {
								if (blankX==ord[id1].x) {
									return Direction.UP;
								}	
								else {
									return (blankX>ord[id1].x) ? Direction.RIGHT : Direction.LEFT;
								}
							}
						}
					}
					else { //再将id2移动到3x2的范围内
						if (ord[id2].x<col-2) { //先移动x坐标，这与id1的移动方式相同
							if (blankY>ord[id2].y) {
								return (blankX>ord[id2].x) ? Direction.DOWN : Direction.LEFT;
							}
							else if (blankY==ord[id2].y) {
								return (blankX>ord[id2].x) ? Direction.RIGHT :
									(blankY-1>objY) ? Direction.DOWN : Direction.UP;
							}
							else {
								return (blankX>ord[id2].x) ? Direction.UP : Direction.LEFT;
							}
						}
						else { //再移动y坐标，与id2的移动方式也差不多
							if (ord[id2].y>objY+2) {
								if (blankY>=ord[id2].y) {
									return (blankX!=ord[id2].x) ? Direction.DOWN :
									 	   (blankX+1<=col-1) ? Direction.LEFT : Direction.RIGHT;
								}
								else {
									return (blankX==ord[id2].x) ? Direction.UP :
										   (blankX>ord[id2].x) ? Direction.RIGHT : Direction.LEFT;
								}
							}
							else { //再移动空格至3x2位置内
								if (blankX<col-2) { //先移动x
									if (blankY==ord[id2].y){
										return (blankY-1>objY) ? Direction.DOWN : Direction.UP;
									}
									else {
										return Direction.LEFT;
									}
								}
								else { 
									if (blankY>objY+2){//再移动y
										if (blankX==ord[id2].x) {
											return (blankX==col-1) ? Direction.RIGHT : Direction.LEFT;
										}
										else {
											return Direction.DOWN;
										}
									}
									else { //再以这3x2的拼图状态判断下一步
										Direction tmpDir = Algorithm.nextStep(
															new Ordinate(ord[id1].y-objY, ord[id1].x-(col-2)),
															new Ordinate(ord[id2].y-objY, ord[id2].x-(col-2)),
															new Ordinate(blankY-objY, blankX-(col-2))
															);
										return tmpDir.xRev().lRot();
									}
								}
							}
						}
					}
				}
				
			}
		}		
		else { //id拼图在后两行中，先按列查找一下不符合的列
			id = blank.getID();
			for (int j=0; j<col; j++){
				if (plate[row-2][j]!=(row-2)*col+j){
					id = (row-2)*col+j;
					break;
				}
				if (plate[row-1][j]!=(row-1)*col+j){
					id = (row-1)*col+j;
					break;
				}
			}
			objX = id%col;
			objY = id/col;
			int id1 = (row-2)*col+objX;
			int id2 = (row-1)*col+objX;
			//若不符合的拼图在前几列
			//则先把id1移动到不符合的列
			if (objX<col-2){ 
				if (ord[id1].x>objX){
					if (blankX<ord[id1].x){
						if (blankY==ord[id1].y){
							return Direction.LEFT;
						}
						else {
							return (blankY>ord[id1].y) ? Direction.DOWN : Direction.UP;
						}
					}
					else {
						if (blankY==ord[id1].y){
							return (blankY==row-1) ? Direction.DOWN : Direction.UP;
						}
						else {
							return Direction.RIGHT;
						}
					}
				}
				else { //再把id2移动到2x3的范围内
					if (ord[id2].x>objX+2){
						if (blankX<ord[id2].x){
							if (blankY==ord[id2].y){
								return Direction.LEFT;
							}
							else {
								return (blankY==row-1) ? Direction.DOWN : Direction.UP;
							}
						}
						else {
							if (blankY==ord[id2].y){
								return (blankY==row-1) ? Direction.DOWN : Direction.UP;
							}
							else {
								return Direction.RIGHT;
							}
						}
					}
					else { //再把空格移动到2x3范围内
						if (blankX>objX+2){
							if (blankY==ord[id2].y){
								return (blankY==row-1) ? Direction.DOWN : Direction.UP;
							}
							else {
								return Direction.RIGHT;
							}
						}
						else { //再调用解2x3拼图的方法
							return Algorithm.nextStep(
									new Ordinate(ord[id1].x-objX, ord[id1].y-(row-2)),
									new Ordinate(ord[id2].x-objX, ord[id2].y-(row-2)),
									new Ordinate(blankX-objX, blankY-(row-2))
									);
						}
					}
				}
			}
			//若不符合的拼图在后两列，直接处理2x2拼图即可(或者偷懒直接调用getShortestPath)
			else if (objX==col-2) {
				if (ord[id1].x==col-2) {
					if (ord[id1].y==row-2){
						if (plate[row-1][col-1]==id2&&plate[row-1][col-2]==blank.getID()) {
							return Direction.LEFT;
						}
						else {
							JOptionPane.showConfirmDialog(null, "无法还原该拼图", "提示", JOptionPane.OK_CANCEL_OPTION);
							return Direction.NULL;
						}
					}
					else {
						if (blankX==col-2) {
							return Direction.UP;
						}
						else {
							return (blankY==row-1) ? Direction.DOWN : Direction.RIGHT;
						}
					}
				}
				else {
					if (blankY==ord[id1].y){
						return Direction.LEFT;
					}
					else {
						if (blankX==col-1){
							return Direction.RIGHT;
						}
						else {
							return (blankY==row-1) ? Direction.DOWN : Direction.UP;
						}
					}
				}
			}
			else {
				if (blankY==row-2) {
					return Direction.UP;
				}
				else {
					return Direction.NULL;
				}
			}
		}

	}

	public void staticBFS(int[][] state){ //BFS查找位于state状态的拼图
		//state为null时由于hashSearch(null)和.equals(null)始终为false，故相当于全部遍历

		HashTree tmp = hashTable.search(state);

		if (tmp==null){ //没找到，继续层级遍历BFS
			boolean isFind = false; //标志有没有找到
			HashTree subState;
			while (!hashQueue.isEmpty()) { //当队列为空或找到时跳出循环
				while (levelNum>0){ //当该层遍历完时跳出，并更新levelNum

					tmp = hashQueue.deQueue(); //出队
					// System.out.println(Arrays.deepToString(tmp.plate)); //测试用
					// System.out.println(tmp.level);

					// if (tmp.level>) //层数太大，会爆内存
						 // return;

					if (tmp.op!=Direction.UP&&tmp.blank.y<row-1){ //回到上一层的操作不为UP时且可以UP时，进行UP操作
						subState = new HashTree(tmp, Direction.UP); //构造tmp经过UP操作的拼图
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){ //找不到时加入hashTable和Queue中
							hashTable.insert(subState);
							hashQueue.enQueue(subState);
						}

					}
					if (tmp.op!=Direction.DOWN&&tmp.blank.y>0){ 
						subState = new HashTree(tmp, Direction.DOWN); 
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){
							hashTable.insert(subState);
							hashQueue.enQueue(subState);
						}
					}
					if (tmp.op!=Direction.LEFT&&tmp.blank.x<col-1){ 
						subState = new HashTree(tmp, Direction.LEFT); 
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){
							hashTable.insert(subState);
							hashQueue.enQueue(subState);
						}
					}
					if (tmp.op!=Direction.RIGHT&&tmp.blank.x>0){ 
						subState = new HashTree(tmp, Direction.RIGHT); 
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){
							hashTable.insert(subState);
							hashQueue.enQueue(subState);
						}
					}

					levelNum--;

					if (isFind){ //找到了就直接返回
						return;
					}
					// return;

				}
				levelNum = hashQueue.length(); //该层遍历结束，更新levelNum
			}

		} 

	}

	public void dynamicBFS(int[][] state){ //BFS查找位于state状态的拼图
										  //只用两个hashTable，只存两层的拼图
		HashTree tmp = hashTable.search(state);

		if (tmp==null){ //没找到，继续层级遍历BFS
			boolean isFind = false; //标志有没有找到
			HashTree subState;
			HashTable nextHashTable = new HashTable(Algorithm.DYNAMIC_HASH_RANGE);
			hashTable = new HashTable(Algorithm.DYNAMIC_HASH_RANGE);
			hashTable.insert(ini);
			hashQueue.clear();
			hashQueue.enQueue(ini);
			levelNum = 1;

			while (!hashQueue.isEmpty()) { //当队列为空或找到时跳出循环
				while (levelNum>0){ //当该层遍历完时跳出，并更新levelNum

					tmp = hashQueue.deQueue(); //出队
					// System.out.println(Arrays.deepToString(tmp.plate)); //测试用
					// System.out.println(tmp.level);
					if (hashQueue.length()>1000000)
						return;	//可能拼图数过多，直接返回

					if (tmp.op!=Direction.UP&&tmp.blank.y<row-1){ //回到上一层的操作不为UP时且可以UP时，进行UP操作
						subState = new HashTree(tmp, Direction.UP); //构造tmp经过UP操作的拼图
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){ //上一层里没有时加入hashTable、nextHashTable和Queue中
							hashTable.insert(subState);
							nextHashTable.insert(subState);
							hashQueue.enQueue(subState);
						}

					}
					if (tmp.op!=Direction.DOWN&&tmp.blank.y>0){ 
						subState = new HashTree(tmp, Direction.DOWN); 
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){
							hashTable.insert(subState);
							nextHashTable.insert(subState);
							hashQueue.enQueue(subState);
						}
					}
					if (tmp.op!=Direction.LEFT&&tmp.blank.x<col-1){ 
						subState = new HashTree(tmp, Direction.LEFT); 
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){
							hashTable.insert(subState);
							nextHashTable.insert(subState);
							hashQueue.enQueue(subState);
						}
					}
					if (tmp.op!=Direction.RIGHT&&tmp.blank.x>0){ 
						subState = new HashTree(tmp, Direction.RIGHT); 
						if (Algorithm.equals(subState.plate, state)){ //找到了就标记一下
							isFind = true;
						}
						if (hashTable.search(subState.plate)==null){
							hashTable.insert(subState);
							nextHashTable.insert(subState);
							hashQueue.enQueue(subState);
						}
					}

					levelNum--;

					if (isFind){ //找到了就直接返回
						return;
					}

				}
				levelNum = hashQueue.length(); //该层遍历结束，更新levelNum和hashTable
				hashTable = nextHashTable;
				nextHashTable = new HashTable(Algorithm.DYNAMIC_HASH_RANGE);
			}

		} 

	}


	public void getShortestPathHint(){ //最短还原方法提示

		if (!Algorithm.isSolvable(plate)){ //无法还原，弹出相应提示
			JOptionPane.showConfirmDialog(null, "无法还原该拼图", "提示", JOptionPane.OK_CANCEL_OPTION);
			return;
		}
		else if (hintMode==HintMode.DYNAMIC){//利用BFS实时更新，找到当前状态就停止
									   		//每次不重置hashTable，所以有爆内存的风险
			this.dynamicBFS(plate);

		}

		//查表并往答案走一步
		HashTree tmp = hashTable.search(plate);
		if (tmp==null){ //查表失败，可能是爆内存了
			JOptionPane.showConfirmDialog(null, "拼图还原步数较多，暴力BFS无能为力", "提示", JOptionPane.OK_CANCEL_OPTION);	
			throw new IndexOutOfBoundsException();
		} else {
			this.move(tmp.op);
		}
		

	} 

	public void flush(){//根据ord[]刷新所有cell的坐标
		for (int i=0; i<cells.length; i++){
			cells[i].setLocation(cellWidth*ord[i].x, cellHeight*ord[i].y);
		}
	}

	public boolean isWin(){//根据ord[]判断是否拼好
		for (int i=0; i<ord.length; i++){
			if (ord[i].x!=i%col||ord[i].y!=i/col){
				return false;
			}
		}
		return true;
	}

	public void shuffle(int shuffleMode){ //采用Algorithm中写的刷新方法，有多种洗牌方法，默认采用三交换法
		int[] perm = new int[row*col-1];
		int[][] ini = new int[row][col];
		for (int i=0; i<row*col; i++){
			if (i<row*col-1) {
				perm[i] = i;
			}
			ini[i/col][i%col] = i;
		}
		switch(shuffleMode){
			case 0://三交换法
				Algorithm.rotShuffle(perm);
				for (int i=0; i<row*col-1; i++){
					plate[i/col][i%col] = perm[i];
					ord[perm[i]].x = i%col;
					ord[perm[i]].y = i/col;
				}
				plate[row-1][col-1] = blank.getID();
				ord[row*col-1].x = col-1;
				ord[row*col-1].y = row-1;
				break;
			case 1://随机游走法
				Algorithm.wanderShuffle(ini, new Ordinate(col-1, row-1));
				for (int i=0; i<row*col; i++){
					plate[i/col][i%col] = ini[i/col][i%col];
					ord[ini[i/col][i%col]].x = i%col;
					ord[ini[i/col][i%col]].y = i/col;
				}
				break;
			case 2://直接刷新
				Algorithm.shuffle(perm);
				for (int i=0; i<row*col-1; i++){
					plate[i/col][i%col] = perm[i];
					ord[perm[i]].x = i%col;
					ord[perm[i]].y = i/col;
				}
				plate[row-1][col-1] = blank.getID();
				ord[row*col-1].x = col-1;
				ord[row*col-1].y = row-1;
				break;
			case 3://保证有解情况下直接刷新
				Algorithm.evenShuffle(perm);
				for (int i=0; i<row*col-1; i++){
					plate[i/col][i%col] = perm[i];
					ord[perm[i]].x = i%col;
					ord[perm[i]].y = i/col;
				}
				plate[row-1][col-1] = blank.getID();
				ord[row*col-1].x = col-1;
				ord[row*col-1].y = row-1;
				break;
			default: break;
		}
		this.flush();
	}

	public void shuffle(){ //默认为随机游走1000次刷新
		this.shuffle(1);
	}

	public void move(Direction dir){ //相当于游戏盘在接收到方向键时的行为
		int blankX = ord[blank.getID()].x;
		int blankY = ord[blank.getID()].y;
		int newBlankX = blankX+dir.reverse().x;
		int newBlankY = blankY+dir.reverse().y;

		if (newBlankX>=0&&newBlankX<col&&newBlankY>=0&&newBlankY<row){
			cells[plate[newBlankY][newBlankX]].move(dir);
			blank.move(dir.reverse());
			Algorithm.swap(ord, plate[newBlankY][newBlankX], plate[blankY][blankX]);
			Algorithm.swap(plate, blankX, blankY, newBlankX, newBlankY);
		}

	}

	@Override
	public void mousePressed(MouseEvent e){//根据发出信号的cell的id判断
		Cell tmp = (Cell) e.getSource();

		int blankX = ord[blank.getID()].x;
		int blankY = ord[blank.getID()].y;
		int cellX  = ord[tmp.getID()].x;
		int cellY  = ord[tmp.getID()].y;
		int gapX   = blankX-cellX;
		int gapY   = blankY-cellY;

		this.move(Direction.getDirection(gapX, gapY));

		if (this.isWin()){
			JOptionPane.showConfirmDialog(null, "恭喜过关", "提示", JOptionPane.OK_CANCEL_OPTION);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e){}
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}

	@Override
	public void keyPressed(KeyEvent e){
		
		// System.out.println(e.getKeyCode());
		//检测不到键盘？？？

		switch (e.getKeyCode()){
			case KeyEvent.VK_UP: 
				this.move(Direction.UP);
				break;
			case KeyEvent.VK_DOWN: 
				this.move(Direction.DOWN);
				break;
			case KeyEvent.VK_LEFT:
				this.move(Direction.LEFT);
				break;
			case KeyEvent.VK_RIGHT:
				this.move(Direction.RIGHT);
				break;
			default: break;
		}

		if (this.isWin()){
			JOptionPane.showConfirmDialog(null, "恭喜过关", "提示", JOptionPane.OK_CANCEL_OPTION);
		}
	}

	@Override
	public void keyReleased(KeyEvent e){}
	@Override
	public void keyTyped(KeyEvent e){}


}


