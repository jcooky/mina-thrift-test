package com.github.jcooky.mina.thrift.test;

import com.github.jcooky.mina.thrift.test.service.EchoServiceImpl;
import com.github.jcooky.mina.thrift.test.service.gen.EchoService;
import com.github.jcooky.mina.thrift.test.utils.TMinaServerTestRule;
import com.github.jcooky.mina.thrift.test.utils.ThriftServerTestRule;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: jcooky
 * Date: 13. 2. 12.
 * Time: 오후 10:19
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        TProcessor processor;
        ThriftServerTestRule thriftServerTestRule = null;
        TMinaServerTestRule minaServerTestRule = null;
        TProtocol clientProtocol;
        long deltaTime;

        processor = new EchoService.Processor<EchoService.Iface>(new EchoServiceImpl());
        try {
            thriftServerTestRule = new ThriftServerTestRule(processor);
            thriftServerTestRule.starting();
            clientProtocol = thriftServerTestRule.getClientProtocol();
            EchoService.Iface client = new EchoService.Client(clientProtocol);

            String input = UUID.randomUUID().toString();
            deltaTime = System.nanoTime();
            String echoStr = client.echo(input);
            deltaTime -= System.nanoTime() - deltaTime;
            System.out.println("thrift time : \t\t" + deltaTime);
        } finally {
            if (thriftServerTestRule != null)
                thriftServerTestRule.finished();
        }

        System.gc();

        try {
            minaServerTestRule = new TMinaServerTestRule(processor);
            minaServerTestRule.starting();
            clientProtocol = minaServerTestRule.getClientProtocol();
            EchoService.Iface client = new EchoService.Client(clientProtocol);

            String input = UUID.randomUUID().toString();
            deltaTime = System.nanoTime();
            String echoStr = client.echo(input);
            deltaTime -= System.nanoTime() - deltaTime;
            System.out.println("mina-thrift time : \t" + deltaTime);
        } finally {
            if (minaServerTestRule != null)
                minaServerTestRule.finished();
        }
        int threadCount = 5;
        System.gc();
        System.out.println("+++ multi +++");
        try {
            thriftServerTestRule = new ThriftServerTestRule(processor);
            thriftServerTestRule.starting();
            clientProtocol = thriftServerTestRule.getClientProtocol();
            final EchoService.Iface client = new EchoService.Client(clientProtocol);

            List<Thread> threadList = new LinkedList<Thread>();
            deltaTime = System.nanoTime();
            for (int i = 0 ; i < threadCount ; ++i) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            String input = UUID.randomUUID().toString();
                            String echoStr = client.echo(input);
                        } catch(TException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                threadList.add(t);
                t.start();
            }
            for (Thread t : threadList) {
                t.join();
            }
            deltaTime -= System.nanoTime() - deltaTime;
            System.out.println("thrift time : \t" + deltaTime);
        } finally {
            if (thriftServerTestRule != null)
                thriftServerTestRule.finished();
        }

        System.gc();

        try {
            minaServerTestRule = new TMinaServerTestRule(processor);
            minaServerTestRule.starting();
            clientProtocol = minaServerTestRule.getClientProtocol();
            final EchoService.Iface client = new EchoService.Client(clientProtocol);

            List<Thread> threadList = new LinkedList<Thread>();
            deltaTime = System.nanoTime();
            for (int i = 0 ; i < threadCount ; ++i) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            String input = UUID.randomUUID().toString();
                            String echoStr = client.echo(input);
                        } catch(TException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                threadList.add(t);
                t.start();
            }
            for (Thread t : threadList) {
                t.join();
            }
            deltaTime -= System.nanoTime() - deltaTime;
            System.out.println("mina-thrift time : \t" + deltaTime);
        } finally {
            if (minaServerTestRule != null)
                minaServerTestRule.finished();
        }
    }
}
