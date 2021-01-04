//界面设计与主要框架，设置关闭、重置、提示等选项按钮位置布局
package game;

import java.io.*;
import game.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.stream.*;
import javax.imageio.*;
import java.util.*;
import java.applet.*;
import java.net.*;

public class GameFrame extends JFrame{
	//放按键的面板
	public JPanel panel = new JPanel();
	//洗牌按键
	public JButton wanderShuffle = new JButton("随机游走打乱");
	public JButton rotShuffle = new JButton("三轮换打乱");
	public JButton shuffle = new JButton("随机排列打乱");
	public JButton sShuffle = new JButton("有解随机排列打乱");
	//提示按键
	public JButton bHint = new JButton("最短步数算法提示");
	public JButton artHint = new JButton("人工算法提示");
	//bgm
	public AudioClip aau;
	public JButton bPlay = new JButton("播放");
	public JButton bStop = new JButton("暂停");
	public GameFrame(){
		super("拼图游戏");
		panel.setLayout(new FlowLayout());
		panel.add(wanderShuffle);
		panel.add(rotShuffle);
		panel.add(shuffle);
		panel.add(sShuffle);
		panel.add(bHint);
		panel.add(artHint);
		panel.add(bPlay);
		panel.add(bStop);
		//con将游戏面板与按键面板组合
		Container con = this.getContentPane();
		con.add(panel, BorderLayout.NORTH);
		//bgm
		try{
		File tmpF = new File("bgm\\default.wav");
		aau = Applet.newAudioClip(tmpF.toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//新建游戏面板，默认为3x3, 从image中读取图像
		GamePanel gamepane = new GamePanel(4, 4, new File("image\\default.jpg"),new File("image\\background.jpg") );
		con.add(gamepane, BorderLayout.CENTER);
		this.setBounds(10, 10, 900, 750);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//给按钮加监听以实现相应的功能，用匿名类
		wanderShuffle.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){ 
				gamepane.shuffle(1);
			}
		});
		rotShuffle.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){ 
				gamepane.shuffle(0);
			}
		});
		shuffle.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){ 
				gamepane.shuffle(2);
			}
		});
		sShuffle.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){ 
				gamepane.shuffle(3);
			}
		});
		bHint.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){
				gamepane.getShortestPathHint();
			}
		});
		artHint.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){
				gamepane.move(gamepane.getHint());
			}
		});
		bPlay.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){
				aau.loop();
			}
		});
		bStop.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e){
				aau.stop();
			}
		});
	}
}