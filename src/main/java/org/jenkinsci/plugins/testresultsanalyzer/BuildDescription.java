package org.jenkinsci.plugins.testresultsanalyzer;

public class BuildDescription {

	private String strShort = "";
	private String strLong = "";

	public String getShort() {
		return strShort;
	}

	public void setShort(String strShort) {
		this.strShort = strShort;
	}

	public String getLong() {
		return strLong;
	}

	public void setLong(String strLong) {
		this.strLong = strLong;
	}

    public BuildDescription(String strShort, String strLong) {
        setShort(strShort);
        setLong(strLong);
    }
}
