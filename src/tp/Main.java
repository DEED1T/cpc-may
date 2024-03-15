package tp;

import simple.Simple;

public class Main {
	public static void main(String[] args) {
		
		Simple.Component simplComp = new SimpleImpl().newComponent();
		Simple.Component simplCountComp = new SimplCount().newComponent();		
		
		simple.Assembly.Component assemblyComp = new AssemblyImpl().newComponent();
		
		simplCountComp.starter().go();
		simplComp.starter().go();
		simplCountComp.starter().go();
		
		System.out.println("---");
		
		assemblyComp.assembly().run();
			
	}
}