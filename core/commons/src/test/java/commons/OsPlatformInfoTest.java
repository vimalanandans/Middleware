package commons;

import com.bezirk.devices.OsPlatformInfo;

import org.junit.Test;

import static org.junit.Assert.*;

public class OsPlatformInfoTest {


    @Test
    public void testUPADevice() {
        if (!OsPlatformInfo.getCurrentOSPlatform().equals(OsPlatformInfo.UPA_SERV__RUNTIME_ENV__JAVA)
                || !OsPlatformInfo.getCurrentOSPlatform().equals(OsPlatformInfo.UPA_SERV__RUNTIME_ENV__JAVA)) {
            fail("OsPlatformInfo is neither java nor android");
        }
        //assertEquals()
    }


}
