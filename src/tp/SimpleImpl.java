package tp;

import simple.Simple;

public class SimpleImpl extends Simple{

	@Override
	protected Start make_starter() {
		return new Start() {
			
			@Override
			public void go() {
				System.out.println("Appel méthode classique");
			}
		};
	}

	
}
