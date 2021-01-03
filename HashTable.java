package game;

import game.*;

public class HashTable{
	private HashTree[] table;

	public HashTable(int range){
		table = new HashTree[range];
	}	

	public HashTree search(int[][] state){
		if (state==null) { //找空数组默认返回null
			return null;
		}
		
		int hashCode = Algorithm.hash(state, table.length);
		HashTree  p  = table[hashCode];

		while (p!=null&&!Algorithm.equals(p.plate, state)){
			p = p.hashNext;
		}

		return p;

	}

	public void insert(HashTree ins){
		int hashCode = Algorithm.hash(ins.plate, table.length);

		HashTree  p  = table[hashCode];
		while (p!=null&&!Algorithm.equals(p.plate, ins.plate)){
			p = p.hashNext;
		}
		if (p==null){
			ins.hashNext = table[hashCode];
			table[hashCode] = ins;
		}
	}

	public void delete(int[][] state){
		int hashCode = Algorithm.hash(state, table.length);
		HashTree  p  = new HashTree();
		p.hashNext   = table[hashCode];

		while (p.hashNext!=null&&!Algorithm.equals(p.hashNext.plate, state)){
			p = p.hashNext;
		}

		if (p.hashNext!=null){
			p.hashNext = p.hashNext.hashNext;
		}

	}

	public void clear(){
		for (int i=0; i<table.length; i++){
			table[i] = null;
		}
	}


}
