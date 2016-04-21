package com.bezirk.protocols.penguin.v01;

import java.util.ArrayList;
import java.util.List;

public class ConditionalProfileSubset extends ProfileSubset {
    /* properties */

    private List<Condition> hasCondition = null;

	/* constructors */

    public ConditionalProfileSubset() {
        super();
        this.hasCondition = new ArrayList<Condition>();
    }
	
	/* getters and setters */

    public List<Condition> getConditions() {
        return this.hasCondition;
    }

    // hasCondition
    public void setConditions(List<Condition> _v) {
        this.hasCondition = _v;
    }

    public void addCondition(Condition _v) {
        this.hasCondition.add(_v);
    }

    @Override
    public String toString() {
        return "ConditionalProfileSubset [hasCondition=" + hasCondition + "]";
    }


}
