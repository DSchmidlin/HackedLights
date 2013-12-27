package com.djs.lightStrandClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import android.os.AsyncTask;

public class LSClient
{
	protected boolean isConencted;
	protected Socket MySocket;
	protected ILSClientListener MyListener;

	public LSClient(ILSClientListener obj)
	{
		MyListener = obj;
	}

	public boolean IsConnected()
	{
		return isConencted;
	}	
	
	public void connect(final int port, final InetAddress inetAddress)
	{
		if (isConencted)
		{
			MyListener.OnLog("connect() called but a connection was already established.");
			return;
		}
		
		AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>()
		{
			String msg;
			boolean wasError;
			
			@Override
			protected Void doInBackground(Void... params)
			{
				wasError = false;
				try
				{
					MySocket = new Socket(inetAddress, port);
					isConencted = true;
				} 
				catch (IOException e)
				{
					wasError = true;
					msg = e.getMessage();					
				}
				
				return null;				
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				if (wasError)
				{
					isConencted = false;
					MyListener.OnConnectionFailed(msg);
				}
				else
				{
					isConencted = true;
					MyListener.OnConnectionSuccess();
				}
			}
			
		};
		
		t.execute();
	}

	public void disconnect()
	{
		if (isConencted == false)
		{
			MyListener.OnLog("disconnect() called but no connection was made yet.");
			return;
		}

		AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>()
		{
			String msg;
			boolean wasError;
			
			@Override
			protected Void doInBackground(Void... params)
			{
				wasError = false;
				try
				{
					MySocket.close();
				} 
				catch (IOException e)
				{
					wasError = true;
					msg = e.getMessage();					
				}
				isConencted = false;
				return null;				
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				if (wasError)
				{
					MyListener.OnDisconnectFailed(msg);
				}
				else
				{
					MyListener.OnDisconnectSuccess();
				}
			}
			
		};
		
		t.execute();
	}

	public void SendCode(final LightCode code)
	{
		if (isConencted == false)
		{
			MyListener.OnCommandFailed("You must call connect( before SendCode().");
			return;
		}
		
		AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>()
		{
			String msg;
			boolean wasError;
			
			@Override
			protected Void doInBackground(Void... params)
			{
				wasError = false;
				try
				{
					msg = code.toString();
					DataOutputStream dataOut = new DataOutputStream( MySocket.getOutputStream());
					dataOut.writeByte(code.ordinal());
				} 
				catch (IOException e)
				{
					wasError = true;
					msg = e.getMessage();					
				}				
				return null;				
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				if (wasError)
				{
					MyListener.OnCommandFailed(msg);
				}
				else
				{
					MyListener.OnCommandSuccess(msg);
				}
			}
			
		};
		
		t.execute();
	}
}
