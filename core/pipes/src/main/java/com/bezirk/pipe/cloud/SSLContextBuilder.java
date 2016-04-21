package com.bezirk.pipe.cloud;

import javax.net.ssl.SSLContext;

public interface SSLContextBuilder {

    /**
     * Create and configure a SSLContext
     *
     * @return
     */
    public SSLContext build() throws Exception;

}