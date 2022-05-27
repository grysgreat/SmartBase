package com.star.model;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;


public class MyHbaseSource extends RichSourceFunction<String> {
    private String url;
    private String port;
    private String BaseName;
    private String tableName;
    private String family;

    public MyHbaseSource(String url, String port, String baseName, String tableName, String family) {
        this.url = url;
        this.port = port;
        BaseName = baseName;
        this.tableName = tableName;
        this.family = family;
    }

    private Connection connection = null;
    private ResultScanner rs = null;
    private Table table = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        org.apache.hadoop.conf.Configuration hconf = HBaseConfiguration.create();
        hconf.set("hbase.zookeeper.quorum", url+":"+port);
        hconf.set("zookeeper.znode.parent", "/hbase");
        connection = ConnectionFactory.createConnection(hconf);
    }

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        table = connection.getTable(TableName.valueOf(BaseName+":"+tableName));
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(family));
        rs = table.getScanner(scan);
        for (Result result : rs) {
            StringBuilder sb = new StringBuilder();
            List<Cell> cells = result.listCells();
            for (Cell cell : cells) {
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                sb.append(value).append("-");
            }
            String value = sb.replace(sb.length() - 1, sb.length(), "").toString();
            sourceContext.collect(value);
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void close() throws Exception {
        if (rs != null) rs.close();
        if (table != null) table.close();
        if (connection != null) connection.close();
    }
}