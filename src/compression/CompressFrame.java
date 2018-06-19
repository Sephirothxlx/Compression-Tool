package compression;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class CompressFrame extends JFrame {
	private JButton b0 = new JButton("select");
	private JButton b1 = new JButton("compress");
	private JButton b2 = new JButton("decompress");
	private JLabel l0 = new JLabel("压缩对象:");
	private JTextField t0 = new JTextField();

	public CompressFrame() {
		//构建各个组件，设计frame
		this.setLayout(null);
		this.setTitle("Compression");
		this.setSize(400, 200);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.add(l0);
		l0.setBounds(30, 60, 100, 20);
		this.add(t0);
		t0.setEnabled(false);
		t0.setBounds(100, 60, 260, 20);
		this.add(b0);
		b0.setBounds(30, 120, 80, 20);
		this.add(b1);
		b1.setBounds(150, 120, 80, 20);
		this.add(b2);
		b2.setBounds(280, 120, 80, 20);
		b0.addActionListener(new l0());
		b1.addActionListener(new l1());
		b2.addActionListener(new l2());
	}

	//select的listener
	public class l0 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.showOpenDialog(null);
				t0.setText(chooser.getSelectedFile().toString());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "请先选择文件。");
			}
		}
	}

	//compress的listener
	public class l1 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = t0.getText();
			File f = new File(s);
			if (f.isDirectory() || f.isFile()) {
				long startMili = System.currentTimeMillis();
				compress(s);
				long endMili = System.currentTimeMillis();
				JOptionPane.showMessageDialog(null, "压缩成功，用时" + (endMili - startMili) / 1000 + "s");
			} else {
				JOptionPane.showMessageDialog(null, "请先选择文件。");
			}
		}
	}

	//decompress的listener
	public class l2 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = t0.getText();
			File f = new File(s);
			if (f.isDirectory() || f.isFile()) {
				long startMili = System.currentTimeMillis();
				decompress(s);
				long endMili = System.currentTimeMillis();
				JOptionPane.showMessageDialog(null, "解压成功，用时" + (endMili - startMili) / 1000 + "s");
			} else {
				JOptionPane.showMessageDialog(null, "请先选择文件。");
			}
		}
	}

	public void compress(String path) {
		//读入路径
		String compressName = "";
		if (path.contains(".")) {
			compressName = path.substring(0, path.lastIndexOf("."));
		} else
			compressName = path;
		
		//构建输出文件名和路径
		compressName += ".xlx";
		
		File f = new File(path);
		
		//存空文件夹和要压缩的文件
		ArrayList<File> emptyDir = new ArrayList();
		ArrayList<File> files = new ArrayList();
		getAllFiles(emptyDir, files, f);//获取文件

		try {
			File outFile = new File(compressName);
			FileOutputStream out0 = new FileOutputStream(outFile);
			DataOutputStream out1 = new DataOutputStream(out0);
			ObjectOutputStream out2 = new ObjectOutputStream(out0);
			
			out1.writeInt(files.size());//写入压缩文件总数
			for (int i = 0; i < files.size(); i++) {
				FileInputStream in = new FileInputStream(files.get(i));
				
				//构建huffmantree，并获得HashMap
				HuffmanTree ht = new HuffmanTree();
				NodeArray na = new NodeArray();
				ArrayList<Node> a = na.getNodes(files.get(i));
				Node root = ht.createTree(a);
				ht.createCodeMap(root);
				HashMap<Byte, String> hfCode = ht.getHfCode0();

				StringBuilder code = new StringBuilder();
				int len = in.available();
				byte[] fileByte = new byte[len];
				in.read(fileByte);
				out1.writeInt(len);//原文件大小
				out1.writeUTF(files.get(i).getAbsolutePath());//文件路径
				out2.writeObject(hfCode);//HashMap
				for (int j = 0; j < len; j++) {
					code = code.append(hfCode.get(fileByte[j]));
					//大于10000000就输出一次
					if (code.length() > 10000000 || j == fileByte.length - 1) {
						char[] temp = code.toString().toCharArray();
						int num = (temp.length) / 8;
						int totalByte = 0;
						if ((temp.length) % 8 != 0) {
							totalByte = num + 1;
							out1.writeInt(totalByte);
						} else {
							totalByte = num;
							out1.writeInt(totalByte);
						}
						byte[] b = new byte[totalByte];
						int index = 0;
						String s = "";
						//8位一个字节
						for (int l = 0; l < 8 * num; l += 8) {
							for (int k = 0; k < 8; k++)
								s = s + temp[l + k];
							int t = toBinary(s.toCharArray());
							b[index] = (byte) t;
							index++;
							s = "";
						}
						
						//处理余数
						if ((temp.length) % 8 != 0) {
							for (int x = 8 * num; x < temp.length; x++) {
								s = s + temp[x];
							}
							int t = toBinary(s.toCharArray());
							b[index] = (byte) t;
						}
						
						//统一写入
						out0.write(b);
						//清空StringBuilder，为下一块做准备
						code.delete(0, code.length());
					}
				}
				in.close();
			}
			out2.writeObject(emptyDir);
			out1.close();
			out2.close();
			out0.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void decompress(String path) {
		//检测文件格式
		if (!path.substring(path.lastIndexOf("."), path.length()).equals(".xlx")) {
			JOptionPane.showMessageDialog(null, "该文件格式不对。");
			return;
		}

		try {
			File f = new File(path);
			FileInputStream in0 = new FileInputStream(f);
			DataInputStream in1 = new DataInputStream(in0);
			ObjectInputStream in2 = new ObjectInputStream(in0);

			int fileNum = in1.readInt();
			for (int index = 0; index < fileNum; index++) {
				//读各个变量
				int len = in1.readInt();
				String filePath = in1.readUTF();
				HashMap<Byte, String> h = (HashMap<Byte, String>) in2.readObject();
				HashMap<String, Byte> m = new HashMap();
				for (Entry<Byte, String> entry : h.entrySet())
					m.put(entry.getValue(), entry.getKey());
				int t = 0;
				StringBuilder code = new StringBuilder();
				File outFile = new File(filePath);
				//创建输出文件
				createFile(outFile);
				//如果写出的bytes等于原文件的大小了，就要继续循环，开始读下一个文件了
				while (t < len) {
					int partLen = in1.readInt();
					int tag = 0;
					int i = 0;
					//读入一个分块的的bytes
					while (tag < partLen) {
						i = in0.read();
						tag++;
						String s = Integer.toBinaryString(i);
						if (s.length() < 8)
							for (int x = 0; x < 8; x++) {
								s = "0" + s;
								if (s.length() == 8)
									break;
							}
						code = code.append(s);
					}
					//准备写入 true是因为一个文件如果被分块，这里的out需要接在后面输出
					FileOutputStream out = new FileOutputStream(outFile, true);
					char[] b = code.toString().toCharArray();
					String s = "";
					ArrayList<Byte> a = new ArrayList();
					for (int x = 0; x < b.length; x++) {
						s = s + b[x];
						if (m.containsKey(s)) {
							a.add(m.get(s));
							s = "";
						}
					}
					t = t + a.size();
					code.delete(0, code.length());
					out.write(arrayToBytes(a));
					out.close();
				}
			}
			ArrayList<File> a = (ArrayList<File>) in2.readObject();
			if (!a.isEmpty())
				for (int i = 0; i < a.size(); i++)
					a.get(i).mkdir();
			in2.close();
			in1.close();
			in0.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//转binary
	public int toBinary(char[] c) {
		int t = 0;
		for (int i = 0; i < c.length; i++) {
			t += (c[i] - 48) * Math.pow(2, 7 - i);
		}
		return t;
	}
	
	//获得所有要压缩的文件
	public void getAllFiles(ArrayList<File> emptyDir, ArrayList<File> files, File f) {
		if (f.isFile()) {
			files.add(f);
			return;
		}
		File[] temp = f.listFiles();
		//如果是空文件夹就添加进emptyDir里面
		if (temp.length == 0) {
			emptyDir.add(f);
			return;
		}
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].isDirectory())
				getAllFiles(emptyDir, files, temp[i]);
			else if (!temp[i].getAbsolutePath().contains(".DS_Store"))
				files.add(temp[i]);
		}
	}

	//创建文件
	public void createFile(File file) throws IOException {
		if (!file.exists())
			makeDir(file.getParentFile());
		else
			file.delete();

		file.createNewFile();
	}
	
	//创建文件夹
	public void makeDir(File dir) {
		if (!dir.getParentFile().exists())
			makeDir(dir.getParentFile());
		dir.mkdir();
	}
	
	//ArrayList转byte数组
	public byte[] arrayToBytes(ArrayList<Byte> a) {
		byte[] b = new byte[a.size()];
		for (int i = 0; i < a.size(); i++) {
			b[i] = a.get(i);
		}
		return b;
	}

	public static void main(String[] args) {
		CompressFrame f = new CompressFrame();
	}
}
