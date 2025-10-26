//тут собрано дерьмо, которое надо избегать в прогах
public class NoVisibility {

    public static boolean ready = false;
    public static int number;

    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!ready) {    //гонка за ready, при отсутствии volatile тред вообще может никогда не увидеть новое значение
                Thread.yield(); //не гарантированная передача управления другому треду
            }
            System.out.println(number); //гонка за number, при отсутствии volatile тоже самое что и с ready
        }
    }

    public static void main(String... args) {
        new ReaderThread().start();
        //возможен reordering - jvm решит что лучше вначале присвоить реди, а потом number
        number = 42;
        ready = true;
    }

}
