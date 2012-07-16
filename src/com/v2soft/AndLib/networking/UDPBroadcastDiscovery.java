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

    protected UDPBroadcastListener mListener;
    private int mRetryCount;
    private int mDelay;
    private Thread mSenderThread = null;
    private Thread mReceiverThread = null;
    private DatagramSocket mSocket;

    protected int mTargetPort;
    private InetAddress mTargetAddress;
    private InetAddress mSourceAddress;

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
        // start receiver thread
        mReceiverThread = new Thread(mBackgroundReceiver,
                UDPBroadcastDiscovery.class.getSimpleName()+"R");
        mReceiverThread.start();

        // start 
        mSenderThread = new Thread(mBackgroundSender,
                UDPBroadcastDiscovery.class.getSimpleName());
        mSenderThread.start();
        if ( mListener != null ) {
            mListener.onDiscoveryStarted();
        }
    }

    /**
     * Start searching of other hosts
     */
    public void stopDiscovery() {
        if ( mSenderThread != null ) {
            mSenderThread.interrupt();
            mSenderThread = null;
        }
        if ( mReceiverThread != null ) {
            mReceiverThread.interrupt();
            mReceiverThread = null;
        }
    }

    public boolean isDiscovering() {
        return (mSenderThread != null);
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
            stopDiscovery();
            if ( mListener != null ) {
                mListener.onDiscoveryFinished();
            }
        }
    };

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
                    if ( mSocket.isClosed() ) {
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
            mReceiverThread = null;
        }
    };
}
