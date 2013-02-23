package utils;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.EchoServiceImpl;
import service.gen.EchoService;
import service.gen.EchoService.Iface;

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

	public NioSocketAcceptor serve() throws Exception {
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

		return acceptor;
	}

	public void stop() throws Exception {
		server.stop();
	}
	
	public static void main(String []args) throws Exception {
		ThriftMinaServer server = new ThriftMinaServer(new EchoService.Processor<Iface>(new EchoServiceImpl()));
		try {
			server.serve();
			Thread.currentThread().join();			
		} finally {
			server.stop();
		}
	}
}
