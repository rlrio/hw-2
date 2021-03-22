package se.rlrio;

import ru.pflb.mq.dummy.exception.DummyException;
import ru.pflb.mq.dummy.implementation.ConnectionImpl;
import ru.pflb.mq.dummy.interfaces.Connection;
import ru.pflb.mq.dummy.interfaces.Destination;
import ru.pflb.mq.dummy.interfaces.Producer;
import ru.pflb.mq.dummy.interfaces.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MyApp {
    public static void main(String[] args) {

        try {
            //sendMessagesFromFile(args[0], "Сообщения");
            sendMessagesInfiniteLoop(args[0], "Сообщения");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Не задан путь к файлу. Укажите путь к файлу в аргументах командной строки.");
        }

    }

    public static void sendMessagesInfiniteLoop(String path, String queueName) {
        Connection connection = new ConnectionImpl();
        try (connection; Session session = connection.createSession(true)) {
            Destination destination = session.createDestination(queueName);
            Producer producer = session.createProducer(destination);
            List<String> queue = messagesFromFile(path);
            while (true) {
                printMessages(queue, producer);
            }
        } catch (DummyException e) {
            System.out.println("Что-то пошло не так. Не установлено соединение.");
        }

    }

    public static void sendMessagesFromFile(String path, String queueName) {
        sendAll(messagesFromFile(path), queueName);
    }

    private static List<String> messagesFromFile(String path) {
        List<String> queue = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) System.out.println("Такого файла не существует.");
        else {
            try {
                queue.addAll(Files.readAllLines(Paths.get(path)));
            } catch (IOException e) {
                System.out.println("Что-то пошло не так. Проблема с чтением файла.");
            }
        }
        return queue;
    }

    private static void sendAll(List<String> list, String queueName) {
        Connection connection = new ConnectionImpl();
        try (connection; Session session = connection.createSession(true)) {
            Destination destination = session.createDestination(queueName);
            Producer producer = session.createProducer(destination);
            printMessages(list, producer);
        } catch (DummyException e) {
            System.out.println("Что-то пошло не так. Не установлено соединение.");
        }
    }

    private static void printMessages(List<String> list, Producer producer) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            producer.send(iterator.next());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Выполнение программы было прервано.");
            }
        }
    }

}
