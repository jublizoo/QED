import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.JPanel;

public class Display extends JPanel {
	
	Main m;
	
	public Display(Main m) {
		this.m = m;
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g; 
		g2d.setStroke(new BasicStroke(5));
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, m.frame.getContentPane().getWidth(), m.frame.getContentPane().getWidth());
		
		Path2D.Double polygon;
		try {
			g2d.setColor(Color.DARK_GRAY);
			polygon = new Path2D.Double();
			polygon.moveTo(m.polygon.get(0)[0], m.polygon.get(0)[1]);
			for(int i = 1; i < m.polygon.size(); i++) {
				polygon.lineTo(m.polygon.get(i)[0], m.polygon.get(i)[1]);
			
			}
			polygon.closePath();
			g2d.fill(polygon);
						
			g2d.setColor(Color.blue);
			g2d.drawLine((int) Math.round(m.point1[0]), (int) Math.round(m.point1[1]), (int) Math.round(m.point2[0]), (int) Math.round(m.point2[1]));
			
			g2d.setColor(Color.red);
			for(int i = 0; i < m.points.size(); i++) {
				g2d.fillOval((int) Math.round(m.points.get(i)[0]) - 5, (int) Math.round(m.points.get(i)[1]) - 5, 10, 10);
			}
			
			g2d.setColor(Color.MAGENTA);
			g2d.fillOval((int) Math.round(m.center[0] - 5), (int) Math.round(m.center[1] - 5), 10, 10);
			
			g2d.setColor(Color.GREEN);
			g2d.fillOval((int) Math.round(m.centroid[0]) - 5, (int) Math.round(m.centroid[1]) - 5, 10, 10);
			
			g2d.setColor(Color.orange);
			for(int i = 0; i < m.dividedShape.size(); i++) {
				polygon = new Path2D.Double();
				polygon.moveTo(m.dividedShape.get(i).get(0)[0], m.dividedShape.get(i).get(0)[1]);
				for(int b = 1; b < m.dividedShape.get(i).size(); b++) {
					polygon.lineTo(m.dividedShape.get(i).get(b)[0], m.dividedShape.get(i).get(b)[1]);
				
				}
				polygon.closePath();
				g2d.draw(polygon);
			}
			
			g2d.setColor(Color.CYAN);
			for(int i = 0; i < m.dividedShape2.size(); i++) {
				polygon = new Path2D.Double();
				polygon.moveTo(m.dividedShape2.get(i).get(0)[0], m.dividedShape2.get(i).get(0)[1]);
				for(int b = 1; b < m.dividedShape2.get(i).size(); b++) {
					polygon.lineTo(m.dividedShape2.get(i).get(b)[0], m.dividedShape2.get(i).get(b)[1]);
				
				}
				polygon.closePath();
				g2d.draw(polygon);
			}
			
		}catch(Exception e) {}		
		
	}

}
