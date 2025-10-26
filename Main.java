import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        println("Тестирование всего и вся\n");

        testBasicFunctions();
        testTabulatedFunctions();
        testMetaFunctions();
        testFileOperations();
        testSerialization();
        testExternalizable();
        testObjectMethods();

    }

    /** Тестирование базовых функций (пакет basic)
     */
    private static void testBasicFunctions() {
        println("1. Тест базовых функций");

        // Тест экспоненты
        Exp exp = new Exp();
        println("Экспонента:");
        System.out.printf("Область определения: [%.1f, %.1f]%n", exp.getLeftDomainBorder(), exp.getRightDomainBorder());
        for (double x = -1; x <= 1; x += 0.5) {
            System.out.printf("exp(%.1f) = %.3f%n", x, exp.getFunctionValue(x));
        }

        // Тест логарифма
        Log log = new Log(10); // Десятичный логарифм
        println("\nЛогарифм по основанию 10:");
        System.out.printf("Область определения: [%.1f, %.1f]%n", log.getLeftDomainBorder(), log.getRightDomainBorder());
        for (double x = 0.1; x <= 10; x *= 10) {
            System.out.printf("log10(%.1f) = %.3f%n", x, log.getFunctionValue(x));
        }

        // Тест тригонометрических функций
        Sin sin = new Sin();
        Cos cos = new Cos();

        println("\nТригонометрические функции на [0, π] с шагом 0.1:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f: sin=%.6f, cos=%.6f%n", x, sin.getFunctionValue(x), cos.getFunctionValue(x));
        }
        println();
    }

    /** Тестирование табулированной функции
     */
    private static void testTabulatedFunctions() {
        println("2. Тест табулированной функции");

        // Тест ArrayTabulatedFunction
        double[] values = {1, 3, 5, 7, 9, 11};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 5, values);

        println("ArrayTabulatedFunction (2x+1):");
        arrayFunc.printTabulatedFunction();

        // Тест интерполяции
        println("\nИнтерполяция ArrayTabulatedFunction:");
        for (double x = 0.5; x <= 4.5; x += 1) {
            System.out.printf("f(%.1f) = %.3f%n", x, arrayFunc.getFunctionValue(x));
        }

        // Тест LinkedListTabulatedFunction
        LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 5, values);

        println("\nLinkedListTabulatedFunction (2x+1):");
        listFunc.printTabulatedFunction();

        // Тест модификаций
        try {
            println("\nМодификация точек:");
            listFunc.setPointY(2, 10); // Меняем значение в точке x=2
            listFunc.addPoint(new FunctionPoint(2.5, 6.25));
            listFunc.deletePoint(1);
            listFunc.printTabulatedFunction();
        } catch (Exception e) {
            println("Ошибка при модификации: " + e.getMessage());
        }
        println();
    }

    /** Тестирование мета-функций (пакет meta)
     */
    private static void testMetaFunctions() {
        println("3. Тест мета-функций");

        Sin sin = new Sin();
        Cos cos = new Cos();

        // Сравнение оригинальных и табулированных функций
        println("Сравнение оригинальных и табулированных функций:");

        // Создаем табулированные аналоги с 10 точками
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);

        println("\nСравнение sin(x) и его табулированного аналога:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double originalSin = sin.getFunctionValue(x);
            double tabulatedSinVal = tabulatedSin.getFunctionValue(x);
            System.out.printf("x=%.1f: orig=%.6f, tab=%.6f%n", x, originalSin, tabulatedSinVal);
        }

        println("\nСравнение cos(x) и его табулированного аналога:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double originalCos = cos.getFunctionValue(x);
            double tabulatedCosVal = tabulatedCos.getFunctionValue(x);
            System.out.printf("x=%.1f: orig=%.6f, tab=%.6f%n", x, originalCos, tabulatedCosVal);
        }

        // Сумма квадратов табулированных аналогов
        println("\nСумма квадратов табулированных син и кос:");
        Function sumOfSquares = Functions.sum(Functions.power(tabulatedSin, 2), Functions.power(tabulatedCos, 2));

        println("sin²(x) + cos²(x) (табулированные, 10 точек):");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f: result=%.10f%n", x, sumOfSquares.getFunctionValue(x));
        }

        // Остальные тесты мета-функций
        Function scaledSin = new Scale(sin, 2, 3); // sin(2x) * 3
        println("\n3 * sin(2x):");
        for (double x = 0; x <= Math.PI; x += Math.PI/4) {
            System.out.printf("x=%.3f: result=%.3f%n", x, scaledSin.getFunctionValue(x));
        }

        Function shiftedCos = new Shift(cos, Math.PI/2, 1); // cos(x - π/2) + 1
        println("\ncos(x - π/2) + 1:");
        for (double x = 0; x <= Math.PI; x += Math.PI/4) {
            System.out.printf("x=%.3f: result=%.3f%n", x, shiftedCos.getFunctionValue(x));
        }

        Function mult = new Mult(sin, cos); // sin(x) * cos(x)
        println("\nsin(x) * cos(x):");
        for (double x = 0; x <= Math.PI; x += Math.PI/4) {
            System.out.printf("x=%.3f: result=%.3f%n", x, mult.getFunctionValue(x));
        }
        println();
    }

    /** Тестирование операций с файлами (текстовые, бинарные)
     */
    private static void testFileOperations() {
        println("4. Тест файловых операций");

        try {
            println("Экспонента - текстовый формат:");
            Exp exp = new Exp();
            TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);

            // Текстовый формат
            String textFile = "exp_text.txt";
            try (FileWriter writer = new FileWriter(textFile)) {
                TabulatedFunctions.writeTabulatedFunction(tabulatedExp, writer);
            }

            // Текстовое чтение
            TabulatedFunction readTextExp;
            try (FileReader reader = new FileReader(textFile)) {
                readTextExp = TabulatedFunctions.readTabulatedFunction(reader);
            }

            println("Экспонента (0 до 10, шаг 1):");
            println("Оригинал vs Прочитанный (текстовый формат):");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("x=%.1f: orig=%.6f, read=%.6f%n", x, tabulatedExp.getFunctionValue(x), readTextExp.getFunctionValue(x));
            }

            println("\nЛогарифм - бинарный формат:");
            Log log = new Log(Math.E);
            TabulatedFunction tabulatedLog = TabulatedFunctions.tabulate(log, 0.1, 10, 11);

            // Бинарная запись
            String binFile = "log_binary.bin";
            try (FileOutputStream fos = new FileOutputStream(binFile)) {
                TabulatedFunctions.outputTabulatedFunction(tabulatedLog, fos);
            }

            // Бинарное чтение
            TabulatedFunction readBinLog;
            try (FileInputStream fis = new FileInputStream(binFile)) {
                readBinLog = TabulatedFunctions.inputTabulatedFunction(fis);
            }

            println("Логарифм (0.1 до 10, шаг 1):");
            println("Оригинал vs Прочитанный (бинарный формат):");
            for (double x = 0.1; x <= 10; x += 1) {
                if (x < 0.1) continue;
                System.out.printf("x=%.1f: orig=%.6f, read=%.6f%n", x, tabulatedLog.getFunctionValue(x), readBinLog.getFunctionValue(x));
            }

        } catch (IOException e) {
            println("Ошибка файловых операций: " + e.getMessage());
        }
        println();
    }

    /** Тестирование Serializable
     */
    private static void testSerialization() {
        println("5. Тест сериализации (Serializable)");

        try {
            Exp exp = new Exp();
            Log log = new Log(Math.E);
            Function composition = Functions.composition(exp, log);
            TabulatedFunction original = TabulatedFunctions.tabulate(composition, 0, 10, 11);

            println("Оригинальная функция ln(exp(x)) = x (0 до 10, 11 точек):");
            printFunctionTable(original, 0, 10, 1);

            // Сериализация
            String serFile = "serializable.ser";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serFile))) {
                oos.writeObject(original);
            }

            // Десериализация
            TabulatedFunction deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serFile))) {
                deserialized = (TabulatedFunction) ois.readObject();
            }

            println("Десериализованная функция:");
            printFunctionTable(deserialized, 0, 10, 1);

        } catch (IOException | ClassNotFoundException e) {
            println("Ошибка сериализации: " + e.getMessage());
        }
        println();
    }

    /** Тестирование Externalizable
     */
    private static void testExternalizable() {
        println("6. Тест сериализации (Externalizable)");

        try {
            // функция для сравнения
            Exp exp = new Exp();
            Log log = new Log(Math.E);
            Function composition = Functions.composition(exp, log);
            TabulatedFunction tabulated = TabulatedFunctions.tabulate(composition, 0, 10, 11);

            // Создаем Externalizable версию
            ArrayTabulatedFunction original = new ArrayTabulatedFunction(0, 10, 11);
            for (int i = 0; i < tabulated.getPointsCount(); i++) {
                original.setPointY(i, tabulated.getPointY(i));
            }

            println("Оригинальная функция (Externalizable):");
            printFunctionTable(original, 0, 10, 1);

            // Сериализация
            String extFile = "externalizable.ser";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(extFile))) {
                oos.writeObject(original);
            }

            // Десериализация
            ArrayTabulatedFunction deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(extFile))) {
                deserialized = (ArrayTabulatedFunction) ois.readObject();
            }

            println("Десериализованная функция (Externalizable):");
            printFunctionTable(deserialized, 0, 10, 1);

        } catch (IOException | ClassNotFoundException e) {
            println("Ошибка Externalizable: " + e.getMessage());
        }
        println();
    }

    /** Тестирование методов toString(), equals(), hashCode(), clone()
     */
    private static void testObjectMethods() {
        println("7. Тест методов toString(), equals(), hashCode(), clone()");

        // Создаем тестовые данные
        double[] values1 = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] values2 = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] values3 = {1.0, 2.5, 3.0, 4.0, 5.0}; // Немного другие значения

        // Создаем объекты для тестирования
        ArrayTabulatedFunction arrayFunc1 = new ArrayTabulatedFunction(0, 4, values1);
        ArrayTabulatedFunction arrayFunc2 = new ArrayTabulatedFunction(0, 4, values2);
        ArrayTabulatedFunction arrayFunc3 = new ArrayTabulatedFunction(0, 4, values3);

        LinkedListTabulatedFunction listFunc1 = new LinkedListTabulatedFunction(0, 4, values1);
        LinkedListTabulatedFunction listFunc2 = new LinkedListTabulatedFunction(0, 4, values2);
        LinkedListTabulatedFunction listFunc3 = new LinkedListTabulatedFunction(0, 4, values3);

        println("\tТестирование toString()");

        println("ArrayTabulatedFunction 1: " + arrayFunc1.toString());
        println("ArrayTabulatedFunction 2: " + arrayFunc2.toString());
        println("ArrayTabulatedFunction 3: " + arrayFunc3.toString());
        println("LinkedListTabulatedFunction 1: " + listFunc1.toString());
        println("LinkedListTabulatedFunction 2: " + listFunc2.toString());
        println("LinkedListTabulatedFunction 3: " + listFunc3.toString());

        println("\n\tТестирование equals()");

        println("arrayFunc1.equals(arrayFunc2): " + arrayFunc1.equals(arrayFunc2) + " (ожидается true)");
        println("arrayFunc1.equals(arrayFunc3): " + arrayFunc1.equals(arrayFunc3) + " (ожидается false)");
        println("listFunc1.equals(listFunc2): " + listFunc1.equals(listFunc2) + " (ожидается true)");
        println("listFunc1.equals(listFunc3): " + listFunc1.equals(listFunc3) + " (ожидается false)");

        // Тест между разными классами с одинаковыми данными
        println("arrayFunc1.equals(listFunc1): " + arrayFunc1.equals(listFunc1) + " (ожидается true)");
        println("listFunc1.equals(arrayFunc1): " + listFunc1.equals(arrayFunc1) + " (ожидается true)");

        // Тест с разным количеством точек
        double[] values4 = {1.0, 2.0, 3.0, 4.0}; // На одну точку меньше
        ArrayTabulatedFunction arrayFunc4 = new ArrayTabulatedFunction(0, 3, values4);
        println("arrayFunc1.equals(arrayFunc4): " + arrayFunc1.equals(arrayFunc4) + " (ожидается false)");

        println("\n\tТестирование hashCode()");

        println("arrayFunc1.hashCode(): " + arrayFunc1.hashCode());
        println("arrayFunc2.hashCode(): " + arrayFunc2.hashCode());
        println("arrayFunc3.hashCode(): " + arrayFunc3.hashCode());
        println("listFunc1.hashCode(): " + listFunc1.hashCode());
        println("listFunc2.hashCode(): " + listFunc2.hashCode());
        println("listFunc3.hashCode(): " + listFunc3.hashCode());

        // Проверка согласованности equals и hashCode
        println("\n\tПроверка согласованности equals() и hashCode():");
        println("arrayFunc1.equals(arrayFunc2) && arrayFunc1.hashCode() == arrayFunc2.hashCode(): " + (arrayFunc1.equals(arrayFunc2) && arrayFunc1.hashCode() == arrayFunc2.hashCode()) + " (ожидается true)");
        println("listFunc1.equals(listFunc2) && listFunc1.hashCode() == listFunc2.hashCode(): " + (listFunc1.equals(listFunc2) && listFunc1.hashCode() == listFunc2.hashCode()) + " (ожидается true)");

        // Тест изменения объекта и его хэш-кода
        println("\n\tТест изменения объекта");

        println("Исходный arrayFunc1: " + arrayFunc1.toString());
        println("Исходный hashCode: " + arrayFunc1.hashCode());

        println();
        // Незначительно изменяем одну координату
        try {
            arrayFunc1.setPointY(2, 3.001); // Изменяем с 3.0 на 3.001
            println("После изменения 'Y[2] = 3.001': " + arrayFunc1.toString());
            println("Новый hashCode: " + arrayFunc1.hashCode());
            println("Хэш-код изменился: " + (arrayFunc1.hashCode() != arrayFunc2.hashCode()) + " (ожидается true)");
        } catch (Exception e) {
            println("Ошибка при изменении точки: " + e.getMessage());
        }

        println("\n\tТестирование clone()");
        try {
            // Клонируем объекты
            ArrayTabulatedFunction arrayClone = (ArrayTabulatedFunction) arrayFunc2.clone();
            LinkedListTabulatedFunction listClone = (LinkedListTabulatedFunction) listFunc2.clone();

            println("Оригинал arrayFunc2: " + arrayFunc2.toString());
            println("Клон arrayClone: " + arrayClone.toString());
            println("arrayFunc2.equals(arrayClone): " + arrayFunc2.equals(arrayClone) + " (ожидается true)");

            println("\nОригинал listFunc2: " + listFunc2.toString());
            println("Клон listClone: " + listClone.toString());
            println("listFunc2.equals(listClone): " + listFunc2.equals(listClone) + " (ожидается true)");

            // Проверка глубокого клонирования
            println("\n\tПроверка глубокого клонирования");

            // Изменяем оригиналы
            arrayFunc2.setPointY(1, 2.5);
            listFunc2.setPointY(1, 2.5);

            println("После изменения оригиналов:");
            println("Оригинал arrayFunc2: " + arrayFunc2.toString());
            println("Клон arrayClone: " + arrayClone.toString());
            println("Клон не изменился: " + arrayClone.getPointY(1) + " (ожидается 2.0)");

            println("\nОригинал listFunc2: " + listFunc2.toString());
            println("Клон listClone: " + listClone.toString());
            println("Клон не изменился: " + listClone.getPointY(1) + " (ожидается 2.0)");

            println("arrayFunc2.equals(arrayClone): " + arrayFunc2.equals(arrayClone) + " (ожидается false)");
            println("listFunc2.equals(listClone): " + listFunc2.equals(listClone) + " (ожидается false)");

        } catch (Exception e) {
            println("Ошибка при клонировании: " + e.getMessage());
            e.printStackTrace();
        }

        println();
    }

//----------------------------------------------------------------------------------------------------------------------

    /** Вспомогательный метод для красивого вывода значений функции
     * @param func функция, значения которой мы хотим вывести
     * @param start начальное значение x
     * @param end конечное значение x
     * @param step шаг изменения x
     */
    private static void printFunctionTable(TabulatedFunction func, double start, double end, double step) {
        for (double x = start; x <= end; x += step) {
            System.out.printf("f(%.1f) = %.6f%n", x, func.getFunctionValue(x));
        }
    }

    /** Короткий вывод
     * @param o то, что будет выводиться
     */
    private static void println(Object o) {
        System.out.println(o);
    }

    /** Пустая Строка
     */
    private static void println() {
        println("");
    }
}