package ryx.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.swing.plaf.TableHeaderUI;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * 在redis中保存的有国家和大区的关系
 * hset  areas AREA_US US
 * hset  areas AREA_CT TW,HK
 * hset  areas AREA_AR PK,KW,SA
 * hset  areas AREA_IN IN
 *./bin/kafka-console-consumer.sh --bootstrap-server hadoop01:9092,hadoop02:9092,hadoop03:9092 --topic allDataClean--from-beginning
 *
 * 我们需要返回kv对的，就要考虑HashMap
 */
public class testSink implements SourceFunction<HashMap<String,String>> {
    private Logger logger= LoggerFactory.getLogger(testSink.class);
    private boolean isRunning =true;
    private Jedis jedis=null;
    private final long SLEEP_MILLION=5000;
    public void run(SourceContext<HashMap<String, String>> ctx) throws Exception {
        this.jedis = new Jedis("hadoop102", 6379);
        HashMap<String, String> kVMap = new HashMap<String, String>();
        while(isRunning){
            try{
                kVMap.clear();
                Map<String, String> areas = jedis.hgetAll("click4");
                for(Map.Entry<String,String> entry:areas.entrySet()){
                    // key :大区 value：国家
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String[] splits = value.split(",");
                    System.out.println("key:"+key+"，--value："+value);
                    for (String split:splits){
                        // key :国家value：大区
                        kVMap.put(split, key);
                    }
                }
                if(kVMap.size()>0){
                    ctx.collect(kVMap);
                }else {
                    logger.warn("从redis中获取的数据为空");
                }
                Thread.sleep(SLEEP_MILLION);
            }catch (JedisConnectionException e){
                logger.warn("redis连接异常，需要重新连接",e.getCause());
                jedis = new Jedis("hadoop102", 6379);
            }catch (Exception e){
                logger.warn(" source 数据源异常",e.getCause());
            }
        }
    }

    public void cancel() {
        isRunning=false;
        while(jedis!=null){
            jedis.close();
        }
    }
}
