package com.bezirk.aip;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the QuestionProtocolRole and AnswerProtocolRole by verifying the event topics and stream topics.
 *
 * @author AJC6KOR
 */
public class ProtocolRoleTest {

    @Test
    public void test() {

        QuestionProtocolRole questionProtocolRole = new QuestionProtocolRole();
        String defaultDescription = "Protocol role for question-type messages";

        List<String> eventTopicList = Arrays.asList(questionProtocolRole.getEventTopics());
        assertTrue("Question topic is missing in QuestionProtocol event topic list.", eventTopicList.contains(Question.topic));
        assertTrue("IncompleteQuestion topic is missing in QuestionProtocol event topic list.", eventTopicList.contains(IncompleteQuestion.topic));
        assertTrue("InspireMe topic is missing in QuestionProtocol event topic list.", eventTopicList.contains(InspireMe.topic));
        assertTrue("OpenQuestion topic is missing in QuestionProtocol event topic list.", eventTopicList.contains(OpenQuestion.topic));
        assertFalse("Answer topic is present in QuestionProtocol event topic list.", eventTopicList.contains(Answer.topic));
        assertNull("StreamTopic is not null for question protocol role.", questionProtocolRole.getStreamTopics());
        assertEquals("Description is not matching the default description.", defaultDescription, questionProtocolRole.getDescription());
        assertEquals("ProtocolRoleName is not set to QuestionProtocolRole.", QuestionProtocolRole.class.getSimpleName(), questionProtocolRole.getProtocolName());

        AnswerProtocolRole answerProtocolRole = new AnswerProtocolRole();
        defaultDescription = "Protocol role for answer-type messages";

        eventTopicList = Arrays.asList(answerProtocolRole.getEventTopics());
        assertTrue("Answer topic is missing in AnswerProtocolRole event topic list.", eventTopicList.contains(Answer.topic));
        assertTrue("Digest topic is missing in AnswerProtocolRole event topic list.", eventTopicList.contains(Digest.topic));
        assertTrue("DisambiguationQuestions topic is missing in AnswerProtocolRole event topic list.", eventTopicList.contains(DisambiguationQuestions.topic));
        assertTrue("RelatedQuestions topic is missing in AnswerProtocolRole event topic list.", eventTopicList.contains(RelatedQuestions.topic));
        assertTrue("SingleAnswer topic is present in AnswerProtocolRole event topic list.", eventTopicList.contains(SingleAnswer.topic));
        assertFalse("OpenQuestion topic is present in AnswerProtocolRole event topic list.", eventTopicList.contains(OpenQuestion.topic));
        assertNull("StreamTopic is not null for question protocol role.", answerProtocolRole.getStreamTopics());
        assertEquals("Description is not matching the default description.", defaultDescription, answerProtocolRole.getDescription());
        assertEquals("ProtocolRoleName is not set to AnswerProtocolole.", AnswerProtocolRole.class.getSimpleName(), answerProtocolRole.getProtocolName());
    }

}
