import vk.test.engine.*;

public class SomeProgramTest {

    private final SomeProgram someProgram = new SomeProgram();

    @AfterSuite
    public static void testAfterSuite() {
        System.out.println("AfterSuite");
    }

    @BeforeSuite
    public static void testBeforeSuite() {
        System.out.println("BeforeSuite");
    }


    @AfterTest
    public static void testAfterTest() {
        System.out.println("AfterTest");
    }

    @BeforeTest
    public static void testBeforeTest() {
        System.out.println("BeforeTest");
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
    @CsvSource(value = "12345, 20, 1234.34F")
    public void testEchoWithParams(String p1, int p2, Float p3) throws Exception {
        if (!someProgram.echoWithParams(p1, p2, p3).equals("12345, 20, 1234.34")){
            throw new Exception("Test not passed!");
        }
    }

}
