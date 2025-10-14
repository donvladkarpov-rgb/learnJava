import vk.test.engine.*;

public class SomeProgramTest {

    private final SomeProgram someProgram = new SomeProgram();

    @AfterSuite
    @CsvSource(value = "54321")
    public static void testAfterSuite(int p1) {
        System.out.println("AfterSuite " + p1);
    }

    @BeforeSuite
    public static void testBeforeSuite() {
        System.out.println("BeforeSuite");
    }


    @AfterTest(priority = 1)
    public static void testAfterTest1() {
        System.out.println("AfterTest1");
    }

    @BeforeTest(priority = 1)
    public static void testBeforeTest1() {
        System.out.println("BeforeTest1");
    }

    @AfterTest(priority = 2)
    public static void testAfterTest2() {
        System.out.println("AfterTest2");
    }

    @BeforeTest(priority = 2)
    public static void testBeforeTest2() {
        System.out.println("BeforeTest2");
    }

    @AfterTest(priority = 3)
    @CsvSource(value = "false, true")
    public static void testAfterTest3(Boolean p1, boolean p2) {
        System.out.println("AfterTest3 " + p1 + ", " + p2);
    }

    @BeforeTest(priority = 3)
    @CsvSource(value = "1234.34F, 123434")
    public static void testBeforeTest3(double p1, Integer p2) {
        System.out.println("BeforeTest3 " + p1 + ", " + p2);
    }

    @Test
    public void testHelloWorld() throws Exception {
        if (!someProgram.helloWorld().equals("Hello Russian World!")){
            throw new Exception("Test not passed!");
        }
    }

    @Test(priority = 2)
    public void testEcho() throws Exception {
        if (!someProgram.echo("12345").equals("12345")){
            throw new Exception("Test not passed!");
        }
    }

    @Test(priority = 1)
    @CsvSource(value = "12345, 20, 1234.34F, true")
    public void testEchoWithParams(String p1, int p2, Float p3, boolean p4) throws Exception {
        if (!someProgram.echoWithParams(p1, p2, p3, p4).equals("12345, 20, 1234.34, true")){
            throw new Exception("Test not passed!");
        }
    }

}
