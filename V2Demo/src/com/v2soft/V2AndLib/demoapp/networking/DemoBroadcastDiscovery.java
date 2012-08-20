/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.v2soft.V2AndLib.demoapp.networking;

import java.net.DatagramPacket;
import java.net.InetAddress;

import com.v2soft.AndLib.networking.UDPBroadcastDiscovery;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DemoBroadcastDiscovery 
extends UDPBroadcastDiscovery {
    private static final String REQUEST_UUID = "UUID";

    public DemoBroadcastDiscovery(InetAddress source, InetAddress target) {
        super(5, 1000, 1235, source, target);
    }

    @Override
    protected void handleIncomePacket(DatagramPacket packet) {
        byte [] data = packet.getData();
        String received = new String(data, 0, packet.getLength());
        if ( mListener != null ) {
            mListener.onNewServer(new DemoUDPHost(packet.getAddress(), received));
        }
        
    }

    @Override
    protected DatagramPacket prepareRequest() {
        final byte[] buf = REQUEST_UUID.getBytes();
        return new DatagramPacket(buf, buf.length);
    }
}
