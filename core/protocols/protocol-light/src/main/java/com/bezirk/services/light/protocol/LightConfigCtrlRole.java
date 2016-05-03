package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.ProtocolRole;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */
public class LightConfigCtrlRole extends ProtocolRole {

    static final String description = "This the protocol role that the light service must subscribe to";
    //Adding events, need to check the test accordingly
    private static final String evts[] =
            {ConfigureBulbLocation.TOPIC,
                    ConfigurePolicy.TOPIC,
                    ActuateBulb.TOPIC,
                    BulbStatus.TOPIC,
                    RequestActiveLights.TOPIC,
                    RequestLightLocation.TOPIC,
                    RequestKing.TOPIC,
                    MakeKing.TOPIC,
                    RequestPolicy.TOPIC,
                    RequestLightStateForLocation.TOPIC,
                    DiscoverLocations.TOPIC,
                    DiscoverLocationResponse.TOPIC,
                    RequestLocationWithLightDetails.TOPIC,
                    ResponseLocationWithLightDetails.TOPIC,
                    ClearUserDetails.TOPIC,
                    TestLocationAndLightSetUpRequest.TOPIC,
                    TestLocationAndLightSetUpResponse.TOPIC};

    @Override
    public String getRoleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String[] getEventTopics() {
        return evts == null ? null : evts.clone();
    }

    @Override
    public String[] getStreamTopics() {
        return null;
    }

}
