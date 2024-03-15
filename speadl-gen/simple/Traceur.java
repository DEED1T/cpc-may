package simple;

import tp.Start;

@SuppressWarnings("all")
public abstract class Traceur {
  public interface Requires {
    /**
     * This can be called by the implementation to access this required port.
     * 
     */
    public Start in();
  }

  public interface Component extends Traceur.Provides {
  }

  public interface Provides {
    /**
     * This can be called to access the provided port.
     * 
     */
    public Start out();
  }

  public interface Parts {
  }

  public static class ComponentImpl implements Traceur.Component, Traceur.Parts {
    private final Traceur.Requires bridge;

    private final Traceur implementation;

    public void start() {
      this.implementation.start();
      this.implementation.started = true;
    }

    protected void initParts() {
      
    }

    protected void init_out() {
      assert this.out == null: "This is a bug.";
      this.out = this.implementation.make_out();
      if (this.out == null) {
      	throw new RuntimeException("make_out() in simple.Traceur shall not return null.");
      }
    }

    protected void initProvidedPorts() {
      init_out();
    }

    public ComponentImpl(final Traceur implem, final Traceur.Requires b, final boolean doInits) {
      this.bridge = b;
      this.implementation = implem;
      
      assert implem.selfComponent == null: "This is a bug.";
      implem.selfComponent = this;
      
      // prevent them to be called twice if we are in
      // a specialized component: only the last of the
      // hierarchy will call them after everything is initialised
      if (doInits) {
      	initParts();
      	initProvidedPorts();
      }
    }

    private Start out;

    public Start out() {
      return this.out;
    }
  }

  /**
   * Used to check that two components are not created from the same implementation,
   * that the component has been started to call requires(), provides() and parts()
   * and that the component is not started by hand.
   * 
   */
  private boolean init = false;;

  /**
   * Used to check that the component is not started by hand.
   * 
   */
  private boolean started = false;;

  private Traceur.ComponentImpl selfComponent;

  /**
   * Can be overridden by the implementation.
   * It will be called automatically after the component has been instantiated.
   * 
   */
  protected void start() {
    if (!this.init || this.started) {
    	throw new RuntimeException("start() should not be called by hand: to create a new component, use newComponent().");
    }
  }

  /**
   * This can be called by the implementation to access the provided ports.
   * 
   */
  protected Traceur.Provides provides() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("provides() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if provides() is needed to initialise the component.");
    }
    return this.selfComponent;
  }

  /**
   * This should be overridden by the implementation to define the provided port.
   * This will be called once during the construction of the component to initialize the port.
   * 
   */
  protected abstract Start make_out();

  /**
   * This can be called by the implementation to access the required ports.
   * 
   */
  protected Traceur.Requires requires() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("requires() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if requires() is needed to initialise the component.");
    }
    return this.selfComponent.bridge;
  }

  /**
   * This can be called by the implementation to access the parts and their provided ports.
   * 
   */
  protected Traceur.Parts parts() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("parts() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if parts() is needed to initialise the component.");
    }
    return this.selfComponent;
  }

  /**
   * Not meant to be used to manually instantiate components (except for testing).
   * 
   */
  public synchronized Traceur.Component _newComponent(final Traceur.Requires b, final boolean start) {
    if (this.init) {
    	throw new RuntimeException("This instance of Traceur has already been used to create a component, use another one.");
    }
    this.init = true;
    Traceur.ComponentImpl  _comp = new Traceur.ComponentImpl(this, b, true);
    if (start) {
    	_comp.start();
    }
    return _comp;
  }
}
