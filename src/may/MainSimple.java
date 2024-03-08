package may;

import demoSimple.Hello;

public class MainSimple {
	public static void main(String[] args) {
		
		Hello.Component comp = new HelloImpl().newComponent();
		comp.say().talk("world");
	}
}
