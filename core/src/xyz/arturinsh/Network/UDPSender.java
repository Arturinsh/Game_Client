package xyz.arturinsh.Network;

import java.util.TimerTask;

import com.esotericsoftware.kryonet.Client;

public class UDPSender extends TimerTask {

	private Client client;

	public UDPSender(Client _client) {
		client = _client;
	}

	@Override
	public void run() {
//		TestUDP test = new TestUDP();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Date date = new Date();
//		test.text = "Hello " + dateFormat.format(date);
//		client.sendTCP(test);
	}
}
