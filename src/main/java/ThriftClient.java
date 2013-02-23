

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import service.gen.EchoService;
import service.gen.EchoService.Iface;
import utils.ThriftServer;


public class ThriftClient implements Client {
	private TTransport transport;
	private Iface iface;

	public void connect() throws TTransportException {
		transport = new TSocket("localhost", ThriftServer.PORT);
		transport = new TFramedTransport(transport);
		transport.open();
		TProtocol protocol = new TCompactProtocol(transport);
		iface = new EchoService.Client(protocol);
	}

	public void close() {
		transport.close();
	}

	public Iface getIface() {
		return iface;
	}
}
