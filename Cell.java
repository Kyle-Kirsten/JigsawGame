//Cell类——设计按键的行为: 图标、高度、宽度、移动
//此处不需考虑越界等问题，只要执行相应操作即可
package game;

import game.*;
import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class Cell extends JButton{
	private static int width; //按键的宽高
	private static int height;
	private int id; //按键的编号

	public Cell(Icon icon){
		this.setIcon(icon);
	}

	public Cell(Icon icon, int id){
		this.setIcon(icon);
		this.id = id;
		this.setSize(this.width, this.height);
	}

	public Cell(ImageIcon icon, int width, int height){
		this.setIcon(new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT)));
		this.setSize(width, height);
	}

	public Cell(ImageIcon icon, int id, int width, int height){
		this(icon, width, height);
		this.id = id;
	}

	public Cell(int id, int width, int height){
		this.id = id;
		this.setSize(width, height);
	}

	public Cell(int id){
		this.id = id;
		this.setSize(this.width, this.height);
	}
	
	public Cell(){
		this.setSize(this.width, this.height);
	}

	public void move(Direction dir) //移动
	{
		Rectangle frame = this.getBounds(); //得到Cell的边框坐标信息
		this.setLocation(frame.x+dir.x*width, frame.y+dir.y*height);
	}

	public static void setWidth(int width){
		Cell.width = width;
	}

	

	public static void setHeight(int height){
		Cell.height = height;
	}


	public void setID(int id){
		this.id = id;
	}

	public int getID() {
		return this.id;
	}

	public int getX() {
		return this.getBounds().x;
	}

	public int getY() {
		return this.getBounds().y;
	}
}

