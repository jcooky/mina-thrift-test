

import java.util.UUID;

import service.EchoServiceImpl;
import service.gen.EchoService;
import service.gen.EchoService.Iface;
import utils.ThriftMinaServer;
import utils.ThriftServer;

public class Main {
	
	public static void main(String []args) throws Exception {
		Client client = null;
		if (args[0].equalsIgnoreCase("mina")) {
			client = new MinaClient();
		} else if (args[0].equalsIgnoreCase("thrift")) {
			client = new ThriftClient();
		} else if (args[0].equalsIgnoreCase("server")) {
			if (args[1].equalsIgnoreCase("mina")) {
				new ThriftMinaServer(new EchoService.Processor<Iface>(new EchoServiceImpl())).serve();
			} else if (args[1].equalsIgnoreCase("thrift")){
				new ThriftServer(new EchoService.Processor<Iface>(new EchoServiceImpl())).serve();
			}			
		} else {
			throw new Exception();
		}
		
		if (client != null) {
			try {
				client.connect();
				client.getIface().echo(UUID.randomUUID().toString());
			} finally {
				client.close();
			}
		}
	}
}
