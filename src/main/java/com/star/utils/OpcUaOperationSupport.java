package com.star.utils;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class OpcUaOperationSupport {

    private static final Logger logger = LoggerFactory.getLogger(OpcUaOperationSupport.class);

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    /**
     * 遍历树形节点
     * @param client opc ua client
     * @param uaNode 节点
     */
    public  List<UaNode> browseNode(OpcUaClient client, UaNode uaNode,List<UaNode> resultList) throws Exception {
        List<? extends UaNode> nodes;

        if (uaNode == null) {
            nodes = client.getAddressSpace().browseNodes(Identifiers.ObjectsFolder);
        } else {
            nodes = client.getAddressSpace().browseNodes(uaNode);
        }
        for (UaNode nd : nodes) {
            if (Objects.requireNonNull(nd.getBrowseName().getName()).contains("_")) {
                continue;
            }
            logger.info("Node is: [{}]",nd);
            resultList.add(nd);
            browseNode(client, nd,resultList);
        }
        return resultList;
    }

    /**
     * 返回节点及其节点的数据
     * @param client
     * @param uaNode
     * @param result
     * @return
     * @throws Exception
     */
    public Map<String,DataValue> browseNodeData(OpcUaClient client, UaNode uaNode, Map<String,DataValue> result) throws Exception {
        if(uaNode!=null){
            Map<String, DataValue> nodeData = getNodeData(client, uaNode);
            if(nodeData!=null){
                return nodeData;
            }
        }
        List<? extends UaNode> nodes;
        if (uaNode == null) {
            nodes = client.getAddressSpace().browseNodes(Identifiers.ObjectsFolder);
        } else {
            nodes = client.getAddressSpace().browseNodes(uaNode);
        }
        for (UaNode nd : nodes) {
            if (Objects.requireNonNull(nd.getBrowseName().getName()).contains("_")) {
                continue;
            }
            logger.info("Node is: [{}]",nd);
            Map<String, DataValue> data = getNodeData(client, nd);
            if(data!=null){
                result.putAll(data);
            }
            browseNodeData(client,nd,result);
        }
        return result;
    }

    /**
     * 读取单个节点数据
     * @param client opc ua client
     * @param nameSpaceInx nameSpaceInx
     * @param identifier identifier
     */
    public DataValue readNode(OpcUaClient client,int nameSpaceInx, String identifier ) throws Exception {
        NodeId nodeId = new NodeId(nameSpaceInx, identifier);
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();
        logger.info("identifier :[{}]  value : [{}]",nodeId.getIdentifier(),value);
        return value.copy().build();
    }

    /**
     * 读取单个节点数据
     * @param client
     * @param nodeId
     */
    public DataValue readNode(OpcUaClient client,NodeId nodeId) throws Exception {
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();
        logger.info("identifier :[{}]  value : [{}]",nodeId.getIdentifier(),value);
        return value.copy().build();
    }

    /**
     * 返回单个节点数据
     * @param client
     * @param node
     * @return
     * @throws Exception
     */
    private Map<String,DataValue> getNodeData(OpcUaClient client,UaNode node) throws Exception {
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, node.getNodeId()).get();
        if(value.getValue().getValue()==null)return null;
        Map<String,DataValue> map = new HashMap<>();
        map.put(node.getDisplayName().getText(),value);
        return map;
    }


    /**
     * 写入节点数据
     *
     * @param client opc ua client
     * @param nameSpaceInx nameSpaceInx
     * @param identifier identifier
     */
    public boolean writeNodeValue(OpcUaClient client,int nameSpaceInx, String identifier,Object value) {
        NodeId nodeId = new NodeId(nameSpaceInx, identifier);
        DataValue nowValue = new DataValue(new Variant(value), null, null);
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        return statusCode.isGood();
    }


    /**
     * 单个订阅
     * @param client
     * @param uaNode
     * @param timeInterval
     * @param consumer
     * @throws Exception
     */
    public void subscribe(OpcUaClient client, UaNode uaNode, double timeInterval, UaMonitoredItem.ValueConsumer consumer) throws Exception {
        client.getSubscriptionManager()
                .createSubscription(timeInterval)
                .thenAccept(t -> {
                    NodeId nodeId = uaNode.getNodeId();
                    ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
                    MonitoringParameters parameters = new MonitoringParameters(UInteger.valueOf(ATOMIC_INTEGER.get()), (double) timeInterval, null, UInteger.valueOf(10), true);

                    MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
                    List<MonitoredItemCreateRequest> requests = new ArrayList<>();
                    requests.add(request);
                    t.createMonitoredItems(
                            TimestampsToReturn.Both,
                            requests,
                            (item, id) -> item.setValueConsumer(consumer)
                    );
                }).get();
        Thread.sleep(Long.MAX_VALUE);
    }
}