package functions.meta;

import functions.Function;

public class Shift implements Function {
    private Function fuf;
    private double shiftX;
    private double shiftY;

    public Shift(Function fuf, double shiftX, double shiftY) {
        this.fuf = fuf;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    public double getLeftDomainBorder() {
        return fuf.getLeftDomainBorder() + shiftX;
    }

    public double getRightDomainBorder() {
        return fuf.getRightDomainBorder() + shiftX;
    }

    public double getFunctionValue(double x) {
        double shiftedX = x - shiftX;
        if (shiftedX < fuf.getLeftDomainBorder() || shiftedX > fuf.getRightDomainBorder()) {
            return Double.NaN;
        }
        return fuf.getFunctionValue(shiftedX) + shiftY;
    }
}