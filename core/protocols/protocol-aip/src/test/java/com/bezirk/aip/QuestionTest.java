package com.bezirk.aip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the Question event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class QuestionTest {


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
        String questionTopic = "LOCATION";
        String subTopic = "DIRECTION";

        Question question = new Question();
        question.setAbout(about);
        question.setAnswerFormat(answerFormat);
        question.setContext(context);
        question.setId(id);
        question.setMaxAnswers(maxAnswers);
        question.setQuestion(questionTopic);
        question.setResponse(response);
        question.setSubTopic(subTopic);

        String serializedQuestion = question.serialize();
        Question deserializedQuestion = Question.deserialize(serializedQuestion);

        assertEquals("About is not equal to the set value.", about, deserializedQuestion.getAbout());
        assertEquals("AnswerFormat is not equal to the set value.", answerFormat, deserializedQuestion.getAnswerFormat());
        assertEquals("Id is not equal to the set value.", id, deserializedQuestion.getId());
        assertEquals("Contextuser is not equal to the set value.", context.getUser(), deserializedQuestion.getContext().getUser());
        assertEquals("MaxAnswers is not equal to the set value.", maxAnswers, deserializedQuestion.getMaxAnswers());
        assertEquals("QuestionTopic is not equal to the set value.", questionTopic, deserializedQuestion.getQuestion());
        assertEquals("Response is not equal to the set value.", response, deserializedQuestion.getResponse());
        assertEquals("SubTopic is not equal to the set value.", subTopic, deserializedQuestion.getSubTopic());

    }

}
