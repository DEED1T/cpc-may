package tp;

import simple.Client;

public class ClientImpl extends Client{
	
	@Override
	protected Runnable make_letsgo() {
		
		return new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Service requis");
				requires().demarreur().go();
			}
		};
	}

}
