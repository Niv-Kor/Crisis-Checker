package albert.user_interface.result_log.graph;

public class Point
{
	private double x, y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public void setX(double val) { x = val; }
	public void setY(double val) { y = val; }
}