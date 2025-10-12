package vk.test.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TestRunner<T> {

    private final Class<T> testClass;

    public TestRunner(Class<T> testClass) {
        this.testClass = testClass;
    }

    public void startTests() {
        try {
            Constructor<T> constructor = testClass.getConstructor();
            Object testClassObject = constructor.newInstance();
            List<TestsDTO> methodList = new ArrayList<>();
            Method afterSuite = null;
            Method beforeSuite = null;
            Method afterTest = null;
            Method beforeTest = null;
            for (Method method : testClass.getMethods()) {
                int modifiers = method.getModifiers();
                Test testAnnotation = method.getAnnotation(Test.class);
                if (testAnnotation != null) {
                    String parSource = null;
                    CsvSource sourceAnnotation = method.getAnnotation(CsvSource.class);
                    if (sourceAnnotation != null) parSource = sourceAnnotation.value();
                    methodList.add(new TestsDTO(testAnnotation.priority(), method, parseParSource(parSource, method.getParameterTypes())));
                }
                if (Modifier.isStatic(modifiers)) {
                    if (method.getAnnotation(AfterSuite.class) != null)
                        if (afterSuite == null) afterSuite = method;
                        else throw new RuntimeException("Only one AfterSuite annotation is allowed!");
                    if (method.getAnnotation(BeforeSuite.class) != null)
                        if (beforeSuite == null) beforeSuite = method;
                        else throw new RuntimeException("Only one BeforeSuite annotation is allowed!");
                    if (method.getAnnotation(AfterTest.class) != null)
                        if (afterTest == null) afterTest = method;
                        else throw new RuntimeException("Only one AfterTest annotation is allowed!");
                    if (method.getAnnotation(BeforeTest.class) != null)
                        if (beforeTest == null) beforeTest = method;
                        else throw new RuntimeException("Only one BeforeTest annotation is allowed!");
                }
            }
            methodList.sort(null);
            if (beforeSuite != null) beforeSuite.invoke(null);
            for (TestsDTO testsDTO : methodList) {
                if (beforeTest != null) beforeTest.invoke(null);
                System.out.println("Test method [" + testsDTO + "] for class [" + testClass.getName() + "]");
                if (testsDTO.parSource == null)
                    testsDTO.method.invoke(testClassObject);
                else
                    testsDTO.method.invoke(testClassObject, testsDTO.parSource);
                System.out.println("Test method [" + testsDTO + "] for class [" + testClass.getName() + "] passed successfully!");
                if (afterTest != null) afterTest.invoke(null);
            }
            if (afterSuite != null) afterSuite.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] parseParSource(String parSource, Class<?>[] parTypes) {
        List al = new ArrayList();
        if (parSource != null && !parSource.isEmpty()) {
            StringTokenizer st = new StringTokenizer(parSource, ", ", false);
            int i = 0;
            while (st.hasMoreTokens())
                al.add(parTypeMap(st.nextToken(), parTypes[i++]));
            return al.toArray();
        } else {
            return null;
        }
    }

    private Object parTypeMap(String s, Class<?> parType) {
        if (parType.equals(int.class)) { //int.class - это что-то для меня новое....
            return Integer.valueOf(s);
        }
        if (parType.equals(Integer.class)) {
            return Integer.valueOf(s);
        }
        if (parType.equals(Float.class)) {
            return Float.valueOf(s);
        }
        if (parType.equals(Double.class)) {
            return Float.valueOf(s);
        }
        return s;
    }

    public static <T> void runTests(Class<T> testClass) {
        TestRunner<T> testRunner = new TestRunner<>(testClass);
        testRunner.startTests();
    }

    public static void runTests(String testClass) {
        try {
            TestRunner testRunner = new TestRunner(Class.forName(testClass));
            testRunner.startTests();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private record TestsDTO(
            int priority,
            Method method,
            Object[] parSource
    ) implements Comparable<TestsDTO> {

        @Override
        public int priority() {
            if (priority < 1 || priority > 10)
                throw new RuntimeException("priority in @Test annotation must be from 1 to 10 inclusive, but current value is " + priority);
            return priority;
        }

        @Override
        public int compareTo(TestsDTO o) {
            return priority - o.priority;
        }

        @Override
        public String toString() {
            return "TestsDTO{" +
                    "priority=" + priority +
                    ", method=" + method.getName() +
                    '}';
        }
    }

}
