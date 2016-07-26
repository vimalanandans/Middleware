package test;

import com.bezirk.middleware.messages.ProtocolRole;

public class ProtocolRoleTest extends ProtocolRole {
    private static final String evts[] =
            {EventTest.TOPIC};

    @Override
    public String getRoleName() {
        return ProtocolRoleTest.class.getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Simple protocol role to test";
    }

    @Override
    public String[] getEventTopics() {
        return evts == null ? null : evts.clone();
    }

    @Override
    public String[] getStreamTopics() {
        return new String[0];
    }
}
