package commons;

import com.bezirk.devices.BezirkOsPlatform;

import org.junit.Test;

import static org.junit.Assert.*;

public class BezirkOsPlatformTest {


    @Test
    public void testUPADevice() {
        if (!BezirkOsPlatform.getCurrentOSPlatform().equals(BezirkOsPlatform.UPA_SERV__RUNTIME_ENV__JAVA)
                || !BezirkOsPlatform.getCurrentOSPlatform().equals(BezirkOsPlatform.UPA_SERV__RUNTIME_ENV__JAVA)) {
            fail("BezirkOsPlatform is neither java nor android");
        }
        //assertEquals()
    }


}
