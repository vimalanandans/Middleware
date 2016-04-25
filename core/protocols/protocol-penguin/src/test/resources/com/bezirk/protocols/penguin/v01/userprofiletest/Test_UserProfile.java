package com.bezirk.protocols.penguin.v01.userprofiletest;

import com.bezirk.protocols.penguin.v01.Condition;
import com.bezirk.protocols.penguin.v01.ConditionalProfileSubset;
import com.bezirk.protocols.penguin.v01.ContextValue;
import com.bezirk.protocols.penguin.v01.DefaultProfileSubset;
import com.bezirk.protocols.penguin.v01.Preference;
import com.bezirk.protocols.penguin.v01.UserModel;
import com.bezirk.protocols.penguin.v01.UserProfile;

public class Test_UserProfile {

    public static void main(String[] args) {
        // create user-profile uhu msg
        //UserProfile bobUserProfile = new UserProfile();

        // create user
        String user = "Bob";

        // create profile
        UserProfile profile = new UserProfile();
        profile.setId(user);

        //***********************************************
        // default temperature sub-profile

        // create default temperature user-model
        UserModel bobDefaultTemperatureUserModel = new UserModel();
        bobDefaultTemperatureUserModel.setName(user);

        // add user context to user-model
        bobDefaultTemperatureUserModel.addContext(new ContextValue("location", "Office/Lab/null"));
        bobDefaultTemperatureUserModel.addContext(new ContextValue("partOfDay", "morning"));

        // create default sub-profile
        DefaultProfileSubset defaultTemperatureProfileSubset = new DefaultProfileSubset();
        defaultTemperatureProfileSubset.setSpecificService("Thermostat");

        // create default thermostat preference
        Preference defaultThermostatPref = new Preference();
        defaultThermostatPref.setType("setTemperature");
        defaultThermostatPref.setValue("65.0");
        defaultThermostatPref.setFormat("Fahrenheit");
        defaultThermostatPref.setConfidence(0.99);

        // add default thermostat preference to user-model
        bobDefaultTemperatureUserModel.addPreference(defaultThermostatPref);

        // add default user-model to default sub-profile
        defaultTemperatureProfileSubset.setUserModel(bobDefaultTemperatureUserModel);

        // add default sub-profile to profile
        profile.addDefaultProfileSubset(defaultTemperatureProfileSubset);

        //***********************************************
        // default light sub-profile

        // create default temperature user-model
        UserModel bobDefaultLightUserModel = new UserModel();
        bobDefaultLightUserModel.setName(user);

        // add user context to user-model
        bobDefaultLightUserModel.addContext(new ContextValue("location", "Office/Lab/null"));
        bobDefaultLightUserModel.addContext(new ContextValue("partOfDay", "morning"));


        // create default sub-profile
        DefaultProfileSubset defaultLightProfileSubset = new DefaultProfileSubset();
        defaultLightProfileSubset.setSpecificService("Light");

        // create default light state preference
        Preference defaultLightStatePref = new Preference();
        defaultLightStatePref.setType("setLight");
        defaultLightStatePref.setValue("on");
        defaultLightStatePref.setConfidence(0.99);

        // create default light hue preference
        Preference defaultLightHuePref = new Preference();
        defaultLightHuePref.setType("setHue");
        defaultLightHuePref.setValue("yellow");
        defaultLightHuePref.setFormat("color");
        defaultLightHuePref.setConfidence(0.99);

        // add default light preference to user-model
        bobDefaultLightUserModel.addPreference(defaultLightStatePref);
        bobDefaultLightUserModel.addPreference(defaultLightHuePref);

        // add default user-model to default sub-profile
        defaultLightProfileSubset.setUserModel(bobDefaultLightUserModel);

        // add default sub-profile to profile
        profile.addDefaultProfileSubset(defaultLightProfileSubset);

        //***********************************************
        // conditional temperature sub-profile

        // create conditional temperature user-model
        UserModel bobConditionalTemperatureUserModel = new UserModel();
        bobConditionalTemperatureUserModel.setName(user);

        // add user context to user-model
        bobConditionalTemperatureUserModel.addContext(new ContextValue("location", "Office/Lab/null"));
        bobConditionalTemperatureUserModel.addContext(new ContextValue("partOfDay", "morning"));


        // create conditional sub-profile
        ConditionalProfileSubset conditionalTemperatureProfileSubset = new ConditionalProfileSubset();
        conditionalTemperatureProfileSubset.setSpecificService("Thermostat");

        // create and add condition: location = Office/Lab/null
        Condition conditionInOfficeLab = new Condition();
        conditionInOfficeLab.setOperator("equal");
        conditionInOfficeLab.setContextValue(new ContextValue("location", "Office/Lab/null"));
        conditionalTemperatureProfileSubset.addCondition(conditionInOfficeLab);

        // create condition: partOfDay = morning
        Condition conditionMorning = new Condition();
        conditionMorning.setOperator("equal");
        conditionMorning.setContextValue(new ContextValue("partOfDay", "morning"));
        conditionalTemperatureProfileSubset.addCondition(conditionMorning);

        // create conditional thermostat preference
        Preference conditionalThermostatPref = new Preference();
        conditionalThermostatPref.setType("setTemperature");
        conditionalThermostatPref.setValue("75.0");
        conditionalThermostatPref.setFormat("Fahrenheit");
        conditionalThermostatPref.setConfidence(0.99);

        // add conditional thermostat preference to user-model
        bobConditionalTemperatureUserModel.addPreference(conditionalThermostatPref);

        // add conditional user-model to conditional sub-profile
        conditionalTemperatureProfileSubset.setUserModel(bobConditionalTemperatureUserModel);

        // add conditional sub-profile to profile
        profile.addConditionalProfileSubset(conditionalTemperatureProfileSubset);

        //***********************************************
        // conditional light sub-profile

        // create conditional light user-model
        UserModel bobConditionalLightUserModel = new UserModel();
        bobConditionalLightUserModel.setName(user);

        // add user context to user-model
        bobConditionalLightUserModel.addContext(new ContextValue("location", "Office/Lab/null"));
        bobConditionalLightUserModel.addContext(new ContextValue("partOfDay", "morning"));


        // create conditional sub-profile
        ConditionalProfileSubset conditionalLightProfileSubset = new ConditionalProfileSubset();
        conditionalLightProfileSubset.setSpecificService("Light");

        // create conditional light state preference
        Preference conditionalLightStatePref = new Preference();
        conditionalLightStatePref.setType("setLight");
        conditionalLightStatePref.setValue("on");
        conditionalLightStatePref.setConfidence(0.99);

        // create conditional light hue preference
        Preference conditionalLightHuePref = new Preference();
        conditionalLightHuePref.setType("setHue");
        conditionalLightHuePref.setValue("red");
        conditionalLightHuePref.setFormat("color");
        conditionalLightHuePref.setConfidence(0.99);

        // add conditional preferences to user-model
        bobConditionalLightUserModel.addPreference(conditionalLightStatePref);
        bobConditionalLightUserModel.addPreference(conditionalLightHuePref);

        // add conditional user-model to conditional sub-profile
        conditionalLightProfileSubset.setUserModel(bobConditionalLightUserModel);

        // create and add condition: location = Office/Lab/null
        //Condition conditionInOfficeLab = new Condition();
        //conditionInOfficeLab.setOperator("equal");
        //conditionInOfficeLab.setContextValue(new ContextValue("location", "Office/Lab/null"));
        conditionalLightProfileSubset.addCondition(conditionInOfficeLab);

        // create condition: partOfDay = morning
        //Condition conditionMorning = new Condition();
        //conditionMorning.setOperator("equal");
        //conditionMorning.setContextValue(new ContextValue("partOfDay", "morning"));
        conditionalLightProfileSubset.addCondition(conditionMorning);

        // add conditional sub-profile to profile
        profile.addConditionalProfileSubset(conditionalLightProfileSubset);

        //***********************************************

        // add profile to user-profile
        //bobUserProfile.setProfile(profile);

        // print serialized user-profile
        String serialBobUserProfile = profile.toJSON();
        System.out.println(serialBobUserProfile);
    }

}
