package org.jenkinsci.plugins.testresultsanalyzer;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.jenkinsci.plugins.testresultsanalyzer.config.UserConfig;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.Info;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.ResultInfo;
import org.jenkinsci.plugins.testresultsanalyzer.BuildDescription;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsTreeUtil {
    
    public JSONObject getJsTree(List<Integer> builds, List<BuildDescription> buildDescriptions, ResultInfo resultInfo, boolean hideConfigMethods) {
        JSONObject tree = new JSONObject();

        JSONArray buildJson = new JSONArray();
        for (Integer buildNumber : builds) {
            buildJson.add(buildNumber.toString());
        }
        tree.put("builds", buildJson);

        JSONArray results = new JSONArray();
        for (Map.Entry<String, ? extends Info> entry : resultInfo.getPackageResults().entrySet()) {
            results.add(createJson(builds, entry.getValue(), hideConfigMethods));
        }
        tree.put("results", results);

        if (buildDescriptions.size() > 0) {
            JSONArray buildDescriptionsJs = new JSONArray();
            for (BuildDescription descr : buildDescriptions) {
                JSONObject obj = new JSONObject();
                obj.put("short", descr.getShort());
                obj.put("long", descr.getLong());
                buildDescriptionsJs.add(obj);
            }
            tree.put("buildDescriptions", buildDescriptionsJs);
        }
        return tree;
    }

    private JSONObject createJson(List<Integer> builds, Info info, boolean hideConfigMethods, Map<String, String> properties) {
        JSONObject baseJson = new JSONObject();

        baseJson.put("text", info.getName());
        baseJson.put("buildResults", getBuilds(builds, info));
        baseJson.put("children", getChildren(builds, info, hideConfigMethods, properties));

        return baseJson;
    }

    private JSONObject createJson(List<Integer> builds, Info info, boolean hideConfigMethods) {
        Map<String, String> properties = new HashMap<String, String>();
        return createJson(builds, info, hideConfigMethods, properties);
    }

    private JSONArray getBuilds(List<Integer> builds, Info info) {
        JSONArray treeDataJson = new JSONArray();
        for (Integer buildNumber : builds) {
            treeDataJson.add(getBuild(buildNumber, info));
        }
        return treeDataJson;
    }

    private JSONArray getChildren(List<Integer> builds, Info info, boolean hideConfigMethods, Map<String, String> properties) {
        Map<String, ? extends Info> childrenInfo = info.getChildren();
        if (childrenInfo == null)
            return new JSONArray();
    
        properties.putAll(info.getProperties());
    
        JSONArray children = new JSONArray();
        for (Map.Entry<String, ? extends Info> entry : childrenInfo.entrySet()) {
            if (!hideConfigMethods || !entry.getValue().isConfig()) {
                String name = entry.getValue().getName();
                JSONObject json = createJson(builds, entry.getValue(), hideConfigMethods, properties);
                if (properties.containsKey("Description for " + name)) {
                    String descr = properties.get("Description for " + name);
                    if (descr != null) {
                        json.put("description", descr);
                    }
                }
                if (properties.containsKey("DocLink for " + name)) {
                    String link = properties.get("DocLink for " + name);
                    if (link != null) {
                        json.put("link", link);
                    }
                }
                children.add(json);
            }
        }

        return children;
    }

    private JSONObject getBuild(Integer buildNumber, Info info) {
        JSONObject json = new JSONObject();
        json.put("buildNumber", buildNumber.toString());

        ResultData result = info.getBuildResult(buildNumber);
        if (result == null) {
            json.put("status", "N/A");
        } else {
            json.put("totalTests", result.getTotalTests());
            json.put("totalFailed", result.getTotalFailed());
            json.put("totalPassed", result.getTotalPassed());
            json.put("totalSkipped", result.getTotalSkipped());
            json.put("totalTimeTaken", result.getTotalTimeTaken());
            json.put("status", result.getStatus());
            json.put("url", result.getUrl());
        }


        return json;
    }
}
