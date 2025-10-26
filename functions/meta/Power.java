package functions.meta;

import functions.Function;

public class Power implements Function {
    private Function fuf;
    private double power;

    public Power(Function fuf, double power) {
        this.fuf = fuf;
        this.power = power;
    }

    public double getLeftDomainBorder() {
        return fuf.getLeftDomainBorder();
    }

    public double getRightDomainBorder() {
        return fuf.getRightDomainBorder();
    }

    public double getFunctionValue(double x) {
        double value = fuf.getFunctionValue(x);
        if (Double.isNaN(value)) return Double.NaN;
        return Math.pow(value, power);
    }
}