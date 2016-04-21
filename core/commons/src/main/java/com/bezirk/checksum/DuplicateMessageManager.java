package com.bezirk.checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;


public final class DuplicateMessageManager {
    private static final Logger logger = LoggerFactory.getLogger(DuplicateMessageManager.class);

    private static final int SIZE_OF_SET = 1024;
    //	private static final LinkedHashSet<byte[]> duplicateEventsCheck = new LinkedHashSet<byte[]>();
    private static final LinkedHashSet<String> duplicateEventsCheck = new LinkedHashSet<String>();

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private DuplicateMessageManager() {

    }

    public static boolean checkDuplicateEvent(final byte[] checksumOfMsg) {
        String string = CheckSumUtil.bytesToHex(checksumOfMsg);
        if (null == checksumOfMsg) {
            return true;
        }
        if (duplicateEventsCheck.contains(string)) {
            return true;
        }
        if (duplicateEventsCheck.size() == SIZE_OF_SET) {
            duplicateEventsCheck.remove(duplicateEventsCheck.iterator().next());
        }

        duplicateEventsCheck.add(string);
        return false;
    }
}
