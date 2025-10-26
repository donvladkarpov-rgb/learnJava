#### задать поток

1. конструктор Thread в лямде
2. имплемент Runnable
3. наследник от Thread (недостаточки)

- поток продолжает выполнение после завершение потока Main
- прога завершается когда выполнятся все потоки
- потоки демоны (Thred.setDeamond(true)) завершаются автоматически после завершения потока Main

#### фишки

1. в main thread.join(), main ждет thread
2. Thread.start и Thread.run один запускает поток, второй нет
3. порядок Nth Thread.start непредсказуем
4. Thread.stop() - не Ок! deprecated! надо юзать Thread.interrupt() и Thread.currentThread().isInterrupted() внутри run()
5. НО! Внутри run() при этом на sleep() например (и на некотороых других) может вылететь InterruptedException !

#### синхронизация

1. методы в классе можно делать synchronized, но это будет значить что мы все выстроим в один поток по сути
2. надо делать synchronized(Object) - более правильный путь
3. Лучше всего синхронизироваться просто на Object, пример с синхронизацией по Integer (unmodifylable) ++ дает новый объект и трендец
4. Нельзя синхронизироваться на кэшируемых объектах, строках и интежерах (-127 до 128)
5. Нельзя синхронизироваться на mutable объектах (String, Integers)
6. volatile убирает процессорный поточный кэш на переменную
7. synchronized(this) { synchronized(this) } - ok (вложенные synchronized на одном мониторе)
8. synchronized(Object1) { synchronized(Object2) } - may be dadlock

#### executors

1. executorService = Executors.newFixedThreadPool(5);  executorService.execute(()-> {})
2. Future<String> f = executorService.submit(()->{})
3. executorService.shutDown()

#### reordering

```
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

```
volatile помогает бороться с реордерингом

#### happens before

Набор правил (законов) которые гарантированно выполняются любой JVM

- однопоточный код выполняется в порядке как написан
- освобождение монитора происходит гарантировано раньше, чем его захват
- для volatile переменной - запись всегода происходит до последующего чтения
- старт потока всегда раньше, чем любое дествие внутри потока
- Thread terminate - любое действие в потоке всегда раньше, чем завершение потока
- Правило прерывания потока. Поток прерывается всегда точно раньше, чем второй поток увидет это
- Транзитивность: есил А случается раньше чем Б, а С случается раньше чем Б, то А точно всегда случается раньше чем С


