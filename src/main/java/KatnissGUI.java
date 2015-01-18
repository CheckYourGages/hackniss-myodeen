import javax.swing.*;
import javax.swing.table.TableModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;

public class KatnissGUI extends JFrame{
	
	private static final long serialVersionUID = 4689741290780397965L;
	
	JLabel image1;
	JLabel image2;
	JLabel image3;
	ClassLoader loader;
	JLabel timer;
	private JTable table;
	
	public KatnissGUI(){
		super("Hackniss Myodeen");
		setSize(500, 500);
		setResizable(false);
		
		loader = getClass().getClassLoader();
		
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setContinuousLayout(true);
		splitPane.setBackground(new Color(0, 0, 0));
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(splitPane);
		
		JPanel topPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) topPanel.getLayout();
		flowLayout.setVgap(10);
		topPanel.setForeground(Color.BLACK);
		topPanel.setBackground(new Color(66, 255, 35));
		topPanel.setOpaque(true);
		splitPane.setLeftComponent(topPanel);
		
		JLabel titleLabel = new JLabel("H A C K n i s s      M Y O d e e n");
		titleLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		titleLabel.setLabelFor(topPanel);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Orbitron", Font.PLAIN, 22));
		topPanel.add(titleLabel);
		topPanel.setSize(getContentPane().getWidth(), 100);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(new Color(0, 0, 0));
		splitPane.setRightComponent(bottomPanel);
		bottomPanel.setLayout(null);
		
		image1 = new JLabel(new ImageIcon(loader.getResource("img/1.png")));
		image1.setBounds(50, 50, 69, 69);
		bottomPanel.add(image1);
		
		image2 = new JLabel(new ImageIcon(loader.getResource("img/2.png")));
		image2.setBounds(214, 50, 69, 69);
		bottomPanel.add(image2);
		
		image3 = new JLabel(new ImageIcon(loader.getResource("img/3.png")));
		image3.setBounds(370, 50, 69, 69);
		bottomPanel.add(image3);
		
		JLabel unlockLabel = new JLabel("Unlock");
		unlockLabel.setForeground(Color.WHITE);
		unlockLabel.setBounds(60, 130, 44, 16);
		bottomPanel.add(unlockLabel);
		
		JLabel drawLabel = new JLabel("Draw");
		drawLabel.setForeground(Color.WHITE);
		drawLabel.setBounds(231, 130, 32, 16);
		bottomPanel.add(drawLabel);
		
		JLabel releaseLabel = new JLabel("Release");
		releaseLabel.setForeground(Color.WHITE);
		releaseLabel.setBounds(380, 130, 61, 16);
		bottomPanel.add(releaseLabel);
		
		timer = new JLabel("0.0");
		timer.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		timer.setHorizontalAlignment(SwingConstants.CENTER);
		timer.setForeground(Color.WHITE);
		timer.setBounds(6, 168, 484, 16);
		bottomPanel.add(timer);
		
		table = new JTable();
		table.setBounds(50, 213, 389, 176);
		
		bottomPanel.add(table);
		
		setVisible(true);
	}
	
	public void toggle(int i, boolean t){
		String c = "";
		if(t){c="a";}
		
		switch(i){
			case 1:
				image1.setIcon(new ImageIcon(loader.getResource("img/1"+c+".png")));
			break;
			case 2:
				image2.setIcon(new ImageIcon(loader.getResource("img/2"+c+".png")));
			break;
			case 3:
				image3.setIcon(new ImageIcon(loader.getResource("img/3"+c+".png")));
			break;
		}	
	}
	
	public void setTime(String time){
		timer.setText(time);
	}
	
	protected void frameInit() {
		super.frameInit();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
