package com.bezirk.aip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the Answer event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class AnswerTest {


    @Test
    public void test() {

        List<String> answers = new ArrayList<>();
        answers.add("East");
        String id = "ID_9";
        String subTopic = "Direction";

        Answer<String> answer = new Answer<>();
        answer.setAnswers(answers);
        answer.setId(id);
        answer.setSubTopic(subTopic);

        String serializedAnswer = answer.toJson();

        Answer<?> deserializedAnswer = Answer.fromJson(serializedAnswer, Answer.class);

        assertEquals("Answers is not equal to the set value.", answers, deserializedAnswer.getAnswers());
        assertEquals("ID is not equal to the set value.", id, deserializedAnswer.getId());
        assertEquals("SubTopic is not equal to the set value.", subTopic, deserializedAnswer.getSubTopic());

    }

}
