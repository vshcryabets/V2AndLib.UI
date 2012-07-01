package com.v2soft.AndLib.networking;
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
    private InetAddress mTargetAddress;
    protected int mTargetPort;

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
     * @param targetPort
     * @param localAddress
     * @param targetAddress
     * @throws SocketException 
     */
    public void startDiscovery(int targetPort, InetAddress localAddress, InetAddress targetAddress) throws SocketException {
        if ( mSenderThread != null ) {
            throw new IllegalStateException("Broadcast discovery process already started");
        }
        mTargetAddress = targetAddress;
        mTargetPort = targetPort;
        mSocket = new DatagramSocket();
        mSenderThread = new Thread(mBackgroundSender, UDPBroadcastDiscovery.class.getSimpleName());    
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

    protected abstract void sendRequest(InetAddress target, DatagramSocket socket);
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
                sendRequest(mTargetAddress, mSocket);
                // delay
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                    break;
                }
            }
            mSocket.close();
            mSenderThread = null;
            if ( mListener != null ) {
                mListener.onDiscoveryFinished();
            }
        }
    };
}
