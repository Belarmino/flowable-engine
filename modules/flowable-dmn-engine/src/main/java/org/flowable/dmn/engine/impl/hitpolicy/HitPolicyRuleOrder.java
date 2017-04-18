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
package org.flowable.dmn.engine.impl.hitpolicy;

import org.flowable.dmn.engine.impl.mvel.MvelExecutionContext;
import org.flowable.dmn.model.HitPolicy;
import org.flowable.engine.common.api.FlowableException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yvo Swillens
 */
public class HitPolicyRuleOrder extends AbstractHitPolicy {

    @Override
    public String getHitPolicyName() {
        return HitPolicy.RULE_ORDER.getValue();
    }

    @Override
    public void composeOutput(String outputVariableId, Object executionVariable, MvelExecutionContext executionContext) {
        Object resultVariable = executionContext.getResultVariables().get(outputVariableId);
        if (resultVariable == null) {
            resultVariable = new ArrayList<>();
        }
        if (resultVariable instanceof List) {
            ((List) resultVariable).add(executionVariable);

            // add result variable
            executionContext.getResultVariables().put(outputVariableId, resultVariable);
        } else {
            throw new FlowableException("HitPolicy RULE ORDER has wrong output variable type");
        }
    }
}
