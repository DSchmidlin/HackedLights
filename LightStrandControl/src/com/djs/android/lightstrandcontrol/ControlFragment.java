package com.djs.android.lightstrandcontrol;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.djs.anroid.lightstrandcontrol.R;
import com.djs.lightStrandClient.ILSClientListener;
import com.djs.lightStrandClient.LSClient;
import com.djs.lightStrandClient.LightCode;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ControlFragment extends Fragment implements ILSClientListener
{
	protected LSClient lightStrandClient;
	
	protected ImageView MyImgOn;
	protected ImageView MyImgOff;
	protected ImageView MyImgBrightUp;
	protected ImageView MyImgBrightDown;
	protected ImageView MyImgRed;
	protected ImageView MyImgGreen;
	protected ImageView MyImgBlue;
	protected ImageView MyImgWhite;
	protected ImageView MyImgRedOrange;
	protected ImageView MyImgLightGreen;
	protected ImageView MyImgDarkBlue;
	protected ImageView MyImgFlash;
	protected ImageView MyImgOrange;
	protected ImageView MyImgGreenBlue;
	protected ImageView MyImgDarkPurple;
	protected ImageView MyImgStrobe;
	protected ImageView MyImgOrangeYellow;
	protected ImageView MyImgLightBlue;
	protected ImageView MyImgMediumPurple;
	protected ImageView MyImgFade;
	protected ImageView MyImgYellow;
	protected ImageView MyImgMediumBlue;
	protected ImageView MyImgLightPurple;
	protected ImageView MyImgSmooth;
	protected Button 	MyBtnConnection;
	protected EditText	MyIPEditText;
	protected EditText  MyPortEditText;
	protected ProgressDialog MyDialog;
	
	protected static String IPKEY 	= "ip";
	protected static String PORTKEY = "port";
		
	protected static int UDPRCV_TIMEOUT = 30 * 1000;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		lightStrandClient = new LSClient(this);				
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume()
	{		
		Connect();
		
		super.onResume();
	}
	
	@Override
	public void onPause()
	{
		lightStrandClient.disconnect();		
		
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootV = inflater.inflate( R.layout.fragment_control, null);
		
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		MyIPEditText = (EditText) rootV.findViewById(R.id.ipaddress);		
		String tmpIP = prefs.getString(IPKEY, "");
		if (tmpIP.length() != 0 )
		{
			MyIPEditText.setText(tmpIP);
		}
				
		MyPortEditText = (EditText) rootV.findViewById(R.id.port);
		String tmpPort = prefs.getString(PORTKEY, "");
		if (tmpPort.length() != 0)
		{
			MyPortEditText.setText(tmpPort);
		}
		
		MyBtnConnection = (Button)rootV.findViewById(R.id.buttonconnection);
		MyBtnConnection.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Vibrate_ButtonPressed();
				
				if (lightStrandClient.IsConnected())
				{
					lightStrandClient.disconnect();										
					UpdateButtons();
				}
				else
				{
					Connect();					
				}
				
			}
		});
		
		MyImgOn = (ImageView)rootV.findViewById(R.id.button_on);
		MyImgOn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.On);				
			}
		});
		
		MyImgOff = (ImageView)rootV.findViewById(R.id.button_off);
		MyImgOff.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Off);				
			}
		});
		
		MyImgBrightUp = (ImageView)rootV.findViewById(R.id.button_brightnessup);
		MyImgBrightUp.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Brighter);				
			}
		});
		
		MyImgBrightDown = (ImageView)rootV.findViewById(R.id.button_brightnessdown);
		MyImgBrightDown.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Dimmer);				
			}
		});
		
		MyImgRed = (ImageView)rootV.findViewById(R.id.button_red);
		MyImgRed.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Red);				
			}
		});
		
		MyImgGreen = (ImageView)rootV.findViewById(R.id.button_green);
		MyImgGreen.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Green);				
			}
		});
		
		MyImgBlue = (ImageView)rootV.findViewById(R.id.button_blue);
		MyImgBlue.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Blue);				
			}
		});
		
		MyImgWhite = (ImageView)rootV.findViewById(R.id.button_white);
		MyImgWhite.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.White);				
			}
		});
		
		MyImgRedOrange = (ImageView)rootV.findViewById(R.id.button_red_orange);
		MyImgRedOrange.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.RedOrange);				
			}
		});
		
		MyImgLightGreen = (ImageView)rootV.findViewById(R.id.button_light_green);
		MyImgLightGreen.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.LightGreen);				
			}
		});
		
		MyImgDarkBlue = (ImageView)rootV.findViewById(R.id.button_dark_blue);
		MyImgDarkBlue.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.DarkBlue);				
			}
		});
		
		MyImgFlash = (ImageView)rootV.findViewById(R.id.button_flash);
		MyImgFlash.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Flash);				
			}
		});
		
		MyImgOrange = (ImageView)rootV.findViewById(R.id.button_orange);
		MyImgOrange.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Orange);				
			}
		});
		
		MyImgGreenBlue = (ImageView)rootV.findViewById(R.id.button_green_blue);
		MyImgGreenBlue.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.GreenBlue);				
			}
		});
		
		MyImgDarkPurple = (ImageView)rootV.findViewById(R.id.button_dark_purple);
		MyImgDarkPurple.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.DarkPurple);				
			}
		});
		
		MyImgStrobe = (ImageView)rootV.findViewById(R.id.button_strobe);
		MyImgStrobe.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Strobe);				
			}
		});
		
		MyImgOrangeYellow = (ImageView)rootV.findViewById(R.id.button_orange_yellow);
		MyImgOrangeYellow.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.OrangeYellow);				
			}
		});
		
		MyImgLightBlue = (ImageView)rootV.findViewById(R.id.button_light_blue);
		MyImgLightBlue.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.LightBlue);				
			}
		});
		
		MyImgMediumPurple = (ImageView)rootV.findViewById(R.id.button_medium_purple);
		MyImgMediumPurple.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.MediumPurple);					
			}
		});
		
		MyImgFade = (ImageView)rootV.findViewById(R.id.button_fade);
		MyImgFade.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Fade);					
			}
		});
		
		MyImgYellow = (ImageView)rootV.findViewById(R.id.button_yellow);
		MyImgYellow.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Yellow);					
			}
		});
		
		MyImgMediumBlue = (ImageView)rootV.findViewById(R.id.button_medium_blue);
		MyImgMediumBlue.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.MediumBlue);				
			}
		});
		
		MyImgLightPurple = (ImageView)rootV.findViewById(R.id.button_light_purple);
		MyImgLightPurple.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.LightPurple);				
			}
		});
		
		MyImgSmooth = (ImageView)rootV.findViewById(R.id.button_smooth);
		MyImgSmooth.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SendCode(LightCode.Smooth);				
			}
		});
		
		
		UpdateButtons();
		
		return rootV;
	}
	
	protected void SendCode(LightCode code)
	{
		if (lightStrandClient.IsSending())
		{
			SLog.Info("NOT sending " + code.toString() + ", there is still a code being sent.");
			return;
		}
		
		SLog.Debug("Sending " + code.toString() + "...");
		Vibrate_ButtonPressed();
		lightStrandClient.SendCode(code);
	}
	
	protected void Vibrate_ButtonPressed()
	{
		Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		v.vibrate(100);
	}
		
	
	protected void Connect()
	{
		
		
		String ipStr 	= MyIPEditText.getText().toString();
		String portStr  = MyPortEditText.getText().toString();
		
		if (lightStrandClient.IsConnected())
		{			
			return;		
		}
				
		MyDialog = new ProgressDialog(getActivity());
		MyDialog.setIndeterminate(true);		
		MyDialog.setCancelable(false);
		MyDialog.setMessage(getString(R.string.connection));
		MyDialog.show();
		
		//if we don't have an IP for the device yet, try to 
		//listen for the UDP broadcast.
		if (ipStr.length() <= 0 || portStr.length() <= 0)
		{
			lightStrandClient.AsyncListenForUDPHeartBeat();
			return;
		}
		
		try
		{
			int 		port 	= Integer.valueOf(portStr);
			
			lightStrandClient.asyncConnect(port, ipStr);
		} 
		catch (NumberFormatException e)
		{
			Toast.makeText(getActivity(), "Unable to connect to arduino, bad port #", Toast.LENGTH_LONG).show();
		}	
	}
	
	protected void UpdateButtons()
	{
		if (MyImgOn == null)
		{
			return;
		}
		
		MyImgOn.setEnabled(lightStrandClient.IsConnected());
		MyImgOff.setEnabled(lightStrandClient.IsConnected());
		MyImgBrightUp.setEnabled(lightStrandClient.IsConnected());
		MyImgBrightDown.setEnabled(lightStrandClient.IsConnected());
		MyImgRed.setEnabled(lightStrandClient.IsConnected());
		MyImgGreen.setEnabled(lightStrandClient.IsConnected());
		MyImgBlue.setEnabled(lightStrandClient.IsConnected());
		MyImgWhite.setEnabled(lightStrandClient.IsConnected());
		MyImgRedOrange.setEnabled(lightStrandClient.IsConnected());
		MyImgLightGreen.setEnabled(lightStrandClient.IsConnected());
		MyImgDarkBlue.setEnabled(lightStrandClient.IsConnected());
		MyImgFlash.setEnabled(lightStrandClient.IsConnected());
		MyImgOrange.setEnabled(lightStrandClient.IsConnected());
		MyImgGreenBlue.setEnabled(lightStrandClient.IsConnected());
		MyImgDarkPurple.setEnabled(lightStrandClient.IsConnected());
		MyImgStrobe.setEnabled(lightStrandClient.IsConnected());
		MyImgOrangeYellow.setEnabled(lightStrandClient.IsConnected());
		MyImgLightBlue.setEnabled(lightStrandClient.IsConnected());
		MyImgMediumPurple.setEnabled(lightStrandClient.IsConnected());
		MyImgFade.setEnabled(lightStrandClient.IsConnected());
		MyImgYellow.setEnabled(lightStrandClient.IsConnected());
		MyImgMediumBlue.setEnabled(lightStrandClient.IsConnected());
		MyImgLightPurple.setEnabled(lightStrandClient.IsConnected());
		MyImgSmooth.setEnabled(lightStrandClient.IsConnected());
		
		
		if (lightStrandClient.IsConnected())
		{
			MyBtnConnection.setBackgroundResource(R.drawable.connect);
		}
		else
		{
			MyBtnConnection.setBackgroundResource(R.drawable.disconnect);
		}
	}
	
	@Override
	public void OnConnectionSuccess()
	{
		if (MyDialog != null && MyDialog.isShowing())
		{
			MyDialog.cancel();
			MyDialog = null;
		}
		
		SLog.Debug("Connected to the light strand");
		UpdateButtons();
		
		//save the IP Used to connect
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Editor ed = prefs.edit();
		ed.putString(IPKEY, MyIPEditText.getText().toString());
		ed.putString(PORTKEY, MyPortEditText.getText().toString());
		ed.commit();
	}

	@Override
	public void OnConnectionFailed(String msg)
	{
		if (MyDialog != null && MyDialog.isShowing())
		{
			MyDialog.cancel();
			MyDialog = null;
		}
					
		SLog.Error("Failed to connect to the light strand : " + msg);	
		
		if (isVisible())
		{		
			Toast.makeText(getActivity(), "Failed to connect!", Toast.LENGTH_LONG).show();
			
			UpdateButtons();
		}
		
		//try listening for UDP Msgs broadcast by the light strand device
		lightStrandClient.AsyncListenForUDPHeartBeat();
	}

	@Override
	public void OnCommandSuccess(String msg)
	{
		SLog.Debug("Command sent successfully to the light strand : " + msg );		
	}

	@Override
	public void OnCommandFailed(String msg)
	{
		SLog.Error("Failed to send command to the light strand : " + msg);		
	}

	@Override
	public void OnLog(String msg)
	{
		SLog.Debug(msg);	
	}

	@Override
	public void OnUDPBroadcastReceived(InetAddress address, int port)
	{
		if (MyDialog != null && MyDialog.isShowing())
		{
			MyDialog.cancel();
			MyDialog = null;
		}
		
		if (address == null )
		{
			return;
		}		
		
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
		Editor ed = prefs.edit();
		ed.putString(IPKEY, address.getHostAddress());
		ed.putString(PORTKEY, String.valueOf(port) );
		ed.commit();
		
		MyIPEditText.setText(address.getHostAddress());
		MyPortEditText.setText(String.valueOf(port) );
		
		Connect();
	}

	@Override
	public void OnUDPBroadcastFailure(String msg)
	{
		if (MyDialog != null && MyDialog.isShowing())
		{
			MyDialog.cancel();
			MyDialog = null;
		}
		
		String errorMSG = "Failure while listening for udp heart beat :" + msg;
		
		SLog.Error(errorMSG);
		
		Toast.makeText(getActivity(), errorMSG, Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void OnUDPBroadcastTimeout()
	{
		if (MyDialog != null && MyDialog.isShowing())
		{
			MyDialog.cancel();
			MyDialog = null;
		}
		
		String errorMSG = "No UDP heart beat heard within " + LSClient.UDP_RCV_TIMEOUT + "ms";
		
		SLog.Error(errorMSG);
		
		Toast.makeText(getActivity(), errorMSG, Toast.LENGTH_LONG).show();		
	}
}
