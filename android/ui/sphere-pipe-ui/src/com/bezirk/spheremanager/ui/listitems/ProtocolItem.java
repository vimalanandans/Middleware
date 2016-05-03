package com.bezirk.spheremanager.ui.listitems;

public class ProtocolItem implements AbstractPolicyListItem {
    public static final String TAG = "PolicyListItem";
    private String policyName;
    private String policyReason;
    private boolean isActive;
    private boolean isNew;

    public ProtocolItem(String policyName, String policyReason,
                        boolean isActive, boolean isNew) {
        super();
        this.policyName = policyName;
        this.policyReason = policyReason;
        this.isActive = isActive;
        this.isNew = isNew;

    }

    public String getProtocolName() {
        return policyName;
    }

    /**
     * Human readable name for the protocol. E.g. UhU may refer to it to explain pipe policy.
     *
     * @return The Protocol role description
     */
    public String getDescription() {
        return policyReason;
    }

    @Override
    public boolean equals(Object p) {
        if (p instanceof ProtocolItem) {
            return this.getProtocolName().equals(((ProtocolItem) p).getProtocolName());
        }
        return false;
    }

//	/**
//	 * Concrete classes must implement this method to return the specific array of event topics.
//	 * @return array of event topics
//	 */
//	public  String[] getEventTopics();
//	/**
//	 * Concrete classes must implement this method to return the specific array of stream topics.
//	 * @return array of stream topics
//	 */
//	public  String[] getStreamTopics();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((policyName == null) ? 0 : policyName.hashCode());
        result = prime * result + ((policyReason == null) ? 0 : policyReason.hashCode());
        return result;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public enum PolicyDirection {
        INBOUND,
        OUTBOUND
    }
//
//	@Override
//	public View getView(LayoutInflater layoutInflater, ViewGroup parent) {
//		View view;
//		view = (View) layoutInflater.inflate(R.layout.layout_policy_entry,
//				parent);
//		TextView textViewName = (TextView) view.findViewById(R.id.policy_name);
//		textViewName.setText(policyName);
//		TextView textViewReason = (TextView) view
//				.findViewById(R.id.policy_reason);
//		textViewReason.setText(policyReason);
//
//		final CheckBox policy_active = (CheckBox) view
//				.findViewById(R.id.check_policy);
//
//		if (isActive()) {
//			policy_active.setChecked(true);
//		} else {
//			policy_active.setChecked(false);
//		}
//
//		if (isNew) {
//			TextView textViewNew = (TextView) view
//					.findViewById(R.id.policy_changed);
//			textViewNew.setText("New!");
//		}
//
//
//		return view;
//	}

}
