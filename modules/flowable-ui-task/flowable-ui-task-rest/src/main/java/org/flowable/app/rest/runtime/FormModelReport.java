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
package org.flowable.app.rest.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.form.model.ExpressionFormField;
import org.flowable.form.model.FormOutcome;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Joram Barrez
 * @author Tijs Rademakers
 */
@JsonInclude(Include.NON_NULL)
public class FormModelReport implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String name;
    protected String key;
    protected int version;
    protected List<ExpressionFormField> fields;
    protected List<FormOutcome> outcomes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Do not use this method for logical operations since it only return the top level fields. I.e. A "container" field's sub fields are not returned. For verifying and listing all fields from a form
     * use instead listAllFields().
     *
     * @return The top level fields, a container's sub fields are not returned.
     */
    public List<ExpressionFormField> getFields() {
        return fields;
    }

    public void setFields(List<ExpressionFormField> fields) {
        this.fields = fields;
    }

    public List<FormOutcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<FormOutcome> outcomes) {
        this.outcomes = outcomes;
    }
}
