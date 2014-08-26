package com.v2soft.V2AndLib.demoapp.networking;

import java.net.InetAddress;

import com.v2soft.AndLib.ui.views.DataViewAnnotation;
import com.v2soft.V2AndLib.demoapp.R;

public class DemoUDPHost {
	@DataViewAnnotation(resource=R.id.v2andlib_1line)
	private InetAddress mAddress;
	@DataViewAnnotation(resource=R.id.v2andlib_2line)
	private String mId;

	public DemoUDPHost(InetAddress address, String received) {
		mAddress = address;
		mId = received;
	}
	
	@Override
	public int hashCode() {
		return mAddress.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if ( !o.getClass().equals(DemoUDPHost.class)) {
			return false;
		}
		return mId.equals(((DemoUDPHost)o).mId);
	}
}
