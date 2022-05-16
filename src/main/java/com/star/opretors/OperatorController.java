package com.star.opretors;

import com.star.instance.MyOprator;
import com.star.instance.OpratorsPram;
import com.star.opretors.transforms.OpCount;
import com.star.opretors.transforms.OpFilt;
import com.star.opretors.transforms.OpKill;
import com.star.opretors.transforms.OpMap;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.util.ArrayList;
import java.util.List;

@Data
public class OperatorController {
    private OpratorsPram nowOp;

    public MyOprator getOp(){
        switch (nowOp.getOpType()){
            case "OpCount":{
                return new OpCount();
            }
            case "OpFilt":{
                OpFilt opFilt = new OpFilt();
                opFilt.setKey(nowOp.getKey());
                return opFilt;
            }
            case "OpKill":{
                OpKill opKill = new OpKill();
                opKill.setKey(nowOp.getKey());
                return opKill;
            }
            case "OpMap":{
                OpMap OpMap = new OpMap();
                String str=nowOp.getKey();
                String[] split = str.split(",");
                List<Integer> tmp = new ArrayList<>();
                for (String s : split) {
                    tmp.add(Integer.parseInt(s));
                }
                OpMap.setA(tmp);
                return OpMap;
            }
            default: break;
        }
        return null;
    }
}
