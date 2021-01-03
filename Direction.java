//cell要移动的方向，(x,y)字段表示相应方向的向量(以左上角为原点建系)
package game;

public enum Direction{
	UP(0, -1), DOWN(0, 1), RIGHT(1, 0), LEFT(-1, 0), NULL(0, 0);

	//用(x, y)字段表示方向向量
	public final int x;
	public final int y;

	private Direction(int vx, int vy){
		x = vx;
		y = vy;
	}

	public static Direction reverse(Direction dir){
		switch (dir){
			case UP: return DOWN;
			case DOWN: return UP;
			case RIGHT: return LEFT;
			case LEFT: return RIGHT;
			default: return NULL;
		}
	}
	
	public static Direction lRot(Direction dir){ //左旋90度(逆时针)
		switch (dir){
			case UP: return LEFT;
			case DOWN: return RIGHT;
			case LEFT: return DOWN;
			case RIGHT: return UP;
			default: return NULL;
		}
	}

	public static Direction xRev(Direction dir){ //关于y轴对称
		switch (dir){
			case UP: return UP;
			case DOWN: return DOWN;
			case LEFT: return RIGHT;
			case RIGHT: return LEFT;
			default: return NULL;
		}
	}

	public Direction xRev(){
		return Direction.xRev(this);
	}

	public Direction lRot(){
		return Direction.lRot(this);
	}

	public Direction reverse(){ //反向
		return Direction.reverse(this);
	}

	public static Direction getDirection(int vx, int vy){//根据坐标差得到方向
		if (vx==0){
			if (vy==-1) 
				return UP;
			if (vy==1)
				return DOWN;
			else return NULL;
		}
		else if (vy==0){
			if (vx==1)
				return RIGHT;
			if (vx==-1)
				return LEFT;
			else return NULL;
		}
		else return NULL;
	}

}