/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.flinkx.metadatavertica.constants;

import com.dtstack.flinkx.metadata.MetaDataCons;

/**
 * 定义了一些常量属性
 * @author kunni@dtstack.com
 */
public class VerticaMetaDataCons extends MetaDataCons {

    public static final String DRIVER_NAME = "com.vertica.jdbc.Driver";

    public static final String SQL_CREATE_TIME = " SELECT table_name, create_time FROM tables WHERE table_schema = '%s' ";

    public static final String SQL_COMMENT = " SELECT object_name, comment FROM comments WHERE object_schema = '%s' ";

    public static final String SQL_TOTAL_SIZE = " SELECT anchor_table_name, used_bytes FROM projection_storage WHERE anchor_table_schema = '%s' ";

    public static final String SQL_PT_COLUMN = " SELECT table_name, partition_expression FROM tables \n" +
            "WHERE partition_expression <> '' AND table_schema = '%s' ";
}