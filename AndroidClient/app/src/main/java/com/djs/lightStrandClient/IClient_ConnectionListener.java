package com.djs.lightStrandClient;

/**
 * A set of methods that a class must implement if
 * it wants to be informed of connection status changes.  The connections
 * would be to a remote device controlling a set of lights.
 *
 * Created by David Schmidlin on 7/4/2015.
 */
public interface IClient_ConnectionListener
{
    /**
     * The connection to the device was lost
     */
    void OnConnectionLost();

    /**
     * The connection to the device was established or re-established.
     */
    void OnConnectionSuccess();

    /**
     * A connection attempt to the device failed.
     * @param msg   The reason why the attempt failed.
     */
    void OnConnectionFailed(String msg);
}
