package simple;

import tp.Start;

@SuppressWarnings("all")
public abstract class Client {
  public interface Requires {
    /**
     * This can be called by the implementation to access this required port.
     * 
     */
    public Start demarreur();
  }

  public interface Component extends Client.Provides {
  }

  public interface Provides {
    /**
     * This can be called to access the provided port.
     * 
     */
    public Runnable letsgo();
  }

  public interface Parts {
  }

  public static class ComponentImpl implements Client.Component, Client.Parts {
    private final Client.Requires bridge;

    private final Client implementation;

    public void start() {
      this.implementation.start();
      this.implementation.started = true;
    }

    protected void initParts() {
      
    }

    protected void init_letsgo() {
      assert this.letsgo == null: "This is a bug.";
      this.letsgo = this.implementation.make_letsgo();
      if (this.letsgo == null) {
      	throw new RuntimeException("make_letsgo() in simple.Client shall not return null.");
      }
    }

    protected void initProvidedPorts() {
      init_letsgo();
    }

    public ComponentImpl(final Client implem, final Client.Requires b, final boolean doInits) {
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

    private Runnable letsgo;

    public Runnable letsgo() {
      return this.letsgo;
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

  private Client.ComponentImpl selfComponent;

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
  protected Client.Provides provides() {
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
  protected abstract Runnable make_letsgo();

  /**
   * This can be called by the implementation to access the required ports.
   * 
   */
  protected Client.Requires requires() {
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
  protected Client.Parts parts() {
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
  public synchronized Client.Component _newComponent(final Client.Requires b, final boolean start) {
    if (this.init) {
    	throw new RuntimeException("This instance of Client has already been used to create a component, use another one.");
    }
    this.init = true;
    Client.ComponentImpl  _comp = new Client.ComponentImpl(this, b, true);
    if (start) {
    	_comp.start();
    }
    return _comp;
  }
}
