package com.dist.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A singleton class which creates a thread pool and executes threads
 * @author Dean Gaffney
 *
 */
public class ThreadPool {
	
	private static ThreadPool threadPool;
	private ExecutorService executor;
	
	private ThreadPool(){
		executor = Executors.newFixedThreadPool(10);
	}
	
	public static ThreadPool getInstance(){
		if(threadPool == null)
			threadPool = new ThreadPool();
		return threadPool;
	}
	
	public void execute(ServerThread clientThread){
		executor.execute(clientThread);
	}
}
