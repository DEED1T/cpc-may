package tp;

import simple.Traceur;

public class TraceurImpl extends Traceur {

	@Override
	protected Start make_out() {
		// TODO Auto-generated method stub
		return new Start() {
			@Override
			public void go() {
				
				System.out.println("Dans traceur : ");
				requires().in().go();;
			
			}
		};
	}

}
