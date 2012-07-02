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
public abstract class UDPBroadcastReceiver {
    private Thread mReceiverThread = null;
    private DatagramSocket mSocket;
    protected InetAddress mAddress;
    private int mPort;

    public UDPBroadcastReceiver(int port, InetAddress group) throws SocketException {
        mAddress = group;
        mPort = port;
    }

    public void startReceiver() throws SocketException {
        mSocket = new DatagramSocket(mPort);
        if ( mReceiverThread != null ) {
            throw new IllegalStateException("Broadcast receiving process already started");
        }
        mReceiverThread = new Thread(mBackgroundReceiver, UDPBroadcastReceiver.class.getSimpleName());
        mReceiverThread.start();
    }

    public void stopReceiver() {
        mReceiverThread.interrupt();
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
