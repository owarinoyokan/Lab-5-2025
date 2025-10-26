package functions;

import java.io.Externalizable;
import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction,Externalizable {
    private FunctionPoint[] points;
    private int pointsCount;

    // Конструктор по умолчанию требуется для Externalizable
    public ArrayTabulatedFunction() {
        // Инициализация по умолчанию
        this.points = new FunctionPoint[10];
        this.pointsCount = 0;
    }

    // Конструктор создания объекта табулированной функции через кол-во точек
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) { //Проверка области определения
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }
        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount*2];

        double step = (rightX - leftX) / (pointsCount - 1); // Задание шага

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            points[i] = new FunctionPoint(x, 0);    // создание точек с равным шагом step через интервал x
        }
    }

    // Конструктор создания объекта табулированной функции через массив значений
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) { //Проверка области определения
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }
        this.pointsCount = values.length;
        this.points = new FunctionPoint[values.length*2];

        double step = (rightX - leftX) / (values.length - 1);   // Задание шага

        for (int i = 0; i < values.length; i++) {
            double x = leftX + step * i;
            points[i] = new FunctionPoint(x, values[i]);    // создание точек с равным шагом step через интервал x
        }
    }

    // Конструктор создания объекта через массив точек
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        // Проверка упорядоченности точек по X
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Точки не упорядочены по значению абсциссы");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount * 2];

        // Копируем точки с созданием новых объектов для инкапсуляции
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount * 2]; // С запасом

        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    // интерпол
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) { return Double.NaN; } // Вне графика
        if (x == getLeftDomainBorder()) { return points[0].getY(); }    // на левом конце
        if (x == getRightDomainBorder()) { return points[pointsCount - 1].getY(); }     // на правом конце

        // внутри графика
        for (int i = 0; i < pointsCount; i++) {
            double x1 = points[i].getX();
            double x2 = points[i+1].getX();

            if (x >= x1 && x <= x2) {
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();

                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    // кол-во точек
    public int getPointsCount() {
        return pointsCount;
    }

    // копия точки
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        return new FunctionPoint(points[index]);
    }

    // замена точки
    public void setPoint(int index, FunctionPoint point) {
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        // если индекс > 0 и новая точка находится вне левой точки, то скип
        if (index > 0 && point.getX() <= points[index-1].getX()) { return; }

        // если индекс < кол-во точек и новая точка находится вне правой точки, то скип
        if (index < (pointsCount-1) && point.getX() >= points[index+1].getX()) { return; }

        points[index] = new FunctionPoint(point);   // если все норм, то меняем точку
    }

    // значение абсциссы точки с указанным номером
    public double getPointX(int index) {
        return points[index].getX();
    }

    // изменение значение абсциссы точки с указанным номером
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }

        if (index == 0) { // Проверка для первого элемента
            if (x >= points[1].getX()) {
                throw new InappropriateFunctionPointException("Новый 'x' для первой точки должен быть меньше следующей точки");
            }
        }
        else if (index == pointsCount - 1) { // Проверка для последнего элемента
            if (x <= points[pointsCount - 2].getX()) { // строго больше предыдущего
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        }
        else { // Проверка для средних элементов
            if (x <= points[index - 1].getX() || x >= points[index + 1].getX()) {
                throw new InappropriateFunctionPointException("Новый 'x' должен быть между соседними точками");
            }
        }
        points[index].setX(x);
    }

    // значение ординаты точки с указанным номером
    public double getPointY(int index) {
        return points[index].getY();
    }

    // изменение значение ординаты точки с указанным номером.
    public void setPointY(int index, double y) {
        points[index].setY(y);
    }

    // удаление заданной точки табулированной функции.
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        if (pointsCount < 3) { //Если количество точек меньше 3
            throw new IllegalStateException("Невозможно удалить точку, так как 'количество точек меньше 3'");
        }
        System.arraycopy(points, index+1,points, index, pointsCount-index-1);
        pointsCount--;
    }

    // добавить новую точку табулированной функции
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        for (int i = 0; i < pointsCount; i++) { //Проверка на уникальность х
            if (points[i].getX() == point.getX()) {
                throw new InappropriateFunctionPointException("Точка с таким 'x' уже существует");
            }
        }
        FunctionPoint[] newPoints = new FunctionPoint[pointsCount*2];

        // ищем позицию между точками
        int pos = 0;
        while (pos < pointsCount && points[pos].getX() < point.getX()) {
            pos++;
        }

        // копируем до нужной позиции
        System.arraycopy(points, 0, newPoints, 0, pos);

        // вставляем точку
        newPoints[pos] = point;

        // копируем остаток
        System.arraycopy(points, pos, newPoints, pos+1, pointsCount - pos);
        // меняем обратно и увеличиваем кол-во точек
        points = newPoints;
        pointsCount++;
    }

    public void printTabulatedFunction() {

        int pointsCount = getPointsCount();
        for (int i = 0; i < pointsCount; i++) {
            double x = getPointX(i);
            double y = getPointY(i);
            System.out.println(i+1 + " --- (" + x + ", " + y + ")");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            sb.append(points[i].toString());  // Используем toString точки
            if (i < pointsCount - 1) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();  // Формат: {(x1; y1), (x2; y2), ...}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Проверка на тот же объект
        if (!(o instanceof TabulatedFunction)) return false;  // Проверка интерфейса

        if (o instanceof ArrayTabulatedFunction) { // Проверка класса
            // Оптимизация для одинакового класса
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;
            if (this.pointsCount != other.pointsCount) return false;

            for (int i = 0; i < pointsCount; i++) {
                if (!this.points[i].equals(other.points[i])) return false;
            }
            return true;
        } else {
            // Общий случай для любого TabulatedFunction
            TabulatedFunction other = (TabulatedFunction) o;
            if (this.getPointsCount() != other.getPointsCount()) return false;

            for (int i = 0; i < pointsCount; i++) {
                if (!this.getPoint(i).equals(other.getPoint(i))) return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int hash = pointsCount;  // Начинаем с количества точек
        for (int i = 0; i < pointsCount; i++) {
            hash ^= points[i].hashCode();  // XOR с хэш-кодами точек
        }
        return hash;
    }

    @Override
    public Object clone() {
        FunctionPoint[] clonedPoints = new FunctionPoint[points.length];
        for (int i = 0; i < pointsCount; i++) {
            clonedPoints[i] = (FunctionPoint) points[i].clone();  // Глубокое клонирование точек
        }

        ArrayTabulatedFunction clone = new ArrayTabulatedFunction();
        clone.points = clonedPoints;
        clone.pointsCount = this.pointsCount;
        return clone;
    }
}