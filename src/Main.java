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
		frame.setSize(1000, 1000);
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
		new Main();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
	
	public void generateElements(){
		calculateCenter();
		calculateNewPoints(Math.random() * Math.PI * 2);
		centroid = findCentroid(polygon, calculateArea(polygon));
		divideShape();
		divideShape2();
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
			vert = new Double[] { Math.random() * 900, Math.random() * 900};
			polygon.add(vert);
		}

		for (int i = 3; i <= numVerts; i++) {
			attempts = 0;
			do {
				attempts++;
				intersects = false;
				vert = new Double[] {Math.random() * 900, Math.random() * 900};

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
			pointIndexes.add(new Integer[] {polygon.size() - 1, 0});
		}
	}
	
	//TODO Sort pointIndexes
	public TwoList sortPoints(ArrayList<Double[]> one, ArrayList<Integer> two) {
		
		for (int i = 1; i < one.size(); ++i) {
			//The key is the double we are currently sorting.
			Double[] key = one.get(i);
			Integer key2 = two.get(i);
			
			//The index of the variable we compare our key to.
			int a = i - 1;
			
			/*
			 * Here, we move backwards, starting from the index of the double we are sorting. We stop if the index is less
			 * than 0, or if the double that we are checking is less than our key. 			 
			 */
			while (a >= 0 && one.get(a)[0] < key[0]) {
				//Starting at the element to the left of double we are sorting, we move the element to the right
				one.set(a + 1, one.get(a));
				two.set(a + 1, two.get(a));
				a--;
			}
			
			/*
			 * We stopped the while loop when we found the FIRST element that our key was smaller than. Our 'a' value 
			 * represents the index of this first element, because we decreased the value of a on the previous iteration
			 * of the while loop. Therefore, we want the element we are sorting to be to the
			 * right of the first smaller element, which will be a + 1. We do not have to worry about overwriting the 
			 * element at a + 1, because we already moved it to the right in the previous iteration of the while loop.
			 */
			one.set(a + 1, key);
			two.set(a + 1, key2);
		}
		
		TwoList sortedLists = new TwoList(one, two);
		
		return sortedLists;
		
	}
	
	//TODO UNUSED POINTS NOT SORTED????
	public void divideShape() {
		ArrayList<Double[]> unusedPoints = new ArrayList<Double[]>();
		ArrayList<Integer> unusedPointIndexes = new ArrayList<Integer>();
		//We need this to keep track of the updated indexes (within the polygon), even if they have been removed
		ArrayList<Integer> newPointIndexes;
		//The polygon with the intersections inserted
		ArrayList<Double[]> newPolygon = (ArrayList<Double[]>) polygon.clone();
		dividedShape = new ArrayList<ArrayList<Double[]>>();
		ArrayList<Double[]> currentPolygon;
		boolean intersection;
		int initPointIndex = 0;
		//Index of the value we remove from unusedPoints
		int removeIndex = 0;
		//Index of the point we are on
		int index = 0;
		//How much to increase the index of each point when we insert the intersection points
		int indexIncrease = 0;
		//If you should use the first or second option
		boolean option1;
		
		/*
		 * Adding the intersection points to the new Polygon. We have to do it in the order the points occur in
		 * the polygon, because when we increase indexIncrease, it should only affect the points after the 
		 * current point in the polygon. If we do it in the order that they occur in the pointIndexes list, 
		 * it may cycle backwards in the order points occur in the polygon, so adding indexIncrease would give
		 * the wrong index. We use the first index + 1, instead of just the second points, because the last intersection
		 * point should occur at the end of the list, and not the beginning (May not matter).
		 */
		for(int i = 0; i < polygon.size(); i++) {
			for(int b = 0; b < pointIndexes.size(); b++) {
				if(i == pointIndexes.get(b)[0]) {
					//We want to add it after the first point (otherwise it wouldn't be on the line), so we add 1
					newPolygon.add(pointIndexes.get(b)[0] + indexIncrease + 1, points.get(b));
					unusedPointIndexes.add(pointIndexes.get(b)[0] + indexIncrease + 1);
					unusedPoints.add(points.get(b));
					//Each time you add a point, all the points after that will have their index increased.
					indexIncrease++;
				}
			}
		}
		
		unusedPointIndexes = sortPoints(unusedPoints, unusedPointIndexes).two;	
		newPointIndexes = (ArrayList<Integer>) unusedPointIndexes.clone();
				
		//Loops shapes
		while(true) {
			//Starting at the rightmost point
			if(unusedPointIndexes.size() > 0) {
				//Remove below line if sort method does not work.
				index = unusedPointIndexes.get(0);
				initPointIndex = index;
			}else {
				//If there are no more leftmost points, we have exhausted all shapes on this side.			
				break;
			}
			
			option1 = true;
			
			currentPolygon = new ArrayList<Double[]>();
			
			//Loops points
			while(true) {
				currentPolygon.add(newPolygon.get(index));
				
				intersection = false;
				//Find if the current point is an intersection
				for(int i = 0; i < unusedPointIndexes.size(); i++) {
					if(index == unusedPointIndexes.get(i)) {
						intersection = true;
						removeIndex = i;
					}
				}
				
				if(intersection) {
					if(option1) {
						//1. move along shape
						if(index < newPolygon.size() - 1) {
							index++;
						}else {
							//Loops around
							index = 0;
						}
					}else {
						/*
						 * 2. move left on line
						 * If we move left to the initial point, it will have already been removed from
						 * unusedPointIndexes. Therefore, we will get an error if we try to set the index to
						 * that. Instead, we use newPointIndexes, to search for already removed points.
						 */
						for(int i = 0; i < newPointIndexes.size(); i++) {
							if(newPointIndexes.get(i) == index) {
								index = newPointIndexes.get(i - 1);
							}
						}
					}
					
					option1 = !option1;
					
					/*
					 * Removes point from unusedPointIndexes. We cannot use the index, because the index of the value
					 * is what we need for the remove function, so we need the index of the index, which will
					 * almost always be different from the index.
					 */
					unusedPointIndexes.remove(removeIndex);
				}else {
					//Move along shape
					if(index < newPolygon.size() - 1) {
						index++;
					}else {
						//Loops around
						index = 0;
					}
				}
				
				//Checks if it's the initial point
				if(index == initPointIndex) {
					break;
				}else {
					/*
					 * We add each point before we change the index. This means that if we reach the final 
					 * (initial) point, it has not been added, because we check if it is the final point after
					 * we change the index, and we will break before it has the chance to loop, and add the 
					 * point with the current index.
					 * By the same logic, we do not have to worry about ending the loop on the ACTUAL initial 
					 * point (the first occurence), because we change the index before checking if it is the 
					 * final (initial) point.
					 */
				}
			}
			
			dividedShape.add(currentPolygon);
		}

	}
	
	public void divideShape2() {
		ArrayList<Double[]> unusedPoints = new ArrayList<Double[]>();
		ArrayList<Integer> unusedPointIndexes = new ArrayList<Integer>();
		//We need this to keep track of the updated indexes (within the polygon), even if they have been removed
		ArrayList<Integer> newPointIndexes;
		//The polygon with the intersections inserted
		ArrayList<Double[]> newPolygon = (ArrayList<Double[]>) polygon.clone();
		dividedShape2 = new ArrayList<ArrayList<Double[]>>();
		ArrayList<Double[]> currentPolygon;
		boolean intersection;
		int initPointIndex = 0;
		//Index of the value we remove from unusedPoints
		int removeIndex = 0;
		//Index of the point we are on
		int index = 0;
		//How much to increase the index of each point when we insert the intersection points
		int indexIncrease = 0;
		//If you should use the first or second option
		boolean option1;
		
		/*
		 * Adding the intersection points to the new Polygon. We have to do it in the order the points occur in
		 * the polygon, because when we increase indexIncrease, it should only affect the points after the 
		 * current point in the polygon. If we do it in the order that they occur in the pointIndexes list, 
		 * it may cycle backwards in the order points occur in the polygon, so adding indexIncrease would give
		 * the wrong index. We use the first index + 1, instead of just the second points, because the last intersection
		 * point should occur at the end of the list, and not the beginning (May not matter).
		 */
		for(int i = 0; i < polygon.size(); i++) {
			for(int b = 0; b < pointIndexes.size(); b++) {
				if(i == pointIndexes.get(b)[0]) {
					//We want to add it after the first point (otherwise it wouldn't be on the line), so we add 1
					newPolygon.add(pointIndexes.get(b)[0] + indexIncrease + 1, points.get(b));
					unusedPointIndexes.add(pointIndexes.get(b)[0] + indexIncrease + 1);
					unusedPoints.add(points.get(b));
					//Each time you add a point, all the points after that will have their index increased.
					indexIncrease++;
				}
			}
		}
		
		unusedPointIndexes = sortPoints(unusedPoints, unusedPointIndexes).two;	
		newPointIndexes = (ArrayList<Integer>) unusedPointIndexes.clone();
				
		//Loops shapes
		while(true) {
			//Starting at the rightmost point
			if(unusedPointIndexes.size() > 0) {
				//Remove below line if sort method does not work.
				index = unusedPointIndexes.get(0);
				initPointIndex = index;
			}else {
				//If there are no more leftmost points, we have exhausted all shapes on this side.			
				break;
			}
			
			option1 = true;
			
			currentPolygon = new ArrayList<Double[]>();
			
			//Loops points
			while(true) {
				currentPolygon.add(newPolygon.get(index));
				
				intersection = false;
				//Find if the current point is an intersection
				for(int i = 0; i < unusedPointIndexes.size(); i++) {
					if(index == unusedPointIndexes.get(i)) {
						intersection = true;
						removeIndex = i;
					}
				}
				
				if(intersection) {
					if(option1) {
						//1. move along shape
						if(index > 0) {
							index--;
						}else {
							//Loops around
							index = newPolygon.size() - 1;
						}
					}else {
						/*
						 * 2. move left on line
						 * If we move left to the initial point, it will have already been removed from
						 * unusedPointIndexes. Therefore, we will get an error if we try to set the index to
						 * that. Instead, we use newPointIndexes, to search for already removed points.
						 */
						for(int i = 0; i < newPointIndexes.size(); i++) {
							if(newPointIndexes.get(i) == index) {
								index = newPointIndexes.get(i - 1);
							}
						}
					}
					
					option1 = !option1;
					
					/*
					 * Removes point from unusedPointIndexes. We cannot use the index, because the index of the value
					 * is what we need for the remove function, so we need the index of the index, which will
					 * almost always be different from the index.
					 */
					unusedPointIndexes.remove(removeIndex);
				}else {
					//Move along shape
					if(index > 0) {
						index--;
					}else {
						//Loops around
						index = newPolygon.size() - 1;
					}
				}
				
				//Checks if it's the initial point
				if(index == initPointIndex) {
					break;
				}else {
					/*
					 * We add each point before we change the index. This means that if we reach the final 
					 * (initial) point, it has not been added, because we check if it is the final point after
					 * we change the index, and we will break before it has the chance to loop, and add the 
					 * point with the current index.
					 * By the same logic, we do not have to worry about ending the loop on the ACTUAL initial 
					 * point (the first occurence), because we change the index before checking if it is the 
					 * final (initial) point.
					 */
				}
			}
			
			dividedShape2.add(currentPolygon);
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