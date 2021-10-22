import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JPanel;

public class Display extends JPanel {
	
	Main m;
	
	public Display(Main m) {
		this.m = m;
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g; 
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, m.frame.getContentPane().getWidth(), m.frame.getContentPane().getWidth());
		
		g2d.setColor(Color.DARK_GRAY);
		Path2D.Double polygon = new Path2D.Double();
		try {
			polygon.moveTo(m.shape.get(0)[0], m.shape.get(0)[1]);
			for(int i = 1; i < m.shape.size(); i++) {
				polygon.lineTo(m.shape.get(i)[0], m.shape.get(i)[1]);
			
			}
			polygon.closePath();
			g2d.fill(polygon);
			
			g2d.setColor(Color.blue);
			g2d.drawLine((int) Math.round(m.point1[0]), (int) Math.round(m.point1[1]), (int) Math.round(m.point2[0]), (int) Math.round(m.point2[1]));
			
			g2d.setColor(Color.red);
			for(int i = 0; i < m.points.size(); i++) {
				g2d.fillOval((int) Math.round(m.points.get(i)[0]) - 5, (int) Math.round(m.points.get(i)[1]) - 5, 10, 10);
			}
		}catch(Exception e) {}		
		
	}

}
