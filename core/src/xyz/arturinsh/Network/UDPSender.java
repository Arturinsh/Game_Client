package xyz.arturinsh.Network;

import java.util.TimerTask;
import java.util.Date;

import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Network.Packets.PlayerPositionUpdate;

public class UDPSender extends TimerTask {

	private Client client;
	private GameWorld world;
	private int counter = 0;

	public UDPSender(Client _client, GameWorld _world) {
		client = _client;
		world = _world;
	}

	@Override
	public void run() {
		PlayerPositionUpdate posUpdate = new PlayerPositionUpdate();
		posUpdate.character = world.getUsersCharacterInstance().getCharacter();
		Vector3 position = world.getUsersCharacterInstance().getPosition();
		posUpdate.character.x = position.x;
		posUpdate.character.y = 0;
		posUpdate.character.z = position.z;
		posUpdate.character.r = (int) world.getUsersCharacterInstance().getRotation();
		posUpdate.timestamp = new Date();
		client.sendTCP(posUpdate);
		world.getUsersCharacterInstance().addMovementToBuffer(posUpdate);
		// System.out.println("Send " + posUpdate.timestamp.getTime());
		// System.out.println(posUpdate.character.x + " " +
		// posUpdate.character.z + " "
		// + posUpdate.character.r);
		// TestUDP test = new TestUDP();
		// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// Date date = new Date();
		// test.text = "Hello " + dateFormat.format(date);
		// client.sendTCP(test);
	}
}
