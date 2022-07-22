package com.star.model;
import com.star.instance.OpcUaClientFactory;
import com.star.utils.OpcUaConfig;
import com.star.utils.OpcUaOperationSupport;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOpcUaSource extends RichSourceFunction<Map<String,DataValue>> {
    //OpcUa配置项
    OpcUaConfig opcUaConfig = null;

    private OpcUaOperationSupport opcUaOperationSupport = null;
    private OpcUaClient opcClient = null;
    private static List<UaNode> uaNodes = new ArrayList<>();
    private static UaNode NODE = null;

    private boolean running = true;

    public MyOpcUaSource(OpcUaConfig opcUaConfig) {
        this.opcUaConfig = opcUaConfig;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //两种登录方式，用户名登录和匿名登录
        if(opcUaConfig.getAnonymous()){
            opcClient = OpcUaClientFactory.createOpcClientWithAnonymous(opcUaConfig.getServerUrl());
        }else{
            opcClient = OpcUaClientFactory.createOpcClientWithUserName(opcUaConfig.getServerUrl(),opcUaConfig.getUserName(),opcUaConfig.getPassword());
        }
        opcClient.connect().get();

        opcUaOperationSupport = new OpcUaOperationSupport();
        uaNodes = opcUaOperationSupport.browseNode(opcClient,null,new ArrayList<>());
        for(UaNode node:uaNodes){
            String nodeId = node.getNodeId().getIdentifier().toString();
            if(nodeId.equals(opcUaConfig.getIdentifier())){
                NODE = node;
                break;
            }
        }
    }

    @Override
    public void run(SourceContext<Map<String, DataValue>> sourceContext) throws Exception {
        while(true){
            Map<String,DataValue> map = opcUaOperationSupport.browseNodeData(opcClient,NODE,new HashMap<>());
            sourceContext.collect(map);
            //间隔一秒
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}