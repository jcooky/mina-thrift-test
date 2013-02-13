package com.github.jcooky.mina.thrift.test.utils;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.THsHaServer.Args;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class ThriftServer {
	public static final int PORT = 9090;
	public static final int SOCKET_TIMEOUT = 1000;

	private TProcessor processor;
	private TServer server;
	private TNonblockingServerSocket serverSocket;

	public ThriftServer(TProcessor processor) {
		this.processor = processor;
	}

	public void serve() throws Exception {
		serverSocket = new TNonblockingServerSocket(PORT);
		server = new THsHaServer(new Args(serverSocket).processor(processor)
				.protocolFactory(new TCompactProtocol.Factory())
				.transportFactory(new TFramedTransport.Factory()));

		new Thread() {
			public void run() {
				server.serve();
			}
		}.start();
	}

	public void stop() {
		server.stop();
		serverSocket.close();
	}
}
