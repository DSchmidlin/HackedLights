package com.djs.lightStrandClient;

/**
 * Defines the method necessary for a class to receive callbacks from the LCClient class.
 * @author David Schmidlin
 *
 */
public interface ILSClientListener
{
	void OnConnectionSuccess();
	
	void OnConnectionFailed(String msg);
	
	void OnDisconnectSuccess();
	
	void OnDisconnectFailed(String msg);
	
	void OnCommandSuccess(String msg);
	
	void OnCommandFailed(String msg);
	
	void OnLog(String msg);	
}
