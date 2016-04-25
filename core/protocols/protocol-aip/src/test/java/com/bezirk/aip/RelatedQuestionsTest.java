package com.bezirk.aip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the RelatedQuestions event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class RelatedQuestionsTest {


    @Test
    public void test() {

        String id = "QUESTION_1";
        List<String> questionList = new ArrayList<>();
        questionList.add("Which is the location ?");
        questionList.add("Is it near to LAB ?");
        String subTopic = "Location";
        RelatedQuestions relatedQuestions = new RelatedQuestions();
        relatedQuestions.setId(id);
        relatedQuestions.setRelatedQuestions(questionList);
        relatedQuestions.setSubTopic(subTopic);

        String serializedRelatedQuestions = relatedQuestions.toJSON();
        RelatedQuestions deserializedRelatedQuestions = RelatedQuestions.deserialize(serializedRelatedQuestions);

        assertEquals("QuestionList is not same as the set value.", questionList, deserializedRelatedQuestions.getRelatedQuestions());
        assertEquals("Id is not equal to set the value.", id, deserializedRelatedQuestions.getId());
        assertEquals("Subtopic is not equal to set the value.", subTopic, deserializedRelatedQuestions.getSubTopic());


    }

}
