import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Main implements ActionListener {

	JFrame frame;
	Display d;
	Timer timer;
	UserInput ui;
	boolean retry;
	
	
	ArrayList<Double[]> polygon;
	Double[] center;
	Double[] centroid;
	
	//Points of the line through the center
	Double[] point1;
	Double[] point2;
	
	//Intersection points of the line, and the polygon
	ArrayList<Double[]> points;
	//The indexes of the lines these points fall on
	ArrayList<Integer[]> pointIndexes;
		
	ArrayList<ArrayList<Double[]>> dividedShape;
	ArrayList<ArrayList<Double[]>> dividedShape2;

	public Main() {
		timer = new Timer(10, this);
		frame = new JFrame();
		d = new Display(this);
		ui = new UserInput(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
		frame.add(d);
		frame.addKeyListener(ui);
		frame.addMouseListener(ui);
		// Double[] x = findIntersection(new Double[] {1.0, 1.0}, new Double[] {4.0,
		// 4.0}, new Double[] {1.0, 3.0}, new Double[] {2.5, 0.0});
		// System.out.println(x[0] + ", " + x[1]);
		timer.start();
	}

	public static void main(String[] args) {
		Main m = new Main();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
	
	public void generateElements(){
		calculateCenter();
		calculateNewPoints(Math.random() * Math.PI * 2);
		centroid = findCentroid(polygon, calculateArea(polygon));
		sortPoints();
		//divideShape();
	}

	public void generateShape() {
		int numVerts = 3;
		int attempts;
		boolean addVert = true;
		boolean intersects;
		polygon = new ArrayList<Double[]>();
		Double[] vert = null;
		retry = false;

		while (addVert) {
			if (Math.random() < 0.1) {
				addVert = false;
			}
			numVerts++;
		}

		for (int i = 0; i < 3; i++) {
			vert = new Double[] { Math.random() * 500, Math.random() * 500};
			polygon.add(vert);
		}

		for (int i = 3; i <= numVerts; i++) {
			attempts = 0;
			do {
				attempts++;
				intersects = false;
				vert = new Double[] {Math.random() * 500, Math.random() * 500};

				// Checking for a collision between the line from the last point to the current
				// point
				for (int b = 1; b < polygon.size(); b++) {
					if (findIntersection(polygon.get(b - 1), polygon.get(b), polygon.get(polygon.size() - 1), vert) != null) {
						intersects = true;
					}
				}

				/*
				 * If it is the last point, we will draw an additional line to the first point.
				 * Therefore, we also have to check if this line collides.
				 */
				if (i == numVerts) {
					for (int b = 1; b < polygon.size(); b++) {
						if (findIntersection(polygon.get(b - 1), polygon.get(b), vert, polygon.get(0)) != null) {
							intersects = true;
						}
					}
					
					if(attempts > 300) {
						retry = true;
						return;
					}
				}

			} while (intersects);
			polygon.add(vert);
		}

	}
	
	public void calculateCenter() {
		center = new Double[] {0.0, 0.0};
		
		for(int i = 0; i < polygon.size(); i++) {
			center[0] += polygon.get(i)[0];
			center[1] += polygon.get(i)[1];
		}
		
		center[0] /= polygon.size();
		center[1] /= polygon.size();
	}
	
	/*
	 * This is one of few functions with a return type, because we will have to use it on the sub-polygons too, 
	 * and not just the main polygon.
	 */
	public double calculateArea(ArrayList<Double[]> polygon) {
		double area = 0;
		
		//The function assumes that the last point loops around to the first.
		polygon.add(polygon.get(0));
		
		for(int i = 0; i < polygon.size() - 1; i++) {
			area += polygon.get(i)[0] * polygon.get(i + 1)[1] - polygon.get(i + 1)[0] * polygon.get(i)[1];
		}
		
		area /= 2;
		
		return area;
	}
	
	public Double[] findCentroid(ArrayList<Double[]> polygon, double area) {
		Double[] centroid = new Double[] {0.0, 0.0};
		
		//The function assumes that the last point loops around to the first.
		polygon.add(polygon.get(0));
		
		for(int i = 0; i < polygon.size() - 1; i++) {
			centroid[0] += (polygon.get(i)[0] + polygon.get(i + 1)[0]) * (polygon.get(i)[0] * polygon.get(i + 1)[1] - polygon.get(i + 1)[0] * polygon.get(i)[1]);
			centroid[1] += (polygon.get(i)[1] + polygon.get(i + 1)[1]) * (polygon.get(i)[0] * polygon.get(i + 1)[1] - polygon.get(i + 1)[0] * polygon.get(i)[1]);
		}
		
		centroid[0] /= (6 * area);
		centroid[1] /= (6 * area);
		
		return centroid;
	}
	
	public void calculateNewPoints(double angle){
		points = new ArrayList<Double[]>();
		//The maximum distance such that both point will fall outside the polygon bounding box
		double maxDistance = Math.sqrt(2 * Math.pow(500, 2));
		pointIndexes = new ArrayList<Integer[]>();
		Double[] intersection;
		point1 = new Double[] {center[0] + maxDistance * Math.cos(angle), center[1] + maxDistance * Math.sin(angle)};
		point2 = new Double[] {center[0] - maxDistance * Math.cos(angle), center[1] - maxDistance * Math.sin(angle)};
		
		for(int i = 1; i < polygon.size(); i++) {
			intersection = findIntersection(polygon.get(i-1), polygon.get(i), point1, point2);
			if(intersection != null) {
				points.add(intersection);
				pointIndexes.add(new Integer[] {i-1, i});
			}
		}
		
		intersection = findIntersection(polygon.get(polygon.size() - 1), polygon.get(0), point1, point2);
		if(intersection != null) {
			points.add(intersection);
			pointIndexes.add(new Integer[] {polygon.size(), 0});
		}
	}
	
	//TODO Sort pointIndexes
	public void sortPoints() {
		for (int i = 1; i < points.size(); ++i) {
			//The key is the double we are currently sorting.
			Double[] key = points.get(i);
			Integer[] key2 = pointIndexes.get(i);
			
			//The index of the variable we compare our key to.
			int a = i - 1;
			
			/*
			 * Here, we move backwards, starting from the index of the double we are sorting. We stop if the index is less
			 * than 0, or if the double that we are checking is less than our key. 			 
			 */
			while (a >= 0 && points.get(a)[0] < key[0]) {
				//Starting at the element to the left of double we are sorting, we move the element to the right
				points.set(a + 1, points.get(a));
				pointIndexes.set(a + 1, pointIndexes.get(a));
				a--;
			}
			
			/*
			 * We stopped the while loop when we found the FIRST element that our key was smaller than. Our 'a' value 
			 * represents the index of this first element, because we decreased the value of a on the previous iteration
			 * of the while loop. Therefore, we want the element we are sorting to be to the
			 * right of the first smaller element, which will be a + 1. We do not have to worry about overwriting the 
			 * element at a + 1, because we already moved it to the right in the previous iteration of the while loop.
			 */
			points.set(a + 1, key);
			pointIndexes.set(a + 1, key2);
		}
	}
	
	public void divideShape() {
		ArrayList<Double[]> unusedPoints = (ArrayList<Double[]>) points.clone();
		ArrayList<Integer> unusedPointIndexes = new ArrayList<Integer>();
		ArrayList<Double[]> newPolygon = (ArrayList<Double[]>) polygon.clone();
		dividedShape = new ArrayList<ArrayList<Double[]>>();
		ArrayList<Double[]> currentPolygon = new ArrayList<Double[]>();
		boolean intersection;
		int initPointIndex = 0;
		int shapeIndex = 0;
		//Index of the point we are on
		int index = 0;
		//How much to increase the index of each point when we insert the intersection points
		int indexIncrease = 0;
		//If you should use the first or second option
		boolean option1;
		
		//Adding the intersection points to the new Polygon
		for(int i = 0; i < points.size(); i++) {
			newPolygon.add(pointIndexes.get(i)[0] + indexIncrease, points.get(i));
			//The point being added is AFTER the first value in its corresponding point index, so we have to add 1.
			unusedPointIndexes.add(pointIndexes.get(i)[0] + indexIncrease + 1);
			//Each time you add a point, all the points after that will have their index increased.
			indexIncrease++;
		}
		//TODO update the non-intersection point indexes.
		//TODO add intersection point for the last line.
		
		//Loops shapes
		while(true) {
			option1 = true;
			//Loops points
			while(true) {
				intersection = false;
				//Find if the current point is an intersection
				for(int i = 0; i < unusedPointIndexes.size(); i++) {
					if(index == unusedPointIndexes.get(i)) {
						intersection = true;
					}
				}
				
				if(intersection) {
					//Removes point from unusedPoints
					unusedPoints.remove(index);
					unusedPointIndexes.remove(index);
					
					if(option1) {
						//1. move along shape
						index++;
					}else {
						//2. move left on line
					}
					option1 = !option1;
				}else {
					//Move along shape
					index++;
				}
				
				if(/*initial point*/true) {
					break;
				}else {
					dividedShape.add(currentPolygon);
				}
				
				//index++; (Unsure of why I have this here)
			}
			
			shapeIndex++;
			//remove points from pointIndexes
		}

	}

	public Double[] findIntersection(Double[] a1, Double[] a2, Double[] b1, Double[] b2) {
		Double[] maxA;
		Double[] minA;
		Double[] maxB;
		Double[] minB;
		// These variables must be initalized so we can used them later
		double slopeA = 0;
		double slopeB = 0;
		double interceptA = 0;
		double interceptB = 0;
		// Are lines a and b vertical (infinite slopes)
		boolean infA = false;
		boolean infB = false;
		Double[] intersection = new Double[2];

		if (a2[0] - a1[0] != 0) {
			slopeA = (a2[1] - a1[1]) / (a2[0] - a1[0]);
			interceptA = a1[1] - (a1[0] * slopeA);
		} else {
			infA = true;
			intersection[0] = a1[0];
		}

		if (b2[0] - b1[0] != 0) {
			slopeB = (b2[1] - b1[1]) / (b2[0] - b1[0]);
			interceptB = b1[1] - (b1[0] * slopeB);
		} else {
			infB = true;
			intersection[0] = b1[0];
		}

		/*
		 * If the infA and infB are false, we set the value manually, because the slope
		 * was infinite. We cannot find the intersection using the same formula if the
		 * slope is infinite, so we must check if this value has been initialized, so we
		 * can decide which formula to use.
		 */
		if (!infA && !infB) {
			if (slopeA != slopeB) {
				intersection[0] = (interceptB - interceptA) / (slopeA - slopeB);
				intersection[1] = intersection[0] * slopeA + interceptA;
			} else {
				return null;
			}

		} else if (infA && infB) {
			return null;
		} else if (infA) {
			intersection[1] = intersection[0] * slopeB + interceptB;
		} else {
			intersection[1] = intersection[0] * slopeA + interceptA;
		}

		maxA = new Double[] { Math.max(a1[0], a2[0]), Math.max(a1[1], a2[1]) };
		minA = new Double[] { Math.min(a1[0], a2[0]), Math.min(a1[1], a2[1]) };
		maxB = new Double[] { Math.max(b1[0], b2[0]), Math.max(b1[1], b2[1]) };
		minB = new Double[] { Math.min(b1[0], b2[0]), Math.min(b1[1], b2[1]) };
		// Checking if the point is within both of the segments
		if (intersection[0] < maxA[0] && intersection[0] > minA[0] && intersection[1] < maxA[1]
				&& intersection[1] > minA[1]) {
			if (intersection[0] < maxB[0] && intersection[0] > minB[0] && intersection[1] < maxB[1]
					&& intersection[1] > minB[1]) {
				return intersection;
			}
		}

		return null;

	}

}