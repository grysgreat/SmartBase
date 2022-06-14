package com.star;

import com.star.Job.GetMySource;
import com.star.Job.SpJob;
import com.star.instance.MyOprator;
import com.star.instance.OpratorsPram;
import com.star.model.KafkaProducer;
import com.star.opretors.OperatorController;
import com.star.opretors.mapper.MyRedisMapper;
import com.star.sink.MysqlSink;
import com.star.utils.JobPramUtil;
import com.star.utils.ParameterHelper;
import com.star.utils.TestUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.List;


/**
 * --jobJson {\"JobNum\":1,\"job1\":{\"source\":{\"type\":\"text\",\"url\":\"F:/tmp/test.csv\"},\"operator\":{\"num\":2,\"operator1\":{\"type\":\"OpFilt\",\"key\":\"star\"},\"operator2\":{\"type\":\"OpFilt\",\"key\":\"aaa\"}},\"dest\":{\"type\":\"mysql\",\"url\":\"192.168.10.1\",\"username\":\"root\",\"password\":\"123456\",\"basename\":\"sys\",\"tablename\":\"clicks\",\"port\":\"3306\"}}}  --saveUrl hdfs://hadoop102:8020/rng/ck
 */

//test: [{"operators":[{"type":"timer","key":"15"},{"type":"OpKill","key":"123"}],"source":{"types":"kafka","id":19,"url":"hadoop102","port":9092,"topic":"kfkSQL"},"dest":{"types":"kafka","id":19,"url":"hadoop102","destPort":9092,"topic":"kfkPort"}}]

// --jobJson [{\"operators\":[{\"type\":\"OpTime\",\"key\":\"15\"},{\"type\":\"OpKill\",\"key\":\"123\"}],\"source\":{\"types\":\"kafka\",\"id\":19,\"url\":\"hadoop102\",\"port\":9092,\"topic\":\"kfkSQL\"},\"dest\":{\"types\":\"kafka\",\"id\":19,\"url\":\"hadoop102\",\"port\":9092,\"topic\":\"kfkPort\"}}]  --saveUrl hdfs://hadoop102:8020/rng/ck
public class JobController {
    public static void main(String[] args) throws Exception {


    ParameterTool parameterTool = ParameterTool.fromArgs(args);

        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);

        //String parmJson = parameterHelper.getParmJson();

        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        // 1.1 设置CK&状态后端
        executionEnvironment.setStateBackend(new FsStateBackend(parameterHelper.getSaveUrl()+"/tmplate"));
        executionEnvironment.enableCheckpointing(5000L);
        executionEnvironment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        executionEnvironment.getCheckpointConfig().setCheckpointTimeout(10000L);
        executionEnvironment.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        executionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);


        parameterHelper.setParmJson(args[1]);

        String jsonPram = parameterHelper.getParmJson();

        TestUtil jobUtil = new TestUtil();



        jobUtil.addJobList(jsonPram);



        for (ParameterHelper jobPram : jobUtil.getJobList()) {

            System.out.println("-------------here   "+jobPram);
            if(jobPram.getSorceType().equals("MySource")) {
                SpJob<Object> objectSpJob = new SpJob<>();
                objectSpJob.dispose(executionEnvironment,jobPram);
                continue;
            }

            DataStreamSource<String> streamIn=new GetMySource().getSource(jobPram,executionEnvironment);
            streamIn.print();



            SingleOutputStreamOperator<String> stream = streamIn.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value));


            List<OpratorsPram> opList = jobPram.getOpList();

            for (OpratorsPram opratorsPram : opList) {

                OperatorController operatorController = new OperatorController();
                operatorController.setNowOp(opratorsPram);
                MyOprator op = operatorController.getOp();
                stream=op.getOpOut(stream);
            }


            String dest=jobPram.getDestType();
            switch (dest){
                case "mysql":{
                    Configuration conf = new Configuration();
                    conf.setString("baseName",jobPram.getDestBase());
                    conf.setString("tableName",jobPram.getDestTopic());
                    conf.setString("username",jobPram.getDestUserName());
                    conf.setString("password",jobPram.getDestUserPwd());
                    conf.setString("dburl",jobPram.getDestUrl()+":3306");

                    executionEnvironment.getConfig().setGlobalJobParameters(conf);

                    DataStream<String[]> sourceStream = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                            .map((MapFunction<String, String[]>) value -> {
                                String[] args1 = value.split(","); //切割json
                                return args1;
                            }).returns(new TypeHint<String[]>() {});
                    sourceStream.addSink(new MysqlSink());
                    break;
                }
                case "kafka": {

                    stream.addSink(KafkaProducer.getKafkaProducer(jobPram.getDestTopic(),jobPram.getDestUrl()+":"+jobPram.getDestPort()));

                    break;
                }
                case "redis":{

                    DataStream<Tuple2<String,String>> sourceStream = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                            .map((MapFunction<String, Tuple2<String,String>>) value -> {
                                String[] args1 = value.split(","); //切割json
                                Tuple2<String, String> tp = new Tuple2<String, String>(args1[0],args1[1]);
                                return tp;
                            }).returns(new TypeHint<Tuple2<String,String>>() {});

                    // 创建一个到redis连接的配置
                    FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost(jobPram.getDestUrl()).setPort(jobPram.getDestPort()).build();

                    MyRedisMapper myRedisMapper = new MyRedisMapper();

                    myRedisMapper.setTopic(jobPram.getDestTopic());

                    sourceStream.addSink(new RedisSink<Tuple2<String,String>>(conf,myRedisMapper));
                    break;
                }
                case "port" :{
                    break;
                }
                case "text":{
                    stream.writeAsText(jobPram.getDestUrl()).setParallelism(1);
                    break;
                }
                default:break;
            }
            stream.print(": ");
        }




        executionEnvironment.execute();


    }
}


