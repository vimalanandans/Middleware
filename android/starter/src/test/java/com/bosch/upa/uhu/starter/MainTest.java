/*
package com.bosch.upa.uhu.starter;

import android.content.Intent;

import com.bosch.upa.uhu.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

*/
/**
 *Created by AJC6KOR on 11/3/2015.
 **//*


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class MainTest {

    private MainService service = new MainService();

  @Before
    public void setup() {

        service = Robolectric.setupService(MainService.class);
    }


    @Test
    public void testOnStart() {
        Intent intent = new Intent();
        intent.setAction("START_UHU");
        service.onCreate();
        assertNotNull(service.onStartCommand(intent,1,1));
    }
}
*/
