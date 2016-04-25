package com.bezirk.aip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the SingleAnswer event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class SingleAnswerTest {


    @Test
    public void test() {

        List<String> about = new ArrayList<>();
        about.add("DIRECTION");
        about.add("NAVIGATION");
        String answer = "WEST";
        double confidence = 90.0;
        Context context = new Context();
        context.setUser("BOB");
        String format = "SINGLE_ANSWER";
        String id = "TESTID";
        String source = "PREFERENCE";
        String subTopic = "TEST";

        SingleAnswer singleAnswer = new SingleAnswer();
        singleAnswer.setAbout(about);
        singleAnswer.setAnswer(answer);
        singleAnswer.setConfidence(confidence);
        singleAnswer.setContext(context);
        singleAnswer.setFormat(format);
        singleAnswer.setId(id);
        singleAnswer.setSource(source);
        singleAnswer.setSubTopic(subTopic);

        String serializedSingleAnswer = singleAnswer.toJSON();

        SingleAnswer deserializedSingleAnswer = SingleAnswer.deserialize(serializedSingleAnswer);

        assertEquals("About is not equal to the set value.", about, deserializedSingleAnswer.getAbout());
        assertEquals("Answer is not equal to the set value.", answer, deserializedSingleAnswer.getAnswer());
        assertEquals("ContextUser is not equal to the set value.", context.getUser(), deserializedSingleAnswer.getContext().getUser());
        assertEquals("Format is not equal to the set value.", format, deserializedSingleAnswer.getFormat());
        assertEquals("Id is not equal to the set value.", id, deserializedSingleAnswer.getId());
        assertEquals("Source is not equal to the set value.", source, deserializedSingleAnswer.getSource());
        assertEquals("Subtopic is not equal to the set value.", subTopic, deserializedSingleAnswer.getSubTopic());
        assertEquals("Confidence is not equal to the set value.", Double.toString(confidence), Double.toString(deserializedSingleAnswer.getConfidence()));

    }

}
