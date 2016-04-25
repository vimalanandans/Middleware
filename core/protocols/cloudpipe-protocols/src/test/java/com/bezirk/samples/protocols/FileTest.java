package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.GetStreamRequest;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 * This testcase verifies the FileRequest, FileRequestProtocol , FileReply, FileReplyProtocol
 * by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class FileTest {

    @Test
    public void test() {

        testFileRequest();

        testFileRequestProtocol();

        testFileReply();

        testFileReplyProtocol();
    }


    private void testFileRequest() {


        String fileName = "File.txt";
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileName(fileName);

        String serializedFileRequest = fileRequest.toJson();

        FileRequest deserializedFileRequest = FileRequest
                .deserialize(serializedFileRequest);

        assertEquals("Filename is not equal to the set value.", fileName,
                deserializedFileRequest.getFileName());
    }

    private void testFileRequestProtocol() {

        FileRequestProtocol FileRequestProtocol = new FileRequestProtocol();

        assertNotNull("Description is null for FileRequestProtocol",
                FileRequestProtocol.getDescription());
        assertNull("StreamTopics is not null for FileRequestProtocol",
                FileRequestProtocol.getStreamTopics());
        assertEquals("ProtocolName is different for FileRequestProtocol.",
                FileRequestProtocol.class.getSimpleName(),
                FileRequestProtocol.getProtocolName());

        List<String> eventTopicList = Arrays.asList(FileRequestProtocol
                .getEventTopics());
        assertTrue("FileRequestProtocol is missing FileRequest topic in event topic list.",
                eventTopicList.contains(GetStreamRequest.class.getSimpleName()));
    }

    private void testFileReply() {

        String fileName = "SampleFile.txt";

        FileReply fileReply = new FileReply();
        fileReply.setFileName(fileName);

        String serializedFileReply = fileReply.toJson();

        FileReply deserializedFileReply = FileReply.fromJson(serializedFileReply, FileReply.class);

        assertEquals("Filename is not equal to the set value.", fileName,
                deserializedFileReply.getFileName());

    }

    private void testFileReplyProtocol() {

        FileReplyProtocol FileReplyProtocol = new FileReplyProtocol();

        assertNotNull("Description is null for FileReplyProtocol",
                FileReplyProtocol.getDescription());
        assertNull("EventTopics is not null for FileRequestProtocol",
                FileReplyProtocol.getEventTopics());
        assertEquals("ProtocolName is different for FileReplyProtocol.",
                FileReplyProtocol.class.getSimpleName(),
                FileReplyProtocol.getProtocolName());

        List<String> streamTopicList = Arrays.asList(FileReplyProtocol
                .getStreamTopics());
        assertTrue("FileReplyProtocol is missing Filereply topic in stream topic list.",
                streamTopicList.contains(new FileReply().topic));

    }

}
