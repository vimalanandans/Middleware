package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.examples.protocols.parametricUI.NoticeUIshowText.TextType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the NoticeUIshowPic,NoticeUIshowText and NoticeUIshowVideo events by retrieving the properties after deserialization.
 *
 * @author AJC6KOR
 */
public class NoticeUIshowTest {


    @Test
    public void test() {

        String picURL = "pic1.jpg";

        NoticeUIshowPic noticeUIshowPic = new NoticeUIshowPic(picURL);
        String serializedNoticeUIshowPic = noticeUIshowPic.toJSON();
        NoticeUIshowPic deserializedNoticeUIshowPic = NoticeUIshowPic.deserialize(serializedNoticeUIshowPic);

        assertEquals("PicURL is not equal to the set value.", picURL, deserializedNoticeUIshowPic.getPicURL());


        String text = "TEST";
        TextType type = TextType.ERROR;
        long expiration = 89;

        NoticeUIshowText noticeUIshowText = new NoticeUIshowText(text, type, expiration);
        String serializedNoticeUIshowText = noticeUIshowText.toJSON();
        NoticeUIshowText deserializedNoticeUIshowText = NoticeUIshowText.deserialize(serializedNoticeUIshowText);

        assertEquals("Text is not equal to the set value.", text, deserializedNoticeUIshowText.getText());
        assertEquals("Type is not equal to the set value.", type, deserializedNoticeUIshowText.getType());
        assertEquals("Expiration is not equal to the set value.", expiration, deserializedNoticeUIshowText.getExpiration());

        String videoURL = "video1.mov";

        NoticeUIshowVideo noticeUIshowVideo = new NoticeUIshowVideo(videoURL);
        String serializedNoticeUIshowVideo = noticeUIshowVideo.toJSON();
        NoticeUIshowVideo deserializedNoticeUIshowVideo = NoticeUIshowVideo.deserialize(serializedNoticeUIshowVideo);

        assertEquals("VideoURL is not equal to the set value.", videoURL, deserializedNoticeUIshowVideo.getVideoURL());


    }

}
