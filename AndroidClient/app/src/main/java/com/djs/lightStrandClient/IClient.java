package com.djs.lightStrandClient;

/**
 * Defines the methods necessary for a class to implement if it is to be used
 * as a way to communicate to the Light Strand server
 *
 * Created by David Schmidlin on 7/4/2015.
 */
public interface IClient
{
    /**
     * Determines if the client is connected to the remote device
     * controlling the lights
     * @return  True if the Client is connected, false if it is not.
     */
    boolean isConnected();

    /**
     * Connects the client to the rmeote device controlling the lights
     * @param connectListner    The object that will receive connection related
     *                          events
     */
    void connect(IClient_ConnectionListener connectListner);

    /**
     * Shutdowns the connection between the client and the rmeote device controlling the lights
     */
    void disconnect();

    /**
     * Sends a code to the remote device controlling the lights to change the lights state
     * @param code  The code to send to the remote device.
     * @param cmdListener  The object that will receive command related events.
     */
    void sendCode(final LightCode code,IClient_CommandListener cmdListener);
}
