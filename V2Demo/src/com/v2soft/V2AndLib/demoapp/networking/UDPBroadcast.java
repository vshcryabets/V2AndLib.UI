package com.v2soft.V2AndLib.demoapp.networking;

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

    /**
     * 
     * @param retryCount how many times we should send UDP broadcast packet
     * @param delay delay between sending a packet
     */
    public UDPBroadcast(int retryCount, int delay) {
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
        mReceiverThread = new Thread(mBackgroundReceiver, "UDPBroadcastReceiver");
        
    }
    
    /**
     * Start searching of other hosts
     */
    public void stopDiscovery() {
        
    }    
    
    protected abstract void sendRequest();


    public UDPBroadcastListener getListener() {
        return mListener;
    }

    public void setListener(UDPBroadcastListener mListener) {
        this.mListener = mListener;
    }


    private Runnable mBackgroundReceiver = new Runnable() {
        @Override
        public void run() {
            while ( true ) {
                // send packet
                sendRequest();
                // delay
                try {
                    Thread.sleep(mDelay);
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
                sendRequest();
                // delay
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    };
}
