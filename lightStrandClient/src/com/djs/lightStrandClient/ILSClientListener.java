package com.djs.lightStrandClient;

import java.net.InetAddress;

/**
 * Defines the method necessary for a class to receive callbacks from the LCClient class.
 * @author David Schmidlin
 *
 */
public interface ILSClientListener
{
	void OnUDPBroadcastReceived(InetAddress address, int port);
	void OnUDPBroadcastFailure(String msg);
	void OnUDPBroadcastTimeout();
	
	void OnConnectionSuccess();	
	void OnConnectionFailed(String msg);
	
	void OnCommandSuccess(String msg);	
	void OnCommandFailed(String msg);
	
	void OnLog(String msg);	
}
