package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;
import hudson.tasks.test.TestResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Info {
	protected String name;
	protected boolean isConfig = false;
	protected Map<Integer, ResultData> buildResults = new TreeMap<Integer, ResultData>(Collections.<Integer>reverseOrder());
	protected Map<String, String> properties = new HashMap<String, String>();
	protected Integer lastHandledPropertiesBuildNumber = 0;

	public void handleProperties(Integer buildNumber, TestResult result) {
		try {
			Method getPropertiesMethod = result.getClass().getMethod("getProperties");
			Object properiesReturnValue = getPropertiesMethod.invoke(result);	
			if (properiesReturnValue instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<String, String> propertiesMap = (Map<String, String>)properiesReturnValue;
				// merge or update entrie
				for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
					if (!properties.containsKey(entry.getKey()) || buildNumber > lastHandledPropertiesBuildNumber) {
						// update
						this.properties.put(entry.getKey(), entry.getValue());
					}
				}
    		}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) { //NOSONAR
		}

		lastHandledPropertiesBuildNumber = buildNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Integer, ResultData> getBuildPackageResults() {
		return buildResults;
	}

	public ResultData getBuildResult(Integer buildNumber) {
		return buildResults.get(buildNumber);
	}

	public void setBuildPackageResults(Map<Integer, ResultData> buildResults) {
		this.buildResults = buildResults;
	}

	public abstract Map<String, ? extends Info> getChildren();

	public boolean isConfig() {
		return isConfig;
	}

	public void setConfig(boolean config) {
		isConfig = config;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
