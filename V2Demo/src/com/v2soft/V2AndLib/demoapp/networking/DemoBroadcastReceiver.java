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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import com.v2soft.AndLib.networking.UDPBroadcastReceiver;

public class DemoBroadcastReceiver extends UDPBroadcastReceiver {
    private static final String REQUEST_UUID = "UUID";
    
    private UUID mUUID = UUID.randomUUID();

    public DemoBroadcastReceiver(InetAddress broadcast) throws SocketException {
        super(1235, broadcast);
    }

    @Override
    protected void handleIncomePacket(DatagramSocket socket, DatagramPacket packet) {
        byte [] data = packet.getData();
        String received = new String(data, 0, packet.getLength());
        if ( REQUEST_UUID.equals(received)) {
            byte[] buf = mUUID.toString().getBytes();        
            try {
                socket.send(new DatagramPacket(buf, buf.length, 
                        packet.getAddress(), packet.getPort()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }        
        }
    }
}
