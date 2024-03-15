package tp;

import simple.Assembly;
import simple.Client;
import simple.Simple;

public class AssemblyImpl extends Assembly{

	@Override
	protected Client make_client() {
		// TODO Auto-generated method stub
		return new ClientImpl();
	}

	@Override
	protected Simple make_simpl() {
		// TODO Auto-generated method stub
		return new SimplCount(); //Sinon SimplImpl en fonction de l'implémentation qu'on veut sur ce composite
	}


}
