package com.bezirk.pipe.cloud.multipart;

public class Part {

    // HTTP header keys that we are interested in for each Part
    public static final String KEY_CONTENT_TYPE = "Content-Type";
    public static final String KEY_CONTENT_ENCODING = "Content-Transfer-Encoding";
    public static final String KEY_CONTENT_ID = "Content-ID";

    protected String boundary;
    protected String contentType;
    protected String contentTransferEncoding;
    protected String contentId;
    protected Object data;
    protected long sizeInBytes = 0;

    protected Part() {
        /* Protected constructor ensures that this base class is not instantiated directly
		 * this is preferred over making the class abstract per the following PMD rule:
		 * http://pmd.sourceforge.net/pmd-5.1.2/rules/java/design.html#AbstractClassWithoutAbstractMethod
		 */
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }
}
