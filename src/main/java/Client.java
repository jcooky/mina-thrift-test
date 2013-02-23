

import org.apache.thrift.transport.TTransportException;

import service.gen.EchoService;


public interface Client {
	public void connect() throws TTransportException;
	public void close();
	public EchoService.Iface getIface();
}
