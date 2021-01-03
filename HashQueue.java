//普通循环队列，实现层级队列要用外部的计数器来控制
package game;

import game.*;

public class HashQueue{
	private HashTree[] queue;
	private int front;
	private int rear;
	private int count;

	public HashQueue(int range){
		queue = new HashTree[range];
		front = 0;
		rear  = -1;
		count = 0;
	}

	public boolean isEmpty(){
		return count==0 ? true : false;
	}

	public boolean isFull(){
		return count==queue.length ? true : false;
	}

	public void enQueue(HashTree in){
		if (this.isFull()) {
			System.out.println("HashQueue is Full!");
			throw new IndexOutOfBoundsException();
		}
		rear = (rear+1)%queue.length;
		queue[rear] = in;
		count++;
	}

	public HashTree deQueue(){
		if (this.isEmpty()) {
			System.out.println("HashQueue is Empty!");
			throw new IndexOutOfBoundsException();
		}
		int tmp = front;
		front = (front+1)%queue.length;
		count--;
		return queue[tmp];
	}

	public int length(){
		return count;
	}
	
	public void clear(){
		front = 0;
		rear  = -1;
		count = 0;
	}
}