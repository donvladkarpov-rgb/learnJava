import vk.test.engine.TestRunner;

import java.lang.reflect.InvocationTargetException;

public class TestEngine {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TestRunner.runTests(SomeProgramTest.class);
    }

}
