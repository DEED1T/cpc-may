package tp;

import simple.Simple;

public class SimplCount extends Simple{
	
	int nbAppels = 0;
	
	@Override
	protected Start make_starter() {
		return new Start() {
			
			@Override
			public void go() {
				nbAppels++;
				System.out.println("Appel méthode n°"+ nbAppels);
				
			}
		};
	}

}
