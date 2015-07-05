package com.djs.lightStrandClient;

import java.util.ArrayList;
import java.util.Map;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Static logging methods, stores the last 200 log messages
 */
public class SLog 
{
	
	public interface ILogListener
	{
		void OnNewLog(String msg);
	}
	
	private static LruCache<Integer, String> 	logCache 	= new LruCache<Integer, String>(200);	
	private static final String 				TAG 		= "lightstrandcontrol";
	private static boolean 						showdebug 	= true;
	private static ILogListener					MyListener 	= null;
	
	public static void Error(String msg)
	{
		Log.e(TAG,msg);
		
		StoreLog(msg);
	}
	
	public static void Info(String msg)
	{
		Log.i(TAG,msg);
		
		StoreLog(msg);
	}
	
	public static void Warn(String msg)
	{
		Log.w(TAG, msg);
		
		StoreLog(msg);
	}
	
	public static void Debug(String msg)
	{
		
		if (showdebug)
		{
			Log.d(TAG,msg);
			
			StoreLog(msg);
		}
	}
	
	public synchronized static void SetLogListener(ILogListener obj)
	{
		MyListener = obj;
	}
	
	public synchronized static void Clear()
	{
		logCache.evictAll();
	}
	
	public synchronized static ArrayList<String> GetLogs() 
	{
		ArrayList<String> logs = new ArrayList<String>();
		
		Map<Integer,String> map = logCache.snapshot();
		for(Integer key : map.keySet())
		{
			logs.add( map.get(key) );
		}
		
		return logs;
	}
	
	protected synchronized static void StoreLog(String msg)
	{
		logCache.put(logCache.putCount(), msg);
		
		if (MyListener != null)
		{
			MyListener.OnNewLog(msg);;
		}
	}

}
