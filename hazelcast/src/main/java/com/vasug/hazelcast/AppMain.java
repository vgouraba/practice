package com.vasug.hazelcast;

import java.util.concurrent.TimeUnit;

public class AppMain {
	VasuApplication cache;
	String name;
	int maxNodeCount;
	boolean running;
	
	public VasuApplication getCache() {
		return cache;
	}

	public void setCache(VasuApplication cache) {
		this.cache = cache;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxNodeCount() {
		return maxNodeCount;
	}

	public void setMaxNodeCount(int maxNodeCount) {
		this.maxNodeCount = maxNodeCount;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	//constructor
	public AppMain() {		
		cache = new VasuApplication();
		name = "TestApp";
		maxNodeCount = 10;
		running = false;
	}
	
	//Check if the node can be started, if so, start it 
	public boolean startNode() {
		String appName = this.getName();
		
		//Before starting, check if we are already at Max Node state
		int nodesRunning = this.getCache().getNodeCount(appName);
		if (nodesRunning >= this.getMaxNodeCount()) {
			System.out.println (">>>Max number of nodes are already running. Exiting!!!");
			//System.exit(-1);
			return false;
		}
		
		this.getCache().start(appName);
		this.setRunning(true);
		
		//get count again, just to make sure. There's still a window for sync error to get a wrong value
		//need to understand if Hazelcast can provide some sort of synchronization for updates
		nodesRunning = this.getCache().getNodeCount(appName); 
		if (nodesRunning == 1) { //meaning this is the first instance
			System.out.println(">>>We are started!!!");				
		}

		return true;
	}
	
	//Stop the node, reset the cache counters
	public void stopNode() {
		String appName = this.getName();
		//stop it
		if (this.isRunning()) {
			this.getCache().stop(appName);
			this.setRunning(false);
		}
	}
	
	public void runMainLoop() throws InterruptedException {
		//Run the main thread in a loop. 
		//this is where the actual application runs. For now, just do a while loop with sleep
		//normally run the main thread in a while loop, but for testing purpose, I have it in a for loop
		// so as to decrement the node counter
		//while (true) {
		for (int i=0; i<5; i++) {
			System.out.println("Inside Main loop");
			//Sleep for 3 secs for testing purpose
			TimeUnit.SECONDS.sleep(5);			
		}
	}

	/*
	 * This is a driver program to test the Hazelcast caching
	 * It runs in a while loop, checks if the App is already started (by invoking cache)
	 *   For testing purpose, a Stop/evict event is also added
	 */
	public static void main(String[] args) throws InterruptedException {
		AppMain app = new AppMain();
		String appName = app.getName(); //get once and remember it
		
		//Create a Hook to capture Ctrl-C
		//This is not reliable...getting Hazelcast exception
		/*
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				app.stop(name);
			    System.out.println("Exited! Number Of Running nodes =" + app.getNodeCount(name));
			}
		});
		*/

		//simply done/exit if we are unable to start this node
		if (!app.startNode()) {
			app.getCache().getHzInstance().getLifecycleService().shutdown();
			return;
		}
		
		//Run the main thread in a loop. 
		app.runMainLoop();
		
		// now that we are done with main loop, stop the node
		app.stopNode();
		System.out.println ("Done. Remaining # of running nodes =" + app.getCache().getNodeCount(appName));
		app.getCache().getHzInstance().getLifecycleService().shutdown();
	}
	
}
