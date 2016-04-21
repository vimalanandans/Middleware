package com.bezirk.protocols.penguin.v01;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the ConditionalProfileSubset by setting the properties and retrieving them.
 *
 * @author RHR8KOR
 */
public class ConditionalProfileSubsetTest {

    @Test
    public void test() {

        ConditionalProfileSubset condProfSubset = new ConditionalProfileSubset();
        condProfSubset.setSpecificService("MOCK Service");

        ContextValue contextValue1 = new ContextValue("Service_1", "FM");
        ContextValue contextValue2 = new ContextValue("Service_2", "AM");
        ContextValue contextValueNew = new ContextValue("Service_3", "FM 101");


        List<ContextValue> context = new ArrayList<ContextValue>();

        context.add(contextValue1);
        context.add(contextValue2);


        UserModel userModel = new UserModel();
        userModel.setContext(context);
        condProfSubset.setUserModel(userModel);


        assertEquals("MOCK Service", condProfSubset.getSpecificService());
        assertEquals(2, condProfSubset.getUserModel().getContext().size());
        assertFalse("Context Object contains the contextValues", condProfSubset.getUserModel().getContext().contains(contextValueNew));
        assertTrue("Context Object Does'nt contains the contextValue1", condProfSubset.getUserModel().getContext().contains(contextValue1));


        Condition cond = new Condition();
        cond.setOperator("OPERATION FOR SERVICE");
        assertEquals("OPERATION FOR SERVICE", cond.getOperator());

        Condition cond1 = new Condition();
        cond1.setOperator("OPERATION FOR SERVICE 1");
        assertEquals("OPERATION FOR SERVICE 1", cond1.getOperator());

        Condition cond2 = new Condition();
        cond2.setOperator("OPERATION FOR SERVICE 2");
        assertEquals("OPERATION FOR SERVICE 2", cond2.getOperator());


        List<Condition> hasCondition = new ArrayList<Condition>();
        hasCondition.add(cond);
        hasCondition.add(cond1);

        condProfSubset.setConditions(hasCondition);
        assertEquals(2, condProfSubset.getConditions().size());


        assertFalse("Condition Object contains Correct Value", condProfSubset.getConditions().contains(cond2));
        assertTrue("Condition object Contains the correct list of Conditions", condProfSubset.getConditions().contains(cond));

    }

}
