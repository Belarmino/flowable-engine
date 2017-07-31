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

package org.flowable.idm.engine.impl.cmd;

import java.io.Serializable;

import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.idm.api.PrivilegeQuery;
import org.flowable.idm.engine.impl.util.CommandContextUtil;

/**
 * @author Joram Barrez
 */
public class CreatePrivilegeQueryCmd implements Command<PrivilegeQuery>, Serializable {

    private static final long serialVersionUID = 1L;

    public PrivilegeQuery execute(CommandContext commandContext) {
        return CommandContextUtil.getPrivilegeEntityManager(commandContext).createNewPrivilegeQuery();
    }
}
