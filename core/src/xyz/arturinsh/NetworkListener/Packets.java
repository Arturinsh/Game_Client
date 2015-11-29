package xyz.arturinsh.NetworkListener;

import xyz.arturinsh.GameObjects.CharacterClass;

public class Packets {
	public static class LogIn {
		public String userName;
		public String password;
	}

	public static class Register {
		public String userName;
		public String password;
	}

	public static class LogInSuccess {
	}

	public static class RegisterSuccess {
	}

	public static class LogInFailed {
	}

	public static class RegisterFailed {
	}

	public static class AddPlayer {
		public String username;
	}

	public static class RemovePlayer {
		public String username;
	}

	public static class UserCharacter {
		public String charName;
		public CharacterClass charClass;
	}

	public static class CharacterCreateSuccess {

	}

	public static class CharacterCreateFailed {
	}
}
