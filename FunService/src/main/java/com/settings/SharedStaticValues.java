package com.settings;

/**
 * 
 * @author lancepoehler
 *
 */
public class SharedStaticValues {

	public static final int CLIENT_HEART_BEAT_IN_MINUTES = 3; //This is larger than the client to account for missed signals (60 seconds)

    public static enum UpdateType { UPDATE_TYPE, DELETE_TYPE; }

    public static final String MAIN_PAGE_DISPLAY_FIELDS = "{totalPeopleTrackingCount: 0, currentTrackings: 0, currentlyTracking: 0, imagePath: 0}";

}
