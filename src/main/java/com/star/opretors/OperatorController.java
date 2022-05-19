package com.star.opretors;

import com.star.instance.MyOprator;
import com.star.instance.OpratorsPram;
import com.star.opretors.transforms.OpCount;
import com.star.opretors.transforms.OpFilt;
import com.star.opretors.transforms.OpKill;
import com.star.opretors.transforms.OpMap;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Data
public class OperatorController {
    private OpratorsPram nowOp;

    public MyOprator getOp() throws Exception {
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
            case "OpNew":{
                //外部jar所在位置
                /**
                 * 样例参数
                 * "file:F:\\tmp\\smart-base2\\BaseHub\\target\\BaseHub-1.0-SNAPSHOT-jar-with-dependencies.jar&com.star.opretors.transforms.OpCount";
                 */

                String parm = nowOp.getKey();

                String[] args1 = parm.split("&"); //切割json

                URLClassLoader urlClassLoader =null;
                Class<?> MyTest = null;

                //通过URLClassLoader加载外部jar
                urlClassLoader = new URLClassLoader(new URL[]{new URL(args1[0])});
                //获取外部jar里面的具体类对象
                MyTest = urlClassLoader.loadClass(args1[1]);
                //创建对象实例
                MyOprator instance = (MyOprator)MyTest.newInstance();

                return instance;

            }
            default: break;
        }
        return null;
    }
}