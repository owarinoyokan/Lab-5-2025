package functions;

public interface Function {
    public double getLeftDomainBorder();        // возвращает значение левой границы области определения функции
    public double getRightDomainBorder();       // возвращает значение правой границы области определения функции
    public double getFunctionValue(double x);   // возвращает значение функции в заданной точке
}
