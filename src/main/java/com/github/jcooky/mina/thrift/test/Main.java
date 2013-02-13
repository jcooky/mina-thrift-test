package com.github.jcooky.mina.thrift.test;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.github.jcooky.mina.thrift.test.service.EchoServiceImpl;
import com.github.jcooky.mina.thrift.test.service.gen.EchoService;
import com.github.jcooky.mina.thrift.test.utils.TMinaServerTestRule;
import com.github.jcooky.mina.thrift.test.utils.ThriftMinaServer;
import com.github.jcooky.mina.thrift.test.utils.ThriftServer;
import com.github.jcooky.mina.thrift.test.utils.ThriftServerTestRule;

public class Main {
	private ThriftMinaServer minaServer;
	private ThriftServer thriftServer;
	private long deltaTime;
	
	private void initialize(TProcessor processor) throws Exception {
		if (thriftServer != null)
			thriftServer.stop();
		if (minaServer != null)
			minaServer.stop();
		
		thriftServer = new ThriftServer(processor);
		thriftServer.serve();
		minaServer = new ThriftMinaServer(processor);
		minaServer.serve();
	}
	
	private void destroy() throws Exception {
		if (thriftServer != null)
			thriftServer.stop();
		if (minaServer != null)
			minaServer.stop();
	}
	
	public void run() throws Exception {
		TProcessor processor = new EchoService.Processor<EchoService.Iface>(new EchoServiceImpl());
		processSingleThread(processor);
		processMultiThread(processor);
	}
	
	private void processMultiThread(TProcessor processor) throws Exception {
		int threadCount = 5;
		
		System.gc();
		
		System.out.println("+++ multi +++");
		try {
			initialize(processor);
			

			List<Thread> threadList = new LinkedList<Thread>();
			deltaTime = System.nanoTime();
			for (int i = 0; i < threadCount; ++i) {
				Thread t = new Thread() {
					public void run() {
						TTransport clientTransport = null;
						try {
							clientTransport = new TSocket("127.0.0.1", ThriftServer.PORT, ThriftServer.SOCKET_TIMEOUT);
							clientTransport = new TFramedTransport(clientTransport);
							TProtocol clientProtocol = new TCompactProtocol(clientTransport);

							// The transport must be opened before you can begin using
							clientTransport.open();
							EchoService.Iface client = new EchoService.Client(
									clientProtocol);
							
							String input = UUID.randomUUID().toString();
							String echoStr = client.echo(input);
						} catch (TException e) {
							throw new RuntimeException(e);
						} finally {
							if (clientTransport != null) 
								clientTransport.close();
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
	
			System.gc();
	
			threadList = new LinkedList<Thread>();
			deltaTime = System.nanoTime();
			for (int i = 0; i < threadCount; ++i) {
				Thread t = new Thread() {
					public void run() {
						TTransport clientTransport = null;
						try {
							clientTransport = new TSocket("127.0.0.1", ThriftServer.PORT, ThriftServer.SOCKET_TIMEOUT);
							clientTransport = new TFramedTransport(clientTransport);
							TProtocol clientProtocol = new TCompactProtocol(clientTransport);

							// The transport must be opened before you can begin using
							clientTransport.open();
							final EchoService.Iface client = new EchoService.Client(
									clientProtocol);
							String input = UUID.randomUUID().toString();
							String echoStr = client.echo(input);
						} catch (TException e) {
							throw new RuntimeException(e);
						} finally {
							if (clientTransport != null)
								clientTransport.close();
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
			destroy();
		}
	}
	
	private void processSingleThread(TProcessor processor) throws Exception {
		ThriftServerTestRule thriftServerTestRule = null;
		TMinaServerTestRule minaServerTestRule = null;
		TProtocol clientProtocol;
		long deltaTime;

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
	}
	
	public static void main(String[] args) throws Exception {
		new Main().run();
	}
}
