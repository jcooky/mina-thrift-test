package com.github.jcooky.mina.thrift.test.utils;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcooky.mina.thrift.TIoAcceptorServerTransport;
import com.github.jcooky.mina.thrift.TIoSessionTransport;
import com.github.jcooky.mina.thrift.TMinaServer;

public class ThriftMinaServer {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public static final int PORT = 9091;
	public static final int SOCKET_TIMEOUT = 60 * 1000;
	private TProcessor processor;

	private TServer server;
	private TServerTransport socket;

	public ThriftMinaServer(TProcessor processor) {
		this.processor = processor;
	}

	public void serve() throws Exception {
		logger.info("test log");

		socket = new TIoAcceptorServerTransport(PORT);
		server = new TMinaServer(new TMinaServer.Args(socket).processor(processor)
				.protocolFactory(new TCompactProtocol.Factory())
				.inputTransportFactory(new TIoSessionTransport.InputTransportFactory())
				.outputTransportFactory(new TTransportFactory()));

		server.serve();

	}

	public void stop() throws Exception {
		server.stop();
		socket.close();
	}
}
