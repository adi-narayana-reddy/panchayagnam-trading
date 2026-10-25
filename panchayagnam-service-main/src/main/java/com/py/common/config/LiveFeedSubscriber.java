//package com.py.common.config;
//
//import com.py.demataccount.service.HoldingsWebSocketServer;
//import org.springframework.stereotype.Component;
//
//@Component
//public class LiveFeedSubscriber {
//
//    public void onMessage(String message, String channel) {
//
//        try {
//            Long accId = Long.parseLong(channel.replace("LIVE_FEED_", ""));
//
//            // send to frontend clients of THIS instance
//            HoldingsWebSocketServer.sendToAccount(accId, message);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
