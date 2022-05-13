package com.star.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.LinkedMap;

import java.util.*;

public class Json2Map {
    /**
     *<p>类描述：复杂json字符串转换为Map，包含数组时value为List。</p>
     */
    public static Map json2Map(String json) {
        LinkedMap map = new LinkedMap();
        JSONObject js = JSONObject.fromObject(json);
        populate(js, map);
        return map;
    }

    /**
     *<p>类描述：json中的键值对解析成map。</p>
     */
    private static Map populate(JSONObject jsonObject, Map map) {
        for (Iterator iterator = jsonObject.entrySet().iterator(); iterator
                .hasNext();) {
            String entryStr = String.valueOf(iterator.next());
            String key = entryStr.substring(0, entryStr.indexOf("="));
            String value = entryStr.substring(entryStr.indexOf("=") + 1,
                    entryStr.length());
            if (jsonObject.get(key).getClass().equals(JSONObject.class)) {
                HashMap _map = new HashMap();
                map.put(key, _map);
                populate(jsonObject.getJSONObject(key), ((Map) (_map)));
            } else if (jsonObject.get(key).getClass().equals(JSONArray.class)) {
                ArrayList list = new ArrayList();
                map.put(key, list);
                populateArray(jsonObject.getJSONArray(key), list);
            } else {
                map.put(key, jsonObject.get(key));
            }
        }

        return map;
    }

    /**
     *<p>类描述：如果是键对应数组,则返回一个list到上级的map里。</p>
     */
    private static void populateArray(JSONArray jsonArray, List list) {
        for (int i = 0; i < jsonArray.size(); i++)
            if (jsonArray.get(i).getClass().equals(JSONArray.class)) {
                ArrayList _list = new ArrayList();
                list.add(_list);
                populateArray(jsonArray.getJSONArray(i), _list);
            } else if (jsonArray.get(i).getClass().equals(JSONObject.class)) {
                HashMap _map = new HashMap();
                list.add(_map);
                populate(jsonArray.getJSONObject(i), _map);
            } else {
                list.add(jsonArray.get(i));
            }
    }
}