package vk.test.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TestRunner<T> {

    public static void runTests(Class c) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Constructor constructor = c.getConstructor();
        Object testClassObject = constructor.newInstance();
        List<TestsDTO> methodList = new ArrayList<>();
        TestsDTO afterSuite = null;
        TestsDTO beforeSuite = null;
        List<TestsDTO> afterTest = new ArrayList<>();
        List<TestsDTO> beforeTest = new ArrayList<>();
        for (Method method : c.getMethods()) {
            int modifiers = method.getModifiers();
            Test testAnnotation = method.getAnnotation(Test.class);
            String parSource = null;
            CsvSource sourceAnnotation = method.getAnnotation(CsvSource.class);
            if (sourceAnnotation != null) parSource = sourceAnnotation.value();
            Object[] sourcePar = parseParSource(parSource, method.getParameterTypes());
            if (testAnnotation != null) {
                methodList.add(new TestsDTO(testAnnotation.priority(), method, sourcePar));
            }
            if (Modifier.isStatic(modifiers)) {
                AfterTest afterTestAnnotation = method.getAnnotation(AfterTest.class);
                BeforeTest beforeTestAnnotation = method.getAnnotation(BeforeTest.class);
                if (method.getAnnotation(AfterSuite.class) != null)
                    if (afterSuite == null) afterSuite = new TestsDTO(1, method, sourcePar);
                    else throw new RuntimeException("Only one AfterSuite annotation is allowed!");
                if (method.getAnnotation(BeforeSuite.class) != null)
                    if (beforeSuite == null) beforeSuite = new TestsDTO(1, method, sourcePar);
                    else throw new RuntimeException("Only one BeforeSuite annotation is allowed!");
                if (afterTestAnnotation != null)
                    afterTest.add(new TestsDTO(afterTestAnnotation.priority(), method, sourcePar));
                if (beforeTestAnnotation != null)
                    beforeTest.add(new TestsDTO(beforeTestAnnotation.priority(), method, sourcePar));
            }
        }
        methodList.sort(null);
        afterTest.sort(null);
        beforeTest.sort(null);
        if (beforeSuite != null) invokeMethod(beforeSuite, null);
        for (TestsDTO testsDTO : methodList) {
            for (TestsDTO beforeDTO : beforeTest) invokeMethod(beforeDTO, null);
            System.out.println("Test method [" + testsDTO + "] for class [" + c.getName() + "]");
            invokeMethod(testsDTO, testClassObject);
            System.out.println("Test method [" + testsDTO + "] for class [" + c.getName() + "] passed successfully!");
            for (TestsDTO afterDTO : afterTest) invokeMethod(afterDTO, null);
        }
        if (afterSuite != null) invokeMethod(afterSuite, null);
    }

    private static void invokeMethod(TestsDTO testsDTO, Object testClassObject) throws IllegalAccessException, InvocationTargetException {
        if (testsDTO.parSource == null)
            testsDTO.method.invoke(testClassObject);
        else
            testsDTO.method.invoke(testClassObject, testsDTO.parSource);
    }

    static private Object[] parseParSource(String parSource, Class<?>[] parTypes) {
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

    static private Object parTypeMap(String s, Class<?> parType) {
        if (parType.equals(boolean.class) || parType.equals(Boolean.class)) {
            return Boolean.valueOf(s);
        }
        if (parType.equals(int.class) || parType.equals(Integer.class)) {
            return Integer.valueOf(s);
        }
        if (parType.equals(float.class) || parType.equals(Float.class)) {
            return Float.valueOf(s);
        }
        if (parType.equals(double.class) || parType.equals(Double.class)) {
            return Double.valueOf(s);
        }
        return s;
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
