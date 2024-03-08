package assembly;

import composite.Server;

public class ServImpl extends Server {

	@Override
	protected Demarre make_fourni() {
		return (() -> {System.out.println("Vroum vroum");});	
	}

}
