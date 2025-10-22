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

1. методы в классе можно делать synchronized, но это бует значить что мы все выстроим в один поток по сути
2. надо делать synchronized(Object) - более правильный путь
3. Лучше всего синхронизироваться просто на Object, пример с синхронизацией по Integer (unmodifylable) ++ дает новый объект и трендец
4. Нельзя синхронизироваться на кэшируемых объектах, строках и интежерах (-127 до 128)
5. volatile уберает процессорный поточный кэш на переменную
6. synchronized(this) { synchronized(this) } - ok
7. synchronized(Object1) { synchronized(Object2) } - may be dadlock

#### executors

1. executorService = Executors.newFixedThreadPool(5);  executorService.execute(()-> {})
2. Future<String> f = executorService.submit(()->{})
3. executorService.shutDown()

