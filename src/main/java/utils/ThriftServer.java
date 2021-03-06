package utils;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.THsHaServer.Args;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

import service.EchoServiceImpl;
import service.gen.EchoService;
import service.gen.EchoService.Iface;


public class ThriftServer {
	public static final int PORT = 9090;
	public static final int SOCKET_TIMEOUT = 60*1000;

	private TProcessor processor;
	private TServer server;
	private TNonblockingServerSocket serverSocket;

	public ThriftServer(TProcessor processor) {
		this.processor = processor;
	}

	public Thread serve() throws Exception {
		serverSocket = new TNonblockingServerSocket(PORT);
		server = new THsHaServer(new Args(serverSocket).processor(processor)
				.protocolFactory(new TCompactProtocol.Factory())
				.transportFactory(new TFramedTransport.Factory()));

		Thread t = new Thread() {
			public void run() {
				server.serve();
			}
		};
		t.start();
		return t;
	}

	public void stop() {
		server.stop();
		serverSocket.close();
	}
	
	public static void main(String []args) throws Exception {
		ThriftServer thriftServer = new ThriftServer(new EchoService.Processor<Iface>(new EchoServiceImpl()));
		try {
			thriftServer.serve().join();
		} finally {
			System.out.println("aaa");
			thriftServer.stop();
		}
	}
}
