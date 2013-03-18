

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import service.gen.EchoService;
import service.gen.EchoService.Iface;
import utils.ThriftMinaServer;


public class MinaClient implements Client {
	private TTransport transport;
	private Iface iface;

	public void connect() throws TTransportException {
		transport = new TSocket("localhost", ThriftMinaServer.PORT);
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

	public static void main(String []args) throws InterruptedException {
		int count = 100;
		if (args.length == 1)
			count = Integer.parseInt(args[0]);
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					MinaClient minaClient = new MinaClient();
					minaClient.connect();
					Iface echoService = minaClient.getIface();
					echoService.echo(UUID.randomUUID().toString());
					minaClient.close();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		};
		
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0 ; i < count ; ++i) {
			threads.add(new Thread(runnable));
		}
		
		long deltaTime = System.nanoTime();
		for (Thread t : threads) {
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
		deltaTime = System.nanoTime() - deltaTime;
		
		System.out.println("DeltaTime : " + deltaTime);
	}
}
