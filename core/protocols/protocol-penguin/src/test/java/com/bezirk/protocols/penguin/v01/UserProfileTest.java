package com.bezirk.protocols.penguin.v01;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the UserProfile event by setting the properties and retrieving them.
 *
 * @author RHR8KOR
 */
public class UserProfileTest {


    @Test
    public void test() {


        UserProfile usrProfile = new UserProfile();

        List<Condition> condition = new ArrayList<Condition>();

        ContextValue contextValue = new ContextValue("PARTOFDAY", "MORNING");
        Condition cond = new Condition();
        cond.setContextValue(contextValue);
        assertEquals("PARTOFDAY", cond.getContextValue().getType());
        assertEquals("MORNING", cond.getContextValue().getValue());


        contextValue.setValue("MORNING");
        assertEquals("MORNING", contextValue.getValue());

        contextValue.setType("PARTOFDAY");
        assertEquals("PARTOFDAY", contextValue.getType());


        ContextValue contextValue1 = new ContextValue("PARTOFDAY", "EVENING");
        Condition cond1 = new Condition();
        cond1.setContextValue(contextValue1);
        assertEquals("PARTOFDAY", cond1.getContextValue().getType());
        assertEquals("EVENING", cond1.getContextValue().getValue());

        condition.add(cond);
        condition.add(cond1);


        ConditionalProfileSubset condProfileSet = new ConditionalProfileSubset();

        condProfileSet.addCondition(cond);
        condProfileSet.addCondition(cond1);
        assertEquals(2, condProfileSet.getConditions().size());
        assertTrue("ConditionalProfileSubset does'nt contain the added Conditions", condProfileSet.getConditions().contains(cond1));


        condProfileSet.setConditions(condition);
        assertEquals(2, condProfileSet.getConditions().size());
        assertTrue("ConditionProfileSubset not contains the List of added Condition", condProfileSet.getConditions().contains(cond));


        usrProfile.addConditionalProfileSubset(condProfileSet);
        assertTrue("ConditionalProfileSubset does'nt contains the Added Conditions", usrProfile.getConditionalProfileSubset().contains(condProfileSet));


        DefaultProfileSubset defaultSubset = new DefaultProfileSubset();

        UserModel userModel = new UserModel();

        userModel.setName("Default User");
        assertEquals("Default User", userModel.getName());

        List<ContextValue> contextValueList = new ArrayList<ContextValue>();
        contextValueList.add(contextValue);
        contextValueList.add(contextValue1);


        //TODO Check this by getting only one single collection
        userModel.setContext(contextValueList);
        assertTrue("User Model does'nt contains the added Context Value", userModel.getContext().containsAll(contextValueList));

        // Creating the Prefrences //

        Preference pref = new Preference();
        pref.setConfidence(10.00d);
        pref.setDateTime("12-06-2015");
        pref.setFormat("BIG INDIAN");
        pref.setLocation("OFFICE");
        pref.setPartOfDay("Saturday");
        pref.setSource("Home Party");
        pref.setType("Private");
        pref.setUser("BOB");
        pref.setValue("Friend");

        List<Preference> preference = new ArrayList<Preference>();
        preference.add(pref);

        userModel.setPreferences(preference);
        assertTrue("OFFICE", userModel.getPreferences().contains(pref));

        defaultSubset.setSpecificService("SERVIE A");
        defaultSubset.setUserModel(userModel);
        assertTrue("Default SubSet Does Not Contains the Added List of User Model", defaultSubset.getUserModel().equals(userModel));


        userModel.addContext(contextValue);
        userModel.addContext(contextValue1);
        assertEquals(4, userModel.getContext().size());


        userModel.addPreference(pref);
        assertTrue("BOB", userModel.getPreferences().contains(pref));


        usrProfile.addDefaultProfileSubset(defaultSubset);
        assertTrue("User Profile Does'nt Contains the added Default Subset", usrProfile.getDefaultProfileSubset().contains(defaultSubset));


        List<ConditionalProfileSubset> conditionalProfileSubset = new ArrayList<ConditionalProfileSubset>();
        conditionalProfileSubset.add(condProfileSet);
        usrProfile.setConditionalProfileSubset(conditionalProfileSubset);


        List<DefaultProfileSubset> defaultProfileSubSet = new ArrayList<DefaultProfileSubset>();
        defaultProfileSubSet.add(defaultSubset);
        usrProfile.setDefaultProfileSubset(defaultProfileSubSet);


        usrProfile.setId("USER 1");

        String serializedProfile = usrProfile.toJSON();
        UserProfile deserializedUserProfile = UserProfile.deserialize(serializedProfile);

        assertEquals("ProfileSubset not matching to set value.", 1, deserializedUserProfile.getDefaultProfileSubset().size());
        assertEquals("Conditionalsubset not matching to set value.", 1, deserializedUserProfile.getConditionalProfileSubset().size());
        assertEquals("USER 1", deserializedUserProfile.getId());


    }

}
