package fr.miage.tpTriangle;

public record Triangle(Point pt1, Point pt2, Point pt3) {
    public boolean isEquilateralTriangle() {
        double sideAB = pt1.distanceTo(pt2);
        double sideAC = pt1.distanceTo(pt3);
        double sideBC = pt2.distanceTo(pt3);
        double tolerance = 0.0001;
        return Math.abs(sideAB - sideAC) < tolerance && Math.abs(sideAB - sideBC) < tolerance;
    }

    public double calculatePerimeter() {
        double side1 = pt1.distanceTo(pt2);
        double side2 = pt2.distanceTo(pt3);
        double side3 = pt1.distanceTo(pt3);
        return side1 + side2 + side3;
    }

    public double calculatePerimeterEquilateral() {
        return 3 * pt1.distanceTo(pt2);
    }

}
