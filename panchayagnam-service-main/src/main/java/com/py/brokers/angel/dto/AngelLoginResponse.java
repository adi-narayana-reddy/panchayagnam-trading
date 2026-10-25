package com.py.brokers.angel.dto;

public class AngelLoginResponse {

    private boolean status;
    private String message;
    private String errorcode;
    private Object data;

    /**
     *
     */
    public static class Data {

        private String jwtToken;
        private String refreshToken;
        private String feedToken;
        private String state;

        /**
         * @return the jwtToken
         */
        public String getJwtToken() {
            return jwtToken;
        }

        /**
         * @param jwtToken the jwtToken to set
         */
        public void setJwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
        }

        /**
         * @return the refreshToken
         */
        public String getRefreshToken() {
            return refreshToken;
        }

        /**
         * @param refreshToken the refreshToken to set
         */
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        /**
         * @return the feedToken
         */
        public String getFeedToken() {
            return feedToken;
        }

        /**
         * @param feedToken the feedToken to set
         */
        public void setFeedToken(String feedToken) {
            this.feedToken = feedToken;
        }

        /**
         * @return the state
         */
        public String getState() {
            return state;
        }

        /**
         * @param state the state to set
         */
        public void setState(String state) {
            this.state = state;
        }

    }

    /**
     * @return the status
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the errorcode
     */
    public String getErrorcode() {
        return errorcode;
    }

    /**
     * @param errorcode the errorcode to set
     */
    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
    
}

