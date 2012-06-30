package com.v2soft.V2AndLib.demoapp.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class UDPBroadcastReceiver {
    private Thread mReceiverThread = null;
    private DatagramSocket mSocket;
    private InetAddress mGroup;

    public UDPBroadcastReceiver() {
    }


    public void startReceiver(int port, InetAddress group) throws IOException {
        mSocket = new DatagramSocket(port);
        mGroup = group;
        if ( mReceiverThread != null ) {
            throw new IllegalStateException("Broadcast receiving process already started");
        }
        mReceiverThread = new Thread(mBackgroundReceiver, "UDPBroadcastReceiver");
        mReceiverThread.start();
    }

    public void stopReceiver() throws IOException {
        mSocket.close();
    }

    protected abstract void handleIncomePacket(DatagramSocket socket, DatagramPacket income);

    private Runnable mBackgroundReceiver = new Runnable() {
        @Override
        public void run() {
            final byte[] buf = new byte[256];
            
            while ( true ) {
                try {
                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    mSocket.receive(packet);
                    handleIncomePacket(mSocket, packet);
                    Thread.sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    };
}
