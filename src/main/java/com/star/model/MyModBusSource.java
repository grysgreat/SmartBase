package com.star.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.star.utils.ModbusConfig;
import com.star.utils.ModbusData;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.List;


public class MyModBusSource extends RichSourceFunction<String> {



    private ModbusConfig modbusConfig;


    public MyModBusSource(ModbusConfig modbusConfig) {
        this.modbusConfig = modbusConfig;
    }

    /**
     * 工厂。
     */
    static ModbusFactory modbusFactory;
    static {
        if (modbusFactory == null) {
            modbusFactory = new ModbusFactory();
        }
    }

    /**
     * 获取master
     * @return
     * @throws ModbusInitException
     */
    public  ModbusMaster getMaster() throws ModbusInitException {
        IpParameters params = new IpParameters();
        //获取的host，需用户填写，默认为localhost
        params.setHost(modbusConfig.getUrl());
        //获取的端口，需用户填写，默认为502
        params.setPort(modbusConfig.getPort());
        //
        // modbusFactory.createRtuMaster(wapper); //RTU 协议
        // modbusFactory.createUdpMaster(params);//UDP 协议
        // modbusFactory.createAsciiMaster(wrapper);//ASCII 协议
        ModbusMaster master = modbusFactory.createTcpMaster(params, false);// TCP 协议
        master.init();

        return master;
    }

    /**
     * 读取[01 Coil Status 0x]类型 开关数据
     */
    public  Boolean readCoilStatus(int slaveId, int offset)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 01 Coil Status
        BaseLocator<Boolean> loc = BaseLocator.coilStatus(slaveId, offset);
        Boolean value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 读取[02 Input Status 1x]类型 开关数据
     */
    public  Boolean readInputStatus(int slaveId, int offset)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 02 Input Status
        BaseLocator<Boolean> loc = BaseLocator.inputStatus(slaveId, offset);
        Boolean value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 读取[03 Holding Register类型 2x]模拟量数据
     */
    public  Number readHoldingRegister(int slaveId, int offset, int dataType)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 03 Holding Register类型数据读取
        BaseLocator<Number> loc = BaseLocator.holdingRegister(slaveId, offset, dataType);
        Number value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 读取[04 Input Registers 3x]类型 模拟量数据
     */
    public  Number readInputRegisters(int slaveId, int offset, int dataType)
            throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 04 Input Registers类型数据读取
        BaseLocator<Number> loc = BaseLocator.inputRegister(slaveId, offset, dataType);
        Number value = getMaster().getValue(loc);
        return value;
    }

    /**
     * 批量读取使用方法
     *
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     * @throws ModbusInitException
     */
    public  void batchRead() throws ModbusTransportException, ErrorResponseException, ModbusInitException {

        BatchRead<Integer> batch = new BatchRead<Integer>();

        batch.addLocator(0, BaseLocator.holdingRegister(1, 1, DataType.FOUR_BYTE_FLOAT));
        batch.addLocator(1, BaseLocator.inputStatus(1, 0));

        ModbusMaster master = getMaster();

        batch.setContiguousRequests(false);
        BatchResults<Integer> results = master.send(batch);
        System.out.println(results.getValue(0));
        System.out.println(results.getValue(1));
    }


    boolean running = true;

    @Override
    public void run(SourceContext sourceContext) throws Exception {
        int size_data=10;//给出数据对数量
//      设置数据一组为（type，slaveid，offset，datatype），当type为01或02时不需要datatype，在前端默认给一个值就可以，不会识别

        List<ModbusData> modbusData = new Gson().fromJson(modbusConfig.getData(), new TypeToken<List<ModbusData>>(){}.getType());

        System.out.println(modbusData);
        size_data = modbusData.size();
        int []type=new int[size_data];//设置类型（01,02,03,04），需用户填写
        int []slaveid=new int[size_data];//设置slaveid，需用户填写
        int []offset=new int[size_data];//设置读取寄存器位置,需用户填写
        int []datatype=new int[size_data];//设置03,04的数据类型,需用户填写

        boolean [] v_01=new boolean[size_data];//存放01类型
        int size_01=0;

        boolean [] v_02=new boolean[size_data];//存放02类型
        int size_02=0;

        Number [] v_03=new Number[size_data];//存放03类型
        int size_03=0;

        Number [] v_04=new Number[size_data];//存放04类型
        int size_04=0;
        //需要在此给出从前端传来的数据对
        //读取数据
        for(int i=0;i<size_data;i++)
        {
            if(modbusData.get(i).getType()==1){
                try {
                    v_01[size_01]=readCoilStatus( modbusData.get(i).getSlave_id(),modbusData.get(i).getOffset());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                } catch (ModbusInitException e) {
                    e.printStackTrace();
                }
                size_01++;
            }
            else if(modbusData.get(i).getType()==2)
            {
                try {
                    v_02[size_02]=readInputStatus(modbusData.get(i).getSlave_id(),modbusData.get(i).getOffset());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                } catch (ModbusInitException e) {
                    e.printStackTrace();
                }
                size_02++;
            }
            else if(modbusData.get(i).getType()==3){
                try {
                    v_03[size_03]=readHoldingRegister(modbusData.get(i).getSlave_id(),modbusData.get(i).getOffset(),modbusData.get(i).getDatatype());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                } catch (ModbusInitException e) {
                    e.printStackTrace();
                }
                size_03++;
            }
            else if(modbusData.get(i).getType()==4){
                try {
                    v_04[size_04]=readInputRegisters(modbusData.get(i).getSlave_id(),modbusData.get(i).getOffset(),modbusData.get(i).getDatatype());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                } catch (ModbusInitException e) {
                    e.printStackTrace();
                }
                size_04++;
            }
        }
        // 批量读取
        batchRead();

        int finalSize_01 = size_01;
        int finalSize_02 = size_02;
        int finalSize_03 = size_03;
        int finalSize_04 = size_04;
        while(running){

            for(int i = 0; i< finalSize_01; i++)
            {
                sourceContext.collect("v01"+i+": "+v_01[i]);
            }
            for(int i = 0; i< finalSize_02; i++)
            {
                sourceContext.collect("v02"+i+": "+v_02[i]);
            }
            for(int i = 0; i< finalSize_03; i++)
            {
                sourceContext.collect("v03"+i+": "+v_03[i]);
            }
            for(int i = 0; i< finalSize_04; i++)
            {
                sourceContext.collect("v04"+i+": "+v_04[i]);
            }

            Thread.sleep(1000);
        }

    }

    @Override
    public void cancel() {
        running=false;
    }
}