package com.djs.lightStrandClient;

/**
 * A set of methods that a class must implementg if it wants to be informed of the success or
 * failure of commands that are sent to the remote device controlling lights.
 *
 * Created by David Schmidlin on 7/4/2015.
 */
public interface IClient_CommandListener
{
    /**
     * The command was sent without error
     * @param msg   A success message if any at all
     */
    void OnCommandSuccess(String msg);

    /**
     * The command failed to send
     * @param msg   A reason for the failure.
     */
    void OnCommandFailed(String msg);
}
