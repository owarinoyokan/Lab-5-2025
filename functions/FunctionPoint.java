package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private double x;
    private double y;

    // Создаёт объект точки с координатами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Создаёт объект точки с теми же координатами точки
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    // Создаёт объект точки с координатами (0;0)
    public FunctionPoint() {
        this.x = 0;
        this.y = 0;
    }

    // Задать X
    public void setX(double x) {
        this.x = x;
    }

    // Получить X
    public double getX() {
        return x;
    }

    // Задать Y
    public void setY(double y) {
        this.y = y;
    }

    // Получить Y
    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Проверка на тот же объект
        if (o == null || getClass() != o.getClass()) return false; // Проверка типа

        FunctionPoint that = (FunctionPoint) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0; // Сравнение x и y с учётом NaN
    }

    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        int xHash = (int)(xBits ^ (xBits >>> 32)); // XOR старшей и младшей половин
        int yHash = (int)(yBits ^ (yBits >>> 32)); // XOR старшей и младшей половин

        return xHash ^ yHash; // Комбинирование хэшей координат
    }

    @Override
    public Object clone() {
        return new FunctionPoint(this.x, this.y); // Создание копии точки
    }
}