package com.bezirk.aip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies IncompleteQuestion event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class IncompleteQuestionTest {


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

        IncompleteQuestion incompleteQuestion = new IncompleteQuestion();
        incompleteQuestion.setAbout(about);
        incompleteQuestion.setAnswerFormat(answerFormat);
        incompleteQuestion.setContext(context);
        incompleteQuestion.setId(id);
        incompleteQuestion.setMaxAnswers(maxAnswers);
        incompleteQuestion.setQuestion(question);
        incompleteQuestion.setResponse(response);
        incompleteQuestion.setSubTopic(subTopic);

        String serializedIncompleteQuestion = incompleteQuestion.toJson();
        IncompleteQuestion deserializedIncompleteQuestion = IncompleteQuestion.fromJson(serializedIncompleteQuestion);

        assertEquals("About is not equal to the set value.", about, deserializedIncompleteQuestion.getAbout());
        assertEquals("AnswerFormat is not equal to the set value.", answerFormat, deserializedIncompleteQuestion.getAnswerFormat());
        assertEquals("Id is not equal to the set value.", id, deserializedIncompleteQuestion.getId());
        assertEquals("Contextuser is not equal to the set value.", context.getUser(), deserializedIncompleteQuestion.getContext().getUser());
        assertEquals("MaxAnswers is not equal to the set value.", maxAnswers, deserializedIncompleteQuestion.getMaxAnswers());
        assertEquals("Question is not equal to the set value.", question, deserializedIncompleteQuestion.getQuestion());
        assertEquals("Response is not equal to the set value.", response, deserializedIncompleteQuestion.getResponse());
        assertEquals("SubTopic is not equal to the set value.", subTopic, deserializedIncompleteQuestion.getSubTopic());


    }

}
