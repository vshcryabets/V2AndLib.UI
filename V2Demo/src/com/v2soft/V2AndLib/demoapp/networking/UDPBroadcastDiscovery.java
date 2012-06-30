package com.v2soft.V2AndLib.demoapp.networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class UDPBroadcastDiscovery {
    public interface UDPBroadcastListener {
        void onDiscoveryStarted();
        void onDiscoveryFinished();
    }

    private UDPBroadcastListener mListener;
    private int mRetryCount;
    private int mDelay;
    private Thread mSenderThread = null;
    private DatagramSocket mSocket;
    private InetAddress mGroup;

    /**
     * 
     * @param retryCount how many times we should send UDP broadcast packet
     * @param delay delay between sending a packet
     */
    public UDPBroadcastDiscovery(int retryCount, int delay) {
        mRetryCount = retryCount;
        mDelay = delay;
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
