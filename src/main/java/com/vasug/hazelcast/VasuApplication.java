package com.vasug.hazelcast;

import com.hazelcast.core.*;
import com.hazelcast.config.*;

import java.util.Map;

/*
 * This is Hazelcast Cache class
 */
public class VasuApplication {
	private final Map<String, Integer> appState_;
	private HazelcastInstance instance_;
	
	public VasuApplication() {
		Config cfg = new Config();
		//HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
		instance_ = Hazelcast.newHazelcastInstance(cfg);
		appState_ = instance_.getMap("VasuApplication");
	}
	
	public HazelcastInstance getHzInstance() {
		return instance_;
	}
	/**
	 * Put the appname in the cache and increase the node count, indicating it is Started
	 * @param appName
	 */
	public void start(String appName) {
		appState_.put(appName, getNodeCount(appName)+1);
	}
	
	/**
	 * Remove the app name from cache
	 * @param appName
	 */
	public void stop (String appName) {
		appState_.put(appName,getNodeCount(appName)-1);
	}

	/**
	 * 
	 * @param appName
	 * @return Return true if the app name exists in the cache map
	 */
	public boolean isStarted(String appName) {
		return appState_.containsKey(appName);
	}	
	
	/**
	 * 
	 * @param appName
	 * @return Return int numberOfNodes that are already started
	 */
	public int getNodeCount(String appName) {
		return (appState_.containsKey(appName) ? appState_.get(appName) : 0);
	}
}
