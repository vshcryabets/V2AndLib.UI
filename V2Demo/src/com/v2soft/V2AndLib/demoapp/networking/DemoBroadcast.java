package com.v2soft.V2AndLib.demoapp.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class DemoBroadcast extends UDPBroadcast {
    private static final String REQUEST_UUID = "UUID";
    
    private UUID mUUID = UUID.randomUUID();
    private InetAddress mBroadcast;

    public DemoBroadcast(InetAddress broadcast) {
        super(5, 1000);
        mBroadcast = broadcast;
    }

    @Override
    protected void handleIncomePacket(DatagramSocket socket, DatagramPacket packet) {
        byte [] data = packet.getData();
        String received = new String(data, 0, packet.getLength());
        if ( REQUEST_UUID.equals(received)) {
            byte[] buf = mUUID.toString().getBytes();        
            try {
                socket.send(new DatagramPacket(buf, buf.length, 
                        mBroadcast, socket.getLocalPort()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }        

        } else {
            System.out.println("Quote of the Moment: " + received);
        }
    }

    @Override
    protected void sendRequest(DatagramSocket socket) {
        byte[] buf;
        buf = REQUEST_UUID.getBytes();        
        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, mBroadcast, socket.getLocalPort());
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }

}
