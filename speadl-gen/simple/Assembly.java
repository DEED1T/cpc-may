package simple;

import tp.Start;

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
    public Runnable assembly();
  }

  public interface Parts {
    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public Client.Component client();

    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public Simple.Component simpl();

    /**
     * This can be called by the implementation to access the part and its provided ports.
     * It will be initialized after the required ports are initialized and before the provided ports are initialized.
     * 
     */
    public Traceur.Component traceur();
  }

  public static class ComponentImpl implements Assembly.Component, Assembly.Parts {
    private final Assembly.Requires bridge;

    private final Assembly implementation;

    public void start() {
      assert this.client != null: "This is a bug.";
      ((Client.ComponentImpl) this.client).start();
      assert this.simpl != null: "This is a bug.";
      ((Simple.ComponentImpl) this.simpl).start();
      assert this.traceur != null: "This is a bug.";
      ((Traceur.ComponentImpl) this.traceur).start();
      this.implementation.start();
      this.implementation.started = true;
    }

    private void init_client() {
      assert this.client == null: "This is a bug.";
      assert this.implem_client == null: "This is a bug.";
      this.implem_client = this.implementation.make_client();
      if (this.implem_client == null) {
      	throw new RuntimeException("make_client() in simple.Assembly should not return null.");
      }
      this.client = this.implem_client._newComponent(new BridgeImpl_client(), false);
    }

    private void init_simpl() {
      assert this.simpl == null: "This is a bug.";
      assert this.implem_simpl == null: "This is a bug.";
      this.implem_simpl = this.implementation.make_simpl();
      if (this.implem_simpl == null) {
      	throw new RuntimeException("make_simpl() in simple.Assembly should not return null.");
      }
      this.simpl = this.implem_simpl._newComponent(new BridgeImpl_simpl(), false);
    }

    private void init_traceur() {
      assert this.traceur == null: "This is a bug.";
      assert this.implem_traceur == null: "This is a bug.";
      this.implem_traceur = this.implementation.make_traceur();
      if (this.implem_traceur == null) {
      	throw new RuntimeException("make_traceur() in simple.Assembly should not return null.");
      }
      this.traceur = this.implem_traceur._newComponent(new BridgeImpl_traceur(), false);
    }

    protected void initParts() {
      init_client();
      init_simpl();
      init_traceur();
    }

    protected void init_assembly() {
      // nothing to do here
    }

    protected void initProvidedPorts() {
      init_assembly();
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

    public Runnable assembly() {
      return this.client().
      letsgo()
      ;
    }

    private Client.Component client;

    private Client implem_client;

    private final class BridgeImpl_client implements Client.Requires {
      public final Start demarreur() {
        return Assembly.ComponentImpl.this.traceur().
        out()
        ;
      }
    }

    public final Client.Component client() {
      return this.client;
    }

    private Simple.Component simpl;

    private Simple implem_simpl;

    private final class BridgeImpl_simpl implements Simple.Requires {
    }

    public final Simple.Component simpl() {
      return this.simpl;
    }

    private Traceur.Component traceur;

    private Traceur implem_traceur;

    private final class BridgeImpl_traceur implements Traceur.Requires {
      public final Start in() {
        return Assembly.ComponentImpl.this.simpl().
        starter()
        ;
      }
    }

    public final Traceur.Component traceur() {
      return this.traceur;
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
  protected abstract Client make_client();

  /**
   * This should be overridden by the implementation to define how to create this sub-component.
   * This will be called once during the construction of the component to initialize this sub-component.
   * 
   */
  protected abstract Simple make_simpl();

  /**
   * This should be overridden by the implementation to define how to create this sub-component.
   * This will be called once during the construction of the component to initialize this sub-component.
   * 
   */
  protected abstract Traceur make_traceur();

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
