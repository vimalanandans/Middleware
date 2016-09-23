package com.bezirk.middleware.core.proxy;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigTest {
    private static final String APP_NAME = "TEST_APP_NAME";
    private static final String GROUP_NAME = "TEST_GROUP_NAME";
    private static final String PACKAGE_NAME_1 = "com.bezirk.test1";
    private static final Config.Level PACKAGE_LOG_LEVEL_1 = Config.Level.INFO;
    private static final String PACKAGE_NAME_2 = "com.bezirk.test2";
    private static final Config.Level PACKAGE_LOG_LEVEL_2 = Config.Level.ERROR;
    private static final Config.Level level = Config.Level.DEBUG;

    /**
     * Config creation using {@link Config.ConfigBuilder}
     */
    @Test
    public void test() {
        Config config = new Config.ConfigBuilder().setAppName(APP_NAME)
                .setGroupName(GROUP_NAME)
                .setLogLevel(level)
                .setPackageLogLevel(PACKAGE_NAME_1, PACKAGE_LOG_LEVEL_1)
                .setPackageLogLevel(PACKAGE_NAME_2, PACKAGE_LOG_LEVEL_2)
                .create();
        Gson gson = new Gson();
        String configJSON = gson.toJson(config);
        Config reConstructedConfig = gson.fromJson(configJSON, Config.class);
        String reConstructedConfigJSON = gson.toJson(reConstructedConfig);

        //System.out.println(configJSON + "\n" + reConstructedConfigJSON);

        //test serialization-deserialization
        assertTrue(configJSON.equalsIgnoreCase(reConstructedConfigJSON));

        //test content values
        assertTrue(config.getAppName().equalsIgnoreCase(APP_NAME));
        assertTrue(config.getGroupName().equalsIgnoreCase(GROUP_NAME));
        assertTrue(config.getLogLevel().equals(level));
        assertTrue(config.getPackageLogLevelMap().size() == 2);
        assertTrue(config.getPackageLogLevelMap().get(PACKAGE_NAME_1).equals(PACKAGE_LOG_LEVEL_1));
        assertTrue(config.getPackageLogLevelMap().get(PACKAGE_NAME_2).equals(PACKAGE_LOG_LEVEL_2));
    }

    /**
     * Config creation using {@link Config#Config()}
     */
    @Test
    public void test1() {
        Config config = new Config();
        Gson gson = new Gson();
        String configJSON = gson.toJson(config);
        Config reConstructedConfig = gson.fromJson(configJSON, Config.class);
        String reConstructedConfigJSON = gson.toJson(reConstructedConfig);

        //System.out.println(configJSON + "\n" + reConstructedConfigJSON);

        //test serialization-deserialization
        assertTrue(configJSON.equalsIgnoreCase(reConstructedConfigJSON));
    }
}
