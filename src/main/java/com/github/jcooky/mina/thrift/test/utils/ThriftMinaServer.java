package com.github.jcooky.mina.thrift.test.utils;

import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcooky.mina.thrift.TIoAcceptorServerTransport;
import com.github.jcooky.mina.thrift.TMinaServer;
import com.github.jcooky.mina.thrift.codec.TFrameProtocolCodecFactory;

public class ThriftMinaServer {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public static final int PORT = 9091;
	public static final int SOCKET_TIMEOUT = 60 * 1000;
	private TProcessor processor;

	private TServer server;

	public ThriftMinaServer(TProcessor processor) {
		this.processor = processor;
	}

	public void serve() throws Exception {
		logger.info("test log");

		NioSocketAcceptor acceptor;
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		acceptor.setReuseAddress(true);

		acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));

		acceptor.getFilterChain().addLast("thread", new ExecutorFilter());
		acceptor.getFilterChain().addLast(TIoAcceptorServerTransport.CODEC_NAME,
				new ProtocolCodecFilter(new TFrameProtocolCodecFactory()));

		server = new TMinaServer(new TMinaServer.Args(new TIoAcceptorServerTransport(acceptor)).processor(processor)
				.protocolFactory(new TCompactProtocol.Factory()));

		server.serve();

	}

	public void stop() throws Exception {
		server.stop();
	}
}
