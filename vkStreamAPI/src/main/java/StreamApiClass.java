import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamApiClass {

    public static final String WORD_LIST = """
            Для вас, души моей царицы,
            Красавицы, для вас одних
            Времен минувших небылицы,
            В часы досугов золотых,
            Под шепот старины болтливой,
            Рукою верной я писал;
            Примите ж вы мой труд игривый!
            Ничьих не требуя похвал,
            Счастлив уж я надеждой сладкой,
            Что дева с трепетом любви
            Посмотрит, может быть украдкой,
            На песни грешные мои.
            
            У лукоморья дуб зеленый;
            Златая цепь на дубе том:
            И днем и ночью кот ученый
            Всё ходит по цепи кругом;
            Идет направо — песнь заводит,
            Налево — сказку говорит.
            
            Там чудеса: там леший бродит,
            Русалка на ветвях сидит;
            Там на неведомых дорожках
            Следы невиданных зверей;
            Избушка там на курьих ножках
            Стоит без окон, без дверей;
            Там лес и дол видений полны;
            Там о заре прихлынут волны
            На брег песчаный и пустой,
            И тридцать витязей прекрасных
            Чредой из вод выходят ясных,
            И с ними дядька их морской;
            Там королевич мимоходом
            Пленяет грозного царя;
            Там в облаках перед народом
            Через леса, через моря
            Колдун несет богатыря;
            В темнице там царевна тужит,
            А бурый волк ей верно служит;
            Там ступа с Бабою Ягой
            Идет, бредет сама собой;
            Там царь Кащей над златом чахнет;
            Там русской дух… там Русью пахнет!
            И там я был, и мед я пил;
            У моря видел дуб зеленый;
            Под ним сидел, и кот ученый
            Свои мне сказки говорил.
            Одну я помню: сказку эту
            Поведаю теперь я свету…
            """;

    public static final Employee[] EMPLOYEES = {
            new Employee("Вася", 30, "Инженер"),
            new Employee("Кирилл", 30, "Дворник"),
            new Employee("Петя", 35, "Инженер"),
            new Employee("Люба", 30, "Секретарь"),
            new Employee("Жора", 29, "Инженер"),
            new Employee("Гога", 60, "Директор"),
            new Employee("Игорь", 40, "Инженер")};

    public static final Integer[] INTEGERS = {5, 2, 10, 9, 4, 3, 10, 1, 13};

    public record Employee(String name, int age, String position) implements Comparable<Employee> {
        @Override
        public int compareTo(Employee o) {
            return o.age - this.age;
        }
    }

    public static String[] wordArray() {
        return WORD_LIST.toLowerCase().split("[— ,.;…!\n:-]+");
    }
    public static String[] lineArray() {
        return WORD_LIST.toLowerCase().split("[\n]+");
    }

    public static void main(String[] args) {
        example1();
        example2();
        example3();
        example4();
        example5();
        example6();
        example7();
        example8();
    }

    /**
     * Найдите в списке целых чисел 3-е наибольшее число
     * (пример: 5 2 10 9 4 3 10 1 13 => 10)
     */
    public static void example1() {
        System.out.println(
                Stream.of(INTEGERS).sorted((t1, t2) -> t2 - t1).skip(2).findFirst().orElseThrow()
        );
    }

    /**
     * Найдите в списке целых чисел 3-е наибольшее «уникальное» число
     * (пример: 5 2 10 9 4 3 10 1 13 => 9, в отличие от прошлой задачи здесь
     * разные 10 считает за одно число)
     */
    public static void example2() {
        System.out.println(
                Stream.of(INTEGERS).sorted((t1, t2) -> t2 - t1).distinct().skip(2).findFirst().orElseThrow()
        );
    }

    /**
     * Имеется список объектов типа Сотрудник (имя, возраст, должность),
     * необходимо получить список имен 3 самых старших сотрудников с должностью
     * «Инженер», в порядке убывания возраста
     */
    public static void example3() {
        System.out.println(
                Stream.of(EMPLOYEES).filter(e -> e.position.equals("Инженер")).sorted().limit(3).map(e -> e.name).toList()
        );
    }

    /**
     * Имеется список объектов типа Сотрудник (имя, возраст, должность),
     * посчитайте средний возраст сотрудников с должностью «Инженер»
     */
    public static void example4() {
        System.out.println(
                Stream.of(EMPLOYEES).filter(e -> e.position.equals("Инженер")).collect(Collectors.averagingInt(t -> t.age))
        );
    }

    /**
     * Найдите в списке слов самое длинное
     */
    public static void example5() {
        System.out.println(
                Stream.of(wordArray()).max(Comparator.comparingInt(String::length)).orElseThrow()
        );
    }

    /**
     * Имеется строка с набором слов в нижнем регистре, разделенных пробелом.
     * Постройте хеш-мапы, в которой будут хранится пары: слово - сколько раз оно
     * встречается во входной строке
     */
    public static void example6() {
        //Это по задаче
        System.out.println(
                Stream.of(wordArray()).collect(Collectors.groupingBy((w) -> w, Collectors.counting()))
        );
        //Это для красоты
        System.out.println(
                Stream.of(wordArray()).collect(Collectors.groupingBy((w) -> w, Collectors.counting())).entrySet().stream().sorted((e1, e2) -> e2.getValue().intValue() - e1.getValue().intValue()).toList()
        );
    }

    /**
     * Отпечатайте в консоль строки из списка в порядке увеличения длины слова,
     * если слова имеют одинаковую длины, то должен быть сохранен алфавитный порядок
     */
    public static void example7() {
//        System.out.println(
//                Stream.of(wordArray()).collect(Collectors.groupingBy((w) -> w, Collectors.counting())).entrySet().stream().sorted(
//                        (e1, e2) -> {
//                            int l = e1.getKey().length() - e2.getKey().length();
//                            if (l == 0)
//                                return e1.getKey().compareTo(e2.getKey());
//                            else
//                                return l;
//                        }
//                ).map(Map.Entry::getKey).toList()
//        );
        System.out.println(
                Stream.of(wordArray()).collect(Collectors.groupingBy((w) -> w, Collectors.counting())).keySet().stream().sorted(
                        Comparator.comparing(String::length).thenComparing(String::compareToIgnoreCase)
                ).toList()
        );
        //Вопрос: почему Comparator.comparing().thenComparing() не удается сделать на типе Map.Entry<String, Long> ?
        //Например для того чтоб весь Set вывести (как в закоментированном варианте), а не только Key (как в "рекомендованом" варианте)
    }

    /**
     * Имеется массив строк, в каждой из которых лежит набор из 5 слов,
     * разделенных пробелом, найдите среди всех слов самое длинное,
     * если таких слов несколько, получите любое из них
     */
    public static void example8() {
        System.out.println(
                Arrays.stream(lineArray()).flatMap(line->Stream.of(line.split("[— ,.;…!:-]+"))).max(Comparator.comparingInt(String::length)).orElseThrow()
        );
    }

}
