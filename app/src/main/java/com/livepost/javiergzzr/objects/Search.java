package com.livepost.javiergzzr.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vyz on 2016-01-31.
 */
public class Search {
    String index;
    String type;
    Map<String,Map<String,String>> query;
    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public Map<String, Map<String, String>> getQuery() {
        return query;
    }


    public Search(){}
    public Search(String index,String type,String rawQuery){
        this.index = index;
        this.type = type;
        buildQuery(rawQuery);
    }
    void buildQuery(String rawQuery){
        Map<String,String> s = new HashMap<String,String>();
        if(rawQuery.contains("category:")){
            s.put("query",rawQuery);
        }else{
            s.put("query","*"+rawQuery+"*");
        }
        Map<String,Map<String,String>> qs = new HashMap<String,Map<String,String>>();
        qs.put("query_string",s);
        query = qs;
    }
}
