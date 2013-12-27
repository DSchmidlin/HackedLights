package com.djs.android.lightstrandcontrol;

import java.util.ArrayList;

import com.djs.android.lightstrandcontrol.SLog.ILogListener;
import com.djs.anroid.lightstrandcontrol.R;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class LogFragment extends Fragment implements ILogListener
{
	protected EditText MyLogWindow;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootV = inflater.inflate( R.layout.fragment_log, null);
		
		MyLogWindow = (EditText)rootV.findViewById(R.id.log);
		
		StringBuilder tmp = new StringBuilder();
		ArrayList<String> logEntries = SLog.GetLogs();
		for(String entry : logEntries)
		{
			tmp.append( entry + "\n");
		}
		
		MyLogWindow.setText(tmp.toString());
		
		return rootV;
	}
	
	@Override
	public void onResume()
	{
		SLog.SetLogListener(this);
		
		super.onResume();
	}
	
	@Override
	public void onPause()
	{
		SLog.SetLogListener(null);
		
		super.onPause();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.log_fragment_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.item_clear:
			SLog.Clear();
			MyLogWindow.setText("");					
			break;
		case R.id.item_copy:
			ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("simple text", MyLogWindow.getText());
			clipboard.setPrimaryClip(clip);	
			
			Toast.makeText(getActivity(), "logs saved in clipboard", Toast.LENGTH_LONG).show();
			break;
		default:
			return false;
		}
		
		return true;
	}

	@Override
	public void OnNewLog(String msg)
	{
		MyLogWindow.append(msg + "\n");		
	}

}
