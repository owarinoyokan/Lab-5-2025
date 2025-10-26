package functions.meta;

import functions.Function;

public class Sum implements Function {
    private Function fuf1;
    private Function fuf2;

    public Sum(Function fuf1, Function fuf2) {
        this.fuf1 = fuf1;
        this.fuf2 = fuf2;
    }

    public double getLeftDomainBorder() {
        return Math.max(fuf1.getLeftDomainBorder(), fuf2.getLeftDomainBorder());
    }

    public double getRightDomainBorder() {
        return Math.min(fuf1.getRightDomainBorder(), fuf2.getRightDomainBorder());
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        return fuf1.getFunctionValue(x) + fuf2.getFunctionValue(x);
    }
}