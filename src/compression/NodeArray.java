package compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class NodeArray {
	private int[] weight = new int[256];

	public NodeArray() {

	}

	//从文件中读入bytes，并统计频率
	public ArrayList<Node> getNodes(File f) {
		HashMap<Byte,Integer> h=new HashMap();
		try {
			FileInputStream in = new FileInputStream(f);
			byte b[]=new byte[in.available()];
			in.read(b);
			for(int i=0;i<b.length;i++){
				if(h.containsKey(b[i])){
					h.replace(b[i], h.get(b[i])+1);
				}else{
					Integer inte=new Integer(1);
					h.put(b[i], inte);
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//将HashMap转为ArrayList输出
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(Entry<Byte,Integer> entry : h.entrySet())
			nodes.add(new Node(entry.getKey(),entry.getValue()));
		return nodes;
	}
}
