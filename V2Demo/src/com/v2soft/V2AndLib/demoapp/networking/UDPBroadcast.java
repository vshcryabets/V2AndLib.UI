package com.v2soft.V2AndLib.demoapp.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class UDPBroadcast {
    public interface UDPBroadcastListener {
        void onDiscoveryStarted();
        void onDiscoveryFinished();
    }

    private UDPBroadcastListener mListener;
    private int mRetryCount;
    private int mDelay;
    private Thread mSenderThread = null, mReceiverThread = null;
    private DatagramSocket mSocket;
    private InetAddress mGroup;

    /**
     * 
     * @param retryCount how many times we should send UDP broadcast packet
     * @param delay delay between sending a packet
     */
    public UDPBroadcast(int retryCount, int delay) {
        mRetryCount = retryCount;
        mDelay = delay;
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

    /**
     * Start searching of other hosts
     */
    public void startDiscovery() {
        if ( mSenderThread != null ) {
            throw new IllegalStateException("Broadcast discovery process already started");
        }
        mSenderThread = new Thread(mBackgroundSender, "UDPBroadcastSender");    
        mSenderThread.start();
        if ( mListener != null ) {
            mListener.onDiscoveryStarted();
        }
    }

    /**
     * Start searching of other hosts
     */
    public void stopDiscovery() {
        mSenderThread.interrupt();
        mSenderThread = null;
    }    

    protected abstract void sendRequest(DatagramSocket socket);
    protected abstract void handleIncomePacket(DatagramSocket socket, DatagramPacket income);

    public UDPBroadcastListener getListener() {
        return mListener;
    }

    public void setListener(UDPBroadcastListener mListener) {
        this.mListener = mListener;
    }


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


    private Runnable mBackgroundSender = new Runnable() {
        @Override
        public void run() {
            int count = mRetryCount;
            while ( count -- > 0 ) {
                // send packet
                sendRequest(mSocket);
                // delay
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                    break;
                }
            }
            mSenderThread = null;
            if ( mListener != null ) {
                mListener.onDiscoveryFinished();
            }
        }
    };
}
