/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.flinkx.connector.stream.outputFormat;

import com.dtstack.flinkx.util.TableUtil;

import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.binary.BinaryRowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.types.Row;

import com.dtstack.flinkx.conf.FieldConf;
import com.dtstack.flinkx.connector.stream.conf.StreamSinkConf;
import com.dtstack.flinkx.connector.stream.util.TablePrintUtil;
import com.dtstack.flinkx.outputformat.BaseRichOutputFormat;
import com.dtstack.flinkx.restore.FormatState;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * OutputFormat for stream writer
 *
 * @author jiangbo
 * @Company: www.dtstack.com
 *         具体的跟外部系统的交互逻辑
 */
public class StreamOutputFormat extends BaseRichOutputFormat {

    // streamSinkConf属性
    private StreamSinkConf streamSinkConf;
    // 该类内部自己的需要的变量
    private DynamicTableSink.DataStructureConverter converter;
    private Row lastRow;

    @Override
    protected void openInternal(int taskNumber, int numTasks) {
        // do nothing
    }

    @Override
    protected void writeSingleRecordInternal(RowData rowData) {
        Row row = new Row(rowData.getArity());
        if(rowData instanceof BinaryRowData){
            row = (Row)converter.toExternal(rowData);
        }else if(rowData instanceof GenericRowData){
            GenericRowData data = (GenericRowData) rowData;
            for (int i = 0; i < data.getArity(); i++) {
                row.setField(i, data.getField(i));
            }
        }
        if (streamSinkConf.getPrint()) {
            TablePrintUtil.printTable(row, getFieldNames());
        }
        lastRow = row;
    }

    @Override
    protected void writeMultipleRecordsInternal() {
        for (RowData row : rows) {
            writeSingleRecordInternal(row);
        }
    }

    @Override
    public FormatState getFormatState() throws Exception{
        if (lastRow != null) {
            TablePrintUtil.printTable(lastRow, getFieldNames());
        }
        return super.getFormatState();
    }

    public String[] getFieldNames(){
        String[] fieldNames = null;
        List<FieldConf> fieldConfList = streamSinkConf.getColumn();
        if(CollectionUtils.isNotEmpty(fieldConfList)){
            fieldNames = fieldConfList.stream().map(FieldConf::getName).toArray(String[]::new);
        }
        return fieldNames;
    }

    @Override
    public void notifyCheckpointComplete(long checkpointId) {
        // do nothing
    }

    @Override
    public void notifyCheckpointAborted(long checkpointId) {
        // do nothing
    }

    public void setStreamSinkConf(StreamSinkConf streamSinkConf) {
        this.streamSinkConf = streamSinkConf;
    }

    public void setConverter(DynamicTableSink.DataStructureConverter converter) {
        this.converter = converter;
    }

    // TODO 和 StreamSink重复了，看看如何删减
    @Override
    public LogicalType getLogicalType() {
        DataType dataType = TableUtil.getDataType(streamSinkConf.getColumn());
        return dataType.getLogicalType();
    }
}
