package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function fuf;
    private double scaleX;
    private double scaleY;

    public Scale(Function fuf, double scaleX, double scaleY) {
        this.fuf = fuf;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public double getLeftDomainBorder() {
        return fuf.getLeftDomainBorder() * scaleX;
    }

    public double getRightDomainBorder() {
        return fuf.getRightDomainBorder() * scaleX;
    }

    public double getFunctionValue(double x) {
        double scaledX = x / scaleX;
        if (scaledX < fuf.getLeftDomainBorder() || scaledX > fuf.getRightDomainBorder()) {
            return Double.NaN;
        }
        return fuf.getFunctionValue(scaledX) * scaleY;
    }
}