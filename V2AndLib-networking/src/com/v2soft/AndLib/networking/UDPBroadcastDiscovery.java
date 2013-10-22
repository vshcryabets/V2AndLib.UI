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
package com.v2soft.AndLib.networking;

import java.io.IOException;
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
        void onNewServer(Object item);
    }

    protected static final String LOG_TAG = UDPBroadcastDiscovery.class.getSimpleName();

    protected UDPBroadcastListener mListener;
    private int mRetryCount;
    private int mDelay;
    private Thread mSenderThread = null;
    private Thread mReceiverThread = null;
    private DatagramSocket mSocket;

    protected int mTargetPort;
    private InetAddress mTargetAddress;
    private InetAddress mSourceAddress;
    protected boolean isWorking;

    /**
     * 
     * @param retryCount how many times we should send UDP broadcast packet
     * @param delay delay between sending a packet
     */
    public UDPBroadcastDiscovery(int retryCount, int delay, 
            int targetPort, 
            InetAddress localAddress, 
            InetAddress targetAddress) {
        mRetryCount = retryCount;
        mDelay = delay;
        mTargetAddress = targetAddress;
        mTargetPort = targetPort;
        mSourceAddress = localAddress;
    }

    /**
     * Start searching of other hosts
     * @param targetPort
     * @param localAddress
     * @param targetAddress
     * @throws SocketException 
     */
    public void startDiscovery() throws SocketException {
        if ( mSenderThread != null ) {
            throw new IllegalStateException("Broadcast discovery process already started");
        }
        mSocket = new DatagramSocket();
        mSocket.setSoTimeout(mDelay);
        // start 
        mSenderThread = new Thread(mBackgroundSender,
                UDPBroadcastDiscovery.class.getSimpleName());
        mSenderThread.start();
        if ( mListener != null ) {
            mListener.onDiscoveryStarted();
        }
    }

    /**
     * Stop searching of other hosts
     */
    public void stopDiscovery() {
        isWorking = false;
    }

    public boolean isDiscovering() {
        return isWorking;
    }

    protected abstract DatagramPacket prepareRequest();
    protected abstract void handleIncomePacket(DatagramPacket income);

    public UDPBroadcastListener getListener() {
        return mListener;
    }

    public void setListener(UDPBroadcastListener mListener) {
        this.mListener = mListener;
    }

    private Runnable mBackgroundSender = new Runnable() {
        @Override
        public void run() {
            isWorking = true;
            // start receiver thread
            mReceiverThread = new Thread(mBackgroundReceiver,
                    UDPBroadcastDiscovery.class.getSimpleName()+"R");
            mReceiverThread.start();

            int count = mRetryCount;
            try {
                while ( count -- > 0 && isWorking ) {
                    // send packet
                    final DatagramPacket packet = prepareRequest();
                    packet.setAddress(mTargetAddress);
                    packet.setPort(mTargetPort);
                    try {
                        mSocket.send(packet);
                    } catch (Exception e) {
                        //                    Log.e(LOG_TAG, e.toString(), e);
                    }
                    // delay
                    Thread.sleep(mDelay);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isWorking = false;
            if ( mReceiverThread.isAlive() ) {
                try {
                    mReceiverThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mSenderThread = null;
            mReceiverThread = null;
            mSocket.close();
            if ( mListener != null ) {
                mListener.onDiscoveryFinished();
            }
        }
    };

    private Runnable mBackgroundReceiver = new Runnable() {
        @Override
        public void run() {
            final byte[] buf = new byte[256];

            while ( isWorking ) {
                try {
                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    mSocket.receive(packet);
                    handleIncomePacket(packet);
                    Thread.sleep(100);
                } catch (IOException e) {
                    // ignore it
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };
}
