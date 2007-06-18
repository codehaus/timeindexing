// AbstractTimeSpecifier.java

package com.timeindexing.time;

/**
 * An abstract class for objects that specify some modifier for a Timestamp.
 */
public abstract class AbstractTimeSpecifier implements TimeSpecifier {
    // how many modification items need to be done
    long howMany = 0;

    // what direction in time is the modification
    TimeDirection modificationDirection = null;

    // is there a TimeSpecifier that needs to be applied
    // before this one
    TimeSpecifier predSpecfier = null;


    /**
     * Do this TimeSpecifier, then do another TimeSpecifier.
     * This gets more modification.
     * N.B.  spec1.then(spec2) == spec2.afterDoing(spec1)
     *       spec1.then(spec2).then(spec3) == spec3.afterDoing(spec2.afterDoing(spec1))
     */
    public TimeSpecifier then(TimeSpecifier specfier) {
	TimeSpecifier newSpecfier = specfier.afterDoing(this);

	return newSpecfier;
    }

    /**
     * After doing the specfied TimeSpecifier, do this TimeSpecifier.
     * This gets more modification.
     * N.B.  spec1.then(spec2) == spec2.afterDoing(spec1)
     *       spec1.then(spec2).then(spec3) == spec3.afterDoing(spec2.afterDoing(spec1))
     */
    public TimeSpecifier afterDoing(TimeSpecifier specfier) {
	    try {
		// clone this object, and bind it to the predecessor
		AbstractTimeSpecifier clonedSpecfier = (AbstractTimeSpecifier)this.clone();


		if (clonedSpecfier.hasPredecessor()) {
		    // if the clone has a predecessor
		    // then we need to clone all the predecessors
		    // until we can attach the specfier to the end of the chain
		    TimeSpecifier clonedPred = clonedSpecfier.predSpecfier.afterDoing(specfier);
		    clonedSpecfier.predSpecfier = clonedPred;
		} else {
		    // there was no predecessor, so attach the specfier
		    clonedSpecfier.predSpecfier = specfier;
		}

		return clonedSpecfier;
	    } catch (CloneNotSupportedException cnse) {
		// the clone failed, so just use this object
		predSpecfier = specfier;
		return this;
	    }
    }

    /**
     * Does this TimeSpecifier have a predecessor specfier.
     */
    protected boolean hasPredecessor() {
	if (predSpecfier == null) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Clone this object.
     */
    public Object clone() throws CloneNotSupportedException {
	return super.clone();
    }

    /**
     * Get the value.
     * When TimeDirection is FORWARD, then the value is positive,
     * and when TimeDirection is BACKWARD, then the value is negative.
     */
    public long value() {
	if (modificationDirection == TimeDirection.BACKWARD) {
	    return -howMany;
	} else {		// modificationDirection == TimeDirection.FORWARD
	    return howMany;
	}
    }

    /**
     * Get how many.
     */
    public long getHowMany() {
	return howMany;
    }

    /**
     * Set how many.
     */
    void setHowMany(long count) {
	howMany = count;
    }

    /**
     * Get the direction.
     */
    public TimeDirection getDirection() {
	return modificationDirection;
    }

    /**
     * Set the direction.
     */
    void setDirection(TimeDirection direction) {
	modificationDirection = direction;
    }

    /**
     * Get the predecessor TimeSpecifier.
     */
    public TimeSpecifier getPredecessor() {
	return predSpecfier;
    }

    /**
     * Set the predecessor TimeSpecifier.
     */
    void setPredecessor(TimeSpecifier specfier) {
	predSpecfier = specfier;
    }

}
