package albert.user_interface.states;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import albert.user_interface.MainWindow;
import albert.user_interface.Mutable;
import albert.user_interface.Window;
import albert.user_interface.states.report_builder.ReportBuilderState;

public class StateManager
{
	/**
	 * Enum of specific states.
	 * Use this enum to tell Launcher which state to set and where.
	 * @author Niv Kor
	 */
	public static enum Substate {
		REPORT_BUILDER(ReportBuilderState.class);
		
		private Class<? extends State> stateClass;
		
		private Substate(Class<? extends State> c) {
			this.stateClass = c;
		}
		
		/**
		 * Create an instance of the state.
		 * Every State instance needs a mutable window where it has the room to strech,
		 * and cannot exist without one.
		 * A state can take place in more than one window simultaneously.
		 * @param window - The window the will contain the state instance
		 * @return an instance of the state that fits the size of the argument window.
		 */
		public State createInstance(Window window) {
			//create instance
			try { return stateClass.asSubclass(State.class).getConstructor(Window.class).newInstance(window); }
			catch (Exception e) {
				System.err.println("Cannot create an instance of class " + stateClass.getName());
				
				//initialize the state in a different approach for debugging purposes
				try { printStateStackTrace(stateClass); }
				catch(Exception ex) { ex.printStackTrace(); }
			}
			
			return null;
		}
		
		/**
		 * @return a reflection of the Substate's compatible class.
		 */
		public Class<? extends State> getStateClass() { return stateClass; }
		
		private static void printStateStackTrace(Class<? extends State> stateClass) throws Exception {
			Window testWindow = new MainWindow();
			
			switch(stateClass.getSimpleName()) {
				case "ReportBuilderState": new ReportBuilderState(testWindow); break;
			}
		}
	}
	
	/**
	 * A way to save both the window and the state that's running on it, and easily find it.
	 * WindowCache should be created everytime a state is applied to any window,
	 * and should be deleted everytime the state on that window is changed,
	 * or perhaps when that window is closed. 
	 * @author Korach
	 */
	public static class WindowCache
	{
		private Mutable window;
		private State currentState;
		
		public WindowCache(Mutable w) {
			this.window = w;
		}
		
		public Mutable getWindow() { return window; }
		public State getCurrentState() { return currentState; }
		public void setCurrentState(State s) { currentState = s; }
	}
	
	private static List<WindowCache> windowCacheList = new ArrayList<WindowCache>();
	
	/**
	 * Set a state on a window.
	 * @param window - The window that needs to contain the state
	 * @param substate - The requested state to set
	 */
	public static void setState(Window window, Substate substate) {
		if (substate == null) return;
		
		State instance = substate.createInstance(window);
		
		//find the correct window cache
		WindowCache tempCache = null;
		for (WindowCache wm : windowCacheList)
			if (wm.getWindow() == window) tempCache = wm;
		
		//create a new window cache if couldn't find it
		if (tempCache == null) {
			tempCache = new WindowCache(window);
			windowCacheList.add(tempCache);
		}
		
		//apply state to window and update its window memory
		window.applyState(tempCache.getCurrentState(), instance);
		tempCache.setCurrentState(instance);
	}
	
	/**
	 * Get the cahce that contains a currenly open window with some state.
	 * @param window - Currently open window to get its cache
	 * @return cache of that window.
	 */
	public static WindowCache getWindowCache(Window window) {
		for (WindowCache wc : windowCacheList)
			if (wc.getWindow() == window) return wc;
		
		return null;
	}
	
	/**
	 * Test all the states for debugging purposes.
	 * If a state can't be ran correctly, a stack trace will be printed.
	 * This test is needed because when trying to create an instance
	 * of a state using reflection (using Substate.createInstance()),
	 * the stack trace that's printed is not related to the real bugs in the state,
	 * but rather to the failed reflection action. 
	 */
	@Test
	public void launchingTest() {
		for (Substate substate : Substate.values()) {
			try { Substate.printStateStackTrace(substate.getStateClass()); }
			catch(Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
	}
}