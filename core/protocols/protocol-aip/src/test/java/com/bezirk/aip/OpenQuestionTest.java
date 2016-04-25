package com.bezirk.aip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies OpenQuestion event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class OpenQuestionTest {


    @Test
    public void test() {

        List<String> about = new ArrayList<>();
        about.add("LOCATION");
        List<String> answerFormat = new ArrayList<String>();
        answerFormat.add("DIRECTION");
        List<String> response = new ArrayList<String>();
        response.add("LAB");
        Context context = new Context();
        context.setUser("BOB");
        int maxAnswers = 5;
        String id = "Question_2";
        String question = "LOCATION";
        String subTopic = "DIRECTION";
        String until = "ANSWERED";

        OpenQuestion openQuestion = new OpenQuestion();
        openQuestion.setAbout(about);
        openQuestion.setAnswerFormat(answerFormat);
        openQuestion.setContext(context);
        openQuestion.setId(id);
        openQuestion.setMaxAnswers(maxAnswers);
        openQuestion.setQuestion(question);
        openQuestion.setResponse(response);
        openQuestion.setSubTopic(subTopic);
        openQuestion.setUntil(until);


        String serializedOpenQuestion = openQuestion.toJSON();
        OpenQuestion deserializedOpenQuestion = OpenQuestion.deserialize(serializedOpenQuestion);

        assertEquals("About is not equal to the set value.", about, deserializedOpenQuestion.getAbout());
        assertEquals("AnswerFormat is not equal to the set value.", answerFormat, deserializedOpenQuestion.getAnswerFormat());
        assertEquals("Id is not equal to the set value.", id, deserializedOpenQuestion.getId());
        assertEquals("Contextuser is not equal to the set value.", context.getUser(), deserializedOpenQuestion.getContext().getUser());
        assertEquals("MaxAnswers is not equal to the set value.", maxAnswers, deserializedOpenQuestion.getMaxAnswers());
        assertEquals("Question is not equal to the set value.", question, deserializedOpenQuestion.getQuestion());
        assertEquals("Response is not equal to the set value.", response, deserializedOpenQuestion.getResponse());
        assertEquals("SubTopic is not equal to the set value.", subTopic, deserializedOpenQuestion.getSubTopic());
        assertEquals("Until is not equal to the set value.", until, deserializedOpenQuestion.getUntil());


    }

}
