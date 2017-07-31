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
package org.flowable.engine.impl.cmd;

import java.io.Serializable;

import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;

/**
 * @author Tijs Rademakers
 */
public class GetModelEditorSourceCmd implements Command<byte[]>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String modelId;

    public GetModelEditorSourceCmd(String modelId) {
        this.modelId = modelId;
    }

    public byte[] execute(CommandContext commandContext) {
        if (modelId == null) {
            throw new FlowableIllegalArgumentException("modelId is null");
        }

        byte[] bytes = CommandContextUtil.getModelEntityManager(commandContext).findEditorSourceByModelId(modelId);

        return bytes;
    }

}
