package composite;

import assembly.Demarre;

@SuppressWarnings("all")
public abstract class Assembly {
  public interface Requires {
  }

  public interface Component extends Assembly.Provides {
  }

  public interface Provides {
    /**
     * This can be called to access the provided port.
     * 
     */
    public Runnable go();
  }

  public interface Parts {
    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public Client.Component cl();

    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public Server.Component sv();
  }

  public static class ComponentImpl implements Assembly.Component, Assembly.Parts {
    private final Assembly.Requires bridge;

    private final Assembly implementation;

    public void start() {
      assert this.cl != null: "This is a bug.";
      ((Client.ComponentImpl) this.cl).start();
      assert this.sv != null: "This is a bug.";
      ((Server.ComponentImpl) this.sv).start();
      this.implementation.start();
      this.implementation.started = true;
    }

    private void init_cl() {
      assert this.cl == null: "This is a bug.";
      assert this.implem_cl == null: "This is a bug.";
      this.implem_cl = this.implementation.make_cl();
      if (this.implem_cl == null) {
      	throw new RuntimeException("make_cl() in composite.Assembly should not return null.");
      }
      this.cl = this.implem_cl._newComponent(new BridgeImpl_cl(), false);
    }

    private void init_sv() {
      assert this.sv == null: "This is a bug.";
      assert this.implem_sv == null: "This is a bug.";
      this.implem_sv = this.implementation.make_sv();
      if (this.implem_sv == null) {
      	throw new RuntimeException("make_sv() in composite.Assembly should not return null.");
      }
      this.sv = this.implem_sv._newComponent(new BridgeImpl_sv(), false);
    }

    protected void initParts() {
      init_cl();
      init_sv();
    }

    protected void init_go() {
      // nothing to do here
    }

    protected void initProvidedPorts() {
      init_go();
    }

    public ComponentImpl(final Assembly implem, final Assembly.Requires b, final boolean doInits) {
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

    public Runnable go() {
      return this.cl().
      gogo()
      ;
    }

    private Client.Component cl;

    private Client implem_cl;

    private final class BridgeImpl_cl implements Client.Requires {
      public final Demarre requis() {
        return Assembly.ComponentImpl.this.sv().
        fourni()
        ;
      }
    }

    public final Client.Component cl() {
      return this.cl;
    }

    private Server.Component sv;

    private Server implem_sv;

    private final class BridgeImpl_sv implements Server.Requires {
    }

    public final Server.Component sv() {
      return this.sv;
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

  private Assembly.ComponentImpl selfComponent;

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
  protected Assembly.Provides provides() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("provides() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if provides() is needed to initialise the component.");
    }
    return this.selfComponent;
  }

  /**
   * This can be called by the implementation to access the required ports.
   * 
   */
  protected Assembly.Requires requires() {
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
  protected Assembly.Parts parts() {
    assert this.selfComponent != null: "This is a bug.";
    if (!this.init) {
    	throw new RuntimeException("parts() can't be accessed until a component has been created from this implementation, use start() instead of the constructor if parts() is needed to initialise the component.");
    }
    return this.selfComponent;
  }

  /**
   * This should be overridden by the implementation to define how to create this sub-component.
   * This will be called once during the construction of the component to initialize this sub-component.
   * 
   */
  protected abstract Client make_cl();

  /**
   * This should be overridden by the implementation to define how to create this sub-component.
   * This will be called once during the construction of the component to initialize this sub-component.
   * 
   */
  protected abstract Server make_sv();

  /**
   * Not meant to be used to manually instantiate components (except for testing).
   * 
   */
  public synchronized Assembly.Component _newComponent(final Assembly.Requires b, final boolean start) {
    if (this.init) {
    	throw new RuntimeException("This instance of Assembly has already been used to create a component, use another one.");
    }
    this.init = true;
    Assembly.ComponentImpl  _comp = new Assembly.ComponentImpl(this, b, true);
    if (start) {
    	_comp.start();
    }
    return _comp;
  }

  /**
   * Use to instantiate a component from this implementation.
   * 
   */
  public Assembly.Component newComponent() {
    return this._newComponent(new Assembly.Requires() {}, true);
  }
}
