package com.v2soft.AndLib.Timers;

public class SpecialTimer implements Runnable 
{
	private Thread thread;
	private int quant;
	private boolean isProcessing = false;
	private boolean isPaused = false;
	private int timer_max = 0;
	private int timer_val = 0;
	private SpecialTimerListener listner;
	private String name;
	
	public SpecialTimer(int quant, SpecialTimerListener listener, String name)
	{
		this.quant = quant;
		this.listner = listener;
		this.name = name;
	}
	
	public void setTimerMax(int val){timer_max=val;}
	public void setTimer(int val){timer_val=val;}
	public void prolongTimer(){timer_val=timer_max;}
	
	public void start()
	{
		if ( thread == null )
		{
			thread = new Thread(this, name);
			isProcessing = false;
			isPaused = false;
			thread.start();
			return;
		}
		if (( isProcessing ) && (isPaused ))
		{
			isPaused = false;
			return;
		}
	}
	
	public void stop()
	{
//		Log.d("SpecialTimer", "STOP!!!");
		isProcessing = false;
		if ( thread != null )
			thread.interrupt();
	}
	
	public void run() 
	{
		isProcessing = true;
		timer_val = timer_max;
		while ( isProcessing )
		{
			try
			{
				if ( !isPaused)
				{
					timer_val--;
					if ( (timer_val == 0 ) && 
						( listner != null ))
					{
						listner.onTimer();
					}					
					if ( timer_val < 0 )
					{
						isProcessing = false;
						break;
					}
				}
				Thread.sleep(quant);
			}
			catch (Exception e) 
			{
			}
		}
		isProcessing = false;
		thread = null;
	}

	public void pauseTimer(boolean val) 
	{
		isPaused = val;
	}

}
