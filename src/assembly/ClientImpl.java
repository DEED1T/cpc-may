package assembly;

import composite.Client;

public class ClientImpl extends Client {

	@Override
	protected Runnable make_gogo() {
		
		return new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Delegation vers le port requis");
				requires().requis().feu();
			}
		};
	}

}
