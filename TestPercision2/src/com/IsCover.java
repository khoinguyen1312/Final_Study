package com;

public class IsCover {
	boolean isTrue;

	public boolean isTrue() {
		return isTrue;
	}

	public void setTrue(boolean isTrue) {
		this.isTrue = isTrue;
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
