package com.aniapps.flicbuzzapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IAPBroadcastReciever extends BroadcastReceiver {
        /**
         * The Intent action that this Receiver should filter for.
         */
        public static final String ACTION = "com.android.vending.billing.PURCHASES_UPDATED";
        private final IabBroadcastListener mListener;

        public IAPBroadcastReciever(IabBroadcastListener listener) {
            mListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListener != null) {
                mListener.receivedBroadcast();
            }
        }

        /**
         * Listener interface for received broadcast messages.
         */
        public interface IabBroadcastListener {
            void receivedBroadcast();
        }
    }
