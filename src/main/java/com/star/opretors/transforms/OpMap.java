package com.star.opretors.transforms;

import com.star.instance.MyOprator;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.io.Serializable;
import java.util.List;

@Data
public class OpMap implements MyOprator<String>, Serializable {
    private List<Integer> a;
    @Override
    public SingleOutputStreamOperator<String> getOpOut(SingleOutputStreamOperator<String> stream){

        SingleOutputStreamOperator<String> sourceStream = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                .map((MapFunction<String, String>) value -> {
                    String[] args1 = value.split(","); //切割json
                    String ans="";
                    for (Integer integer : a) {
                        ans+=args1[integer-1];
                        ans+=",";
                    }
                    ans = ans.substring(0, ans.length()-1);
                    return ans;
                }).returns(new TypeHint<String>() {});
        return sourceStream;
    }
}
