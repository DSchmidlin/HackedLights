package com.djs.lightStrandClient.wifi;

import android.os.AsyncTask;

import com.djs.lightStrandClient.LightCode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LSClient
{
	protected InetAddress		MyAddress;
	protected int				MyPort;
	protected ILSClientListener MyListener;	
	protected boolean			MySendingCommandFlag;
	
	public static int SOCKET_CONNECT_TIMEOUT = 10 * 1000;
	public static int UDP_RCV_TIMEOUT 		 = 30 * 1000;
	
	public LSClient(ILSClientListener obj)
	{
		MyListener = obj;
	}

	public boolean IsConnected()
	{
		return MyAddress != null && MyPort != -1;
	}	
	
	public boolean IsSending()
	{
		return MySendingCommandFlag;
	}
	
	/**
	 * Listens for a UDP broadcast by the light strand device, the broadcast
	 * will have it's ip and as part of the datagram and the port will be part
	 * of the data
	 * 
	 * ILSClientListener.OnUDPBroadcastReceived() is called if a UDP heartbeat is
	 * heart.
	 * 
	 *  ILSClientListener.OnUDPBroadcastTimeout() is called if a UDP heartbeat 
	 *  is not heard.
	 */
	public void AsyncListenForUDPHeartBeat()
	{
		AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>()
		{
			String msg;
			InetAddress address;
			int port;
			boolean wasError, wasTimeOut;			
			DatagramSocket socket = null;
			
			@Override
			protected Void doInBackground(Void... params)
			{
				wasError 	= false;
				wasTimeOut 	= false;
				try
				{
					socket = new DatagramSocket(5000);
					socket.setSoTimeout(UDP_RCV_TIMEOUT);
					
					byte[] buf = new byte[256];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
													
					socket.receive(packet);
					
					address = packet.getAddress();
					String tmpPortValue = new String(buf).trim();
					port = Integer.valueOf(tmpPortValue);					
				} 
				catch (InterruptedIOException e)
				{
					wasTimeOut = true; 		
				}
				catch (Exception e)
				{
					wasError = true;
					msg = e.getMessage();					
				}
				finally
				{
					if (socket != null)
					{
						socket.close();						
					}
				}
				
				return null;				
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				if (wasTimeOut)
				{
					MyListener.OnUDPBroadcastTimeout();
				}				
				else if (wasError)
				{
					MyListener.OnUDPBroadcastFailure(msg);
				}
				else
				{				
					MyListener.OnUDPBroadcastReceived(address, port);
				}
			}
			
		};
		
		t.execute();
	}	
	
	public void asyncConnect(final int port, final String inetAddress)
	{
		if (this.IsConnected())
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
				Socket socket = null;
				try
				{
					InetAddress ip 	= Inet4Address.getByName(inetAddress);
					
					socket = new Socket();
					socket.connect(new InetSocketAddress(ip, port), SOCKET_CONNECT_TIMEOUT);
					
					MyAddress 	= ip;
					MyPort 		= port;
				} 
				catch (Exception e)
				{
					Reset();
					
					wasError = true;
					msg = e.getMessage();					
				}
				finally
				{
					try
					{
						socket.close();
					} 
					catch (IOException e)
					{
						// Do nothing bug log it.
						e.printStackTrace();
					}
				}
				
				return null;				
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				if (wasError)
				{				
					MyListener.OnConnectionFailed(msg);
				}
				else
				{
					MyListener.OnConnectionSuccess();
				}
			}
			
		};
		
		t.execute();
	}

	public void disconnect()
	{
		if (this.IsConnected() == false)
		{
			MyListener.OnLog("disconnect() called but no connection was made yet.");
			return;
		}
		
				
		Reset();
	}
	
	private void Reset()
	{
		MyAddress 	= null;
		MyPort 		= -1;
	}

	public void SendCode(final LightCode code)
	{
		if (this.IsConnected() == false)
		{
			MyListener.OnCommandFailed("You must call connect( before SendCode().");
			return;
		}
		
		
		if (MySendingCommandFlag )
		{
			return;
		}
		
		
		AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>()
		{
			String msg;
			boolean wasError;
			
			@Override
			protected Void doInBackground(Void... params)
			{
				MySendingCommandFlag = true;
				
				wasError = false;
				Socket socket = null;
				try
				{
					msg = code.toString();
					
					socket = new Socket();
					socket.connect(new InetSocketAddress(MyAddress, MyPort), SOCKET_CONNECT_TIMEOUT);
					
					DataOutputStream dataOut = new DataOutputStream( socket.getOutputStream());
					dataOut.writeByte(code.ordinal());
				} 
				catch (Exception e)
				{
					Reset();
					
					wasError = true;
					msg = e.getMessage();					
				}				
				finally
				{
					try
					{
						socket.close();
					} 
					catch (IOException e)
					{
						// just log it
						e.printStackTrace();
					}
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
				
				MySendingCommandFlag = false;
			}
			
		};
		
		t.execute();
	}
}
