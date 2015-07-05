package com.djs.lightStrandClient;

import com.djs.lightStrandClient.R;
import com.djs.lightStrandClient.bluetooth.LSClient_BLE;
import com.djs.lightStrandClient.wifi.LSClient;
import com.djs.lightStrandClient.LightCode;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Cotains logic and UI elements that allows the user to send commands to a remote
 * device controlling lights.
 */
public class ControlFragment extends Fragment implements IClient_Listener,
		IClient_CommandListener, IClient_ConnectionListener
{
	protected IClient lightStrandClient;
	
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
	protected ProgressDialog MyDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		lightStrandClient = new LSClient_BLE(getActivity());
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume()
	{
		UpdateButtons();

		if (lightStrandClient.isConnected())
		{
			return;
		}

		MyDialog = new ProgressDialog(getActivity());
		MyDialog.setIndeterminate(true);
		MyDialog.setCancelable(false);
		MyDialog.setMessage(getString(R.string.connection));
		MyDialog.show();

		lightStrandClient.connect(this);
		
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
		if (lightStrandClient.isConnected() == false)
		{
			String msg = "NOT sending " + code.toString() + ", bluetooth device is not connected";
			SLog.Info(msg);
			Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			return;
		}
		
		SLog.Debug("Sending " + code.toString() + "...");
		Vibrate_ButtonPressed();
		lightStrandClient.sendCode(code, this);
	}
	
	protected void Vibrate_ButtonPressed()
	{
		Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		v.vibrate(100);
	}

	protected void UpdateButtons()
	{
		if (MyImgOn == null)
		{
			return;
		}
		
		MyImgOn.setEnabled(lightStrandClient.isConnected());
		MyImgOff.setEnabled(lightStrandClient.isConnected());
		MyImgBrightUp.setEnabled(lightStrandClient.isConnected());
		MyImgBrightDown.setEnabled(lightStrandClient.isConnected());
		MyImgRed.setEnabled(lightStrandClient.isConnected());
		MyImgGreen.setEnabled(lightStrandClient.isConnected());
		MyImgBlue.setEnabled(lightStrandClient.isConnected());
		MyImgWhite.setEnabled(lightStrandClient.isConnected());
		MyImgRedOrange.setEnabled(lightStrandClient.isConnected());
		MyImgLightGreen.setEnabled(lightStrandClient.isConnected());
		MyImgDarkBlue.setEnabled(lightStrandClient.isConnected());
		MyImgFlash.setEnabled(lightStrandClient.isConnected());
		MyImgOrange.setEnabled(lightStrandClient.isConnected());
		MyImgGreenBlue.setEnabled(lightStrandClient.isConnected());
		MyImgDarkPurple.setEnabled(lightStrandClient.isConnected());
		MyImgStrobe.setEnabled(lightStrandClient.isConnected());
		MyImgOrangeYellow.setEnabled(lightStrandClient.isConnected());
		MyImgLightBlue.setEnabled(lightStrandClient.isConnected());
		MyImgMediumPurple.setEnabled(lightStrandClient.isConnected());
		MyImgFade.setEnabled(lightStrandClient.isConnected());
		MyImgYellow.setEnabled(lightStrandClient.isConnected());
		MyImgMediumBlue.setEnabled(lightStrandClient.isConnected());
		MyImgLightPurple.setEnabled(lightStrandClient.isConnected());
		MyImgSmooth.setEnabled(lightStrandClient.isConnected());
	}

	@Override
	public void OnConnectionLost()
	{
		UpdateButtons();
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

}
