package commons;

import com.bezirk.devices.UhuOsPlatform;

import org.junit.Test;

import static org.junit.Assert.*;

public class UhuOsPlatformTest {


    @Test
    public void testUPADevice() {
        if (!UhuOsPlatform.getCurrentOSPlatform().equals(UhuOsPlatform.UPA_SERV__RUNTIME_ENV__JAVA)
                || !UhuOsPlatform.getCurrentOSPlatform().equals(UhuOsPlatform.UPA_SERV__RUNTIME_ENV__JAVA)) {
            fail("UhuOSPlatform is neither java nor android");
        }
        //assertEquals()
    }


}
