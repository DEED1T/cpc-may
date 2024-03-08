package assembly;

import composite.Assembly;
import composite.Client;
import composite.Server;

public class AssemblyImpl extends Assembly {

	/*
	@Override
	protected Runnable make_go() {
		
		return new Runnable() {
			
			@Override
			public void run() {
			}
		};
		
		
		return () -> {System.out.println("ça démarre");};
	}*/
	

	@Override
	protected Client make_cl() {
		return new ClientImpl();
	}

	@Override
	protected Server make_sv() {
		return new ServImpl();
	}
	
	public static void main(String[] args) {
		Assembly.Component comp = new AssemblyImpl().newComponent();
		comp.go().run();
	}
}