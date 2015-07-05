package com.djs.lightStrandClient.wifi;

import com.djs.lightStrandClient.IClient_CommandListener;
import com.djs.lightStrandClient.IClient_ConnectionListener;
import com.djs.lightStrandClient.IClient_Listener;

import java.net.InetAddress;

/**
 * Defines the method necessary for a class to receive callbacks from the LCClient class.
 * @author David Schmidlin
 *
 */
public interface ILSClientListener extends IClient_Listener, IClient_ConnectionListener, IClient_CommandListener
{
	void OnUDPBroadcastReceived(InetAddress address, int port);
	void OnUDPBroadcastFailure(String msg);
	void OnUDPBroadcastTimeout();
}
