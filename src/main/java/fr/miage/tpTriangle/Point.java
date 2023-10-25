package fr.miage.tpTriangle;

public record Point( double x, double y) {

    public double distanceTo(Point other) {
        return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
    }


}
