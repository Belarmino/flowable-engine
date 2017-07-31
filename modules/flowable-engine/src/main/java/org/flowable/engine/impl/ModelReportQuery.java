/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowable.engine.impl;

import java.util.List;
import org.flowable.engine.common.impl.Page;
import org.flowable.engine.impl.interceptor.CommandContext;
import org.flowable.engine.ManagementService;

/**
 * @author Belarmino Silva
 */

public class ModelReportQuery extends AbstractQuery<ModelReportQuery, ModelReport> {

  protected String modelKey;
  protected String modelType;

  public ModelReportQuery(ManagementService managementService) {
    super(managementService);
  }

  public ModelReportQuery modelKey(String modelKey){
    this.modelKey = modelKey;
    return this;
  }

  public ModelReportQuery modelType(String modelType){
    this.modelType = modelType;
    return this;
  }

  /*public ModelReport executeInfo(CommandContext commandContext) {
    return (ModelReport) commandContext.getDbSqlSession().selectOne("selectModelsReportByQueryCriteria", this);
  }*/
  
  @Override
  public long executeCount(CommandContext commandContext) {
    return (Long) commandContext.getDbSqlSession().selectOne("selectModelsReportByQueryCriteria", this);
  }

  @Override
  public List<ModelReport> executeList(CommandContext commandContext, Page page) {
    return commandContext.getDbSqlSession().selectList("selectModelsReportByQueryCriteria", this);
  }
}