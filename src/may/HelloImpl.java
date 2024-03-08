package may;

import demoSimple.Hello;

public class HelloImpl extends Hello {

	@Override
	protected HelloWorld make_say() {
		return new HelloWorld() {
			
			@Override
			public void talk(String message) {
				System.out.println("Hello"+message);
				
			}
		};
	}
	
	
}
