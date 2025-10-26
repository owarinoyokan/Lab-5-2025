package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {
    private static class FunctionNode {
        FunctionPoint point;    // точка
        FunctionNode next;     // след точка
        FunctionNode prev;     // пред точка

        FunctionNode(FunctionPoint point) { // конструктор
            this.point = point;
        }
    }
    private FunctionNode head;      // Голова
    private int pointCounts;        // Кол-во точек

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointCounts) {
            throw new IllegalArgumentException("Выходит за границу: " + index);
        }

        FunctionNode current;

        if (index < pointCounts / 2) {
            // Ищем с начала (если индекс в первой половине)
            current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            // Ищем с конца (если индекс во второй половине)
            current = head.prev;
            for (int i = pointCounts - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode last = head.prev;

        newNode.prev = last;
        newNode.next = head;

        last.next = newNode;
        head.prev = newNode;

        pointCounts++;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        if  (index < 0 || index > pointCounts) {
            throw new FunctionPointIndexOutOfBoundsException("Выход за границу" + index);
        }
        // если в конец
        if (index == pointCounts) {
            return addNodeToTail();
        }

        FunctionNode newNode = new FunctionNode(null);

        FunctionNode next = getNodeByIndex(index);
        FunctionNode prev = next.prev;

        newNode.prev = prev;
        newNode.next = next;

        prev.next = newNode;
        next.prev = newNode;

        pointCounts++;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index > pointCounts) {
            throw new FunctionPointIndexOutOfBoundsException("Выход за границу: " + index);
        }
        FunctionNode target = getNodeByIndex(index);

        FunctionNode prevNode = target.prev;
        FunctionNode nextNode = target.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        pointCounts--;

        // Отключаем ссылки в удалённом узле
        target.next = null;
        target.prev = null;

        return target;
    }

        // конструктор 1
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Кол-во точек меньше двух");
        }
        // Инициализация списка
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointCounts = 0;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, 0.0);
        }
    }

        // конструктор 2
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Кол-во точек меньше двух");
        }

        // Инициализация списка
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointCounts = 0;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
    }

    // Конструктор создания объекта через массив точек
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }

        // Проверка упорядоченности точек по X
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Точки не упорядочены по значению абсциссы");
            }
        }

        // Инициализация списка
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointCounts = 0;

        // Добавляем точки с созданием новых объектов для инкапсуляции
        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point);
        }
    }

    public double getLeftDomainBorder() {
        if (pointCounts == 0) return Double.NaN;
        return head.next.point.getX(); // Первый значащий узел
    }

    public double getRightDomainBorder() {
        if (pointCounts == 0) return Double.NaN;
        return head.prev.point.getX(); // Последний значащий узел
    }

    public double getFunctionValue(double x) {
        if (pointCounts == 0) return Double.NaN;

        double left = getLeftDomainBorder();
        double right = getRightDomainBorder();

        if (x < left || x > right) {
            return Double.NaN;
        }
        if (x == left) {
            return getNodeByIndex(0).point.getY();
        }
        if (x == right) {
            return getNodeByIndex(pointCounts - 1).point.getY();
        }

        // обходим узлы последовательно (оптимально — один проход)
        FunctionNode cur = head.next;
        while (cur != head && cur.next != head) {
            double x1 = cur.point.getX();
            double x2 = cur.next.point.getX();

            if (x >= x1 && x <= x2) {
                double y1 = cur.point.getY();
                double y2 = cur.next.point.getY();
                if (x1 == x2) return y1;

                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            cur = cur.next;
        }
        return Double.NaN;
    }

    public int getPointsCount() {
        return pointCounts;
    }

    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double newX = point.getX();

        // Проверка порядка точек
        if (pointCounts == 1) {
            node.point = new FunctionPoint(point);
            return;
        }

        // проверяем левую границу
        if (index == 0) {
            if (newX >= node.next.point.getX()) throw new InappropriateFunctionPointException("Новый 'x' для первой точки должен быть меньше следующей точки");
        }
        // проверяем правую границу
        else if (index == pointCounts - 1) {
            double rightX = getNodeByIndex(index + 1).point.getX();
            if (point.getX() <= rightX) throw new InappropriateFunctionPointException("Новый 'x' для последней точки должен быть больше предыдущей точки");
        }
        else if (newX <= node.prev.point.getX() || newX >= node.next.point.getX()) {
            throw new InappropriateFunctionPointException("Новый 'x' должен быть между соседними точками");
        }

        if (index == 0) {
            // Первый элемент: новая X должна быть < следующей
            if (newX >= node.next.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X для первой точки должна быть меньше следующей точки");
            }
        } else if (index == pointCounts - 1) {
            // Последний элемент: новая X должна быть > предыдущей
            if (newX <= node.prev.point.getX()) {
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        }
        node.point = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double newX = x;

        // Проверка порядка точек
        if (pointCounts == 1) {
            node.point.setX(x);
            return;
        }

        if (index == 0) {
            if (newX >= node.next.point.getX()) {
                throw new InappropriateFunctionPointException("Новый 'x' для первой точки должен быть меньше следующей точки");
            }
        } else if (index == pointCounts - 1) {
            if (newX <= node.prev.point.getX()) {
                throw new InappropriateFunctionPointException("Новый 'x' для последней точки должен быть больше предыдущей точки");
            }
        } else {
            if (newX <= node.prev.point.getX() || newX >= node.next.point.getX()) {
                throw new InappropriateFunctionPointException("Новый 'x' должен быть между соседними точками");
            }
        }

        node.point.setX(x);
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    public void deletePoint(int index) {
        if (pointCounts <= 2) throw new IllegalStateException("Невозможно удалить точку, так как 'количество точек меньше или равно двум'");
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на существование точки с таким X
        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с таким 'x' уже существует");
            }
            current = current.next;
        }

        // Поиск позиции для вставки
        FunctionNode cur = head.next;
        int pos = 0;
        while (cur != head && cur.point.getX() < point.getX()) {
            cur = cur.next;
            pos++;
        }

        FunctionNode newNode = addNodeByIndex(pos);
        newNode.point = new FunctionPoint(point);
    }

    public void printTabulatedFunction() {
        FunctionNode current = head.next;
        int index = 0;
        for (; index < pointCounts; index++) {
            System.out.println(index+1 + " --- (" + current.point.getX() + ", " + current.point.getY() + ")");
            current = current.next;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode current = head.next;
        while (current != head) {
            sb.append(current.point.toString());  // Используем toString точки
            if (current.next != head) sb.append(", ");
            current = current.next;
        }
        sb.append("}");
        return sb.toString();  // Формат: {(x1; y1), (x2; y2), ...}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Проверка на тот же объект
        if (!(o instanceof TabulatedFunction)) return false;  // Проверка интерфейса

        TabulatedFunction other = (TabulatedFunction) o;
        if (this.getPointsCount() != other.getPointsCount()) return false;

        // Оптимизация для одинакового класса
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction otherList = (LinkedListTabulatedFunction) o;
            FunctionNode currentThis = this.head.next;
            FunctionNode currentOther = otherList.head.next;

            while (currentThis != head && currentOther != otherList.head) {
                if (!currentThis.point.equals(currentOther.point)) return false;
                currentThis = currentThis.next;
                currentOther = currentOther.next;
            }
            return true;
        } else {
            // Общий случай для любого TabulatedFunction
            for (int i = 0; i < getPointsCount(); i++) {
                if (!this.getPoint(i).equals(other.getPoint(i))) return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int hash = getPointsCount();  // Начинаем с количества точек
        FunctionNode current = head.next;
        while (current != head) {
            hash ^= current.point.hashCode();  // XOR с хэш-кодами точек
            current = current.next;
        }
        return hash;
    }

    @Override
    public Object clone() {
        // Создаем массив точек для нового списка
        FunctionPoint[] pointsArray = new FunctionPoint[pointCounts];
        FunctionNode current = head.next;

        for (int i = 0; i < pointCounts; i++) {
            pointsArray[i] = (FunctionPoint) current.point.clone();  // Клонируем каждую точку
            current = current.next;
        }

        // Создаем новый объект через конструктор с массивом точек
        return new LinkedListTabulatedFunction(pointsArray);
    }
}