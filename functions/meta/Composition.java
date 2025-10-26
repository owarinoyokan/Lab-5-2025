package functions.meta;

import functions.Function;

public class Composition implements Function {
    private Function fuf1;
    private Function fuf2;

    public Composition(Function fuf1, Function fuf2) {
        this.fuf1 = fuf1;
        this.fuf2 = fuf2;
    }

    public double getLeftDomainBorder() {
        return fuf1.getLeftDomainBorder();
    }

    public double getRightDomainBorder() {
        return fuf1.getRightDomainBorder();
    }

    public double getFunctionValue(double x) {
        double innerValue = fuf1.getFunctionValue(x);
        if (Double.isNaN(innerValue)) return Double.NaN;
        return fuf2.getFunctionValue(innerValue);
    }
}