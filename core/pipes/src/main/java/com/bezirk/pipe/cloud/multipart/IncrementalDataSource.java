package com.bezirk.pipe.cloud.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class IncrementalDataSource implements DataSource {

    private final String contentType;

    private final InputStream inputStream;

    private final String name;

    public IncrementalDataSource(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;

        this.name = inputStream.getClass().getSimpleName() + ":" + contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("getOutputStream() not supported for: " + IncrementalDataSource.class);
    }

}
