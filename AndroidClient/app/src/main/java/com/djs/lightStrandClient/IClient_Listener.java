package com.djs.lightStrandClient;

/**
 *  Deprecated interface, used by the Wifi code that is being deprecated
 *
 * Created by David Schmidlin on 7/4/2015.
 */
public interface IClient_Listener
{

    /**
     * places some message into the adb log
     * @param msg The message the user wants to store
     */
    void OnLog(String msg);
}
