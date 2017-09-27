package com;

public class IsCover {
	boolean isTrue;

	public boolean isTrue() {
		return isTrue;
	}

	public IsCover setTrue(boolean isTrue) {
		this.isTrue = isTrue;
		return this;
	}
	
	public String toString() {
		if (this.isTrue) {
			return "true";
		}
		else {
			return "false";
		}
	}
}
