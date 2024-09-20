/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.gluten.datasource;

import org.apache.gluten.init.JniUtils;
import org.apache.gluten.runtime.Runtime;
import org.apache.gluten.runtime.RuntimeAware;

import org.apache.spark.sql.execution.datasources.BlockStripes;

import java.util.Map;

/** The jni file is at `cpp/core/jni/JniWrapper.cc` */
// FIXME: move to module gluten-data?
public class DatasourceJniWrapper implements RuntimeAware {
  private final Runtime runtime;

  private DatasourceJniWrapper(Runtime runtime) {
    this.runtime = runtime;
  }

  public static DatasourceJniWrapper create(Runtime runtime) {
    return new DatasourceJniWrapper(runtime);
  }

  @Override
  public long rtHandle() {
    return runtime.getHandle();
  }

  public long nativeInitDatasource(String filePath, long cSchema, Map<String, String> options) {
    return nativeInitDatasource(filePath, cSchema, JniUtils.toNativeConf(options));
  }

  public native long nativeInitDatasource(String filePath, long cSchema, byte[] options);

  public native void inspectSchema(long dsHandle, long cSchemaAddress);

  public native void close(long dsHandle);

  public native void writeBatch(long dsHandle, long batchHandle);

  public native BlockStripes splitBlockByPartitionAndBucket(
      long blockAddress, int[] partitionColIndice, boolean hasBucket);
}
