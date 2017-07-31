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

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;

import org.flowable.app.model.common.ResultListDataRepresentation;
import org.flowable.app.model.runtime.ContentItemRepresentation;
import org.flowable.app.service.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import org.flowable.engine.ProcessEngines;
import org.flowable.engine.ProcessEngine;
import java.util.List;
import org.flowable.engine.impl.ModelReportQuery;
import org.flowable.engine.impl.ModelReport;

import org.flowable.engine.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.flowable.app.rest.runtime.FormModelReport;
import org.flowable.form.model.FormModel;
import org.flowable.form.model.FormField;
import org.flowable.form.model.ExpressionFormField;
import org.flowable.engine.HistoryService;

import java.io.File;
import java.util.ArrayList;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;

/**
 * @author Frederik Heremans
 */
@RestController
public class RelatedContentResource extends AbstractRelatedContentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedContentResource.class);

	
	@Autowired
    protected HistoryService historyService;
	
    protected ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/rest/tasks/{taskId}/content", method = RequestMethod.GET)
    public ResultListDataRepresentation getContentItemsForTask(@PathVariable("taskId") String taskId) {
        return super.getContentItemsForTask(taskId);
    }

    @RequestMapping(value = "/rest/process-instances/{processInstanceId}/content", method = RequestMethod.GET)
    public ResultListDataRepresentation getContentItemsForProcessInstance(@PathVariable("processInstanceId") String processInstanceId) {
        return super.getContentItemsForProcessInstance(processInstanceId);
    }

    @RequestMapping(value = "/rest/tasks/{taskId}/raw-content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnTask(@PathVariable("taskId") String taskId, @RequestParam("file") MultipartFile file) {
        return super.createContentItemOnTask(taskId, file);
    }

    /*
     * specific endpoint for IE9 flash upload component
     */
    @RequestMapping(value = "/rest/tasks/{taskId}/raw-content/text", method = RequestMethod.POST)
    public String createContentItemOnTaskText(@PathVariable("taskId") String taskId, @RequestParam("file") MultipartFile file) {
        ContentItemRepresentation contentItem = super.createContentItemOnTask(taskId, file);
        String contentItemJson = null;
        try {
            contentItemJson = objectMapper.writeValueAsString(contentItem);
        } catch (Exception e) {
            LOGGER.error("Error while processing ContentItem representation json", e);
            throw new InternalServerErrorException("ContentItem on task could not be saved");
        }

        return contentItemJson;
    }

    @RequestMapping(value = "/rest/tasks/{taskId}/content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnTask(@PathVariable("taskId") String taskId, @RequestBody ContentItemRepresentation contentItem) {
        return super.createContentItemOnTask(taskId, contentItem);
    }

    @RequestMapping(value = "/rest/processes/{processInstanceId}/content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnProcessInstance(@PathVariable("processInstanceId") String processInstanceId, @RequestBody ContentItemRepresentation contentItem) {
        return super.createContentItemOnProcessInstance(processInstanceId, contentItem);
    }

    @RequestMapping(value = "/rest/process-instances/{processInstanceId}/raw-content", method = RequestMethod.POST)
    public ContentItemRepresentation createContentItemOnProcessInstance(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("file") MultipartFile file) {
        return super.createContentItemOnProcessInstance(processInstanceId, file);
    }

    /*
     * specific endpoint for IE9 flash upload component
     */
    @RequestMapping(value = "/rest/process-instances/{processInstanceId}/raw-content/text", method = RequestMethod.POST)
    public String createContentItemOnProcessInstanceText(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("file") MultipartFile file) {
        ContentItemRepresentation contentItem = super.createContentItemOnProcessInstance(processInstanceId, file);

        String contentItemJson = null;
        try {
            contentItemJson = objectMapper.writeValueAsString(contentItem);
        } catch (Exception e) {
            LOGGER.error("Error while processing ContentItem representation json", e);
            throw new InternalServerErrorException("ContentItem on process instance could not be saved");
        }

        return contentItemJson;
    }

    @RequestMapping(value = "/rest/content/raw", method = RequestMethod.POST)
    public ContentItemRepresentation createTemporaryRawContentItem(@RequestParam("file") MultipartFile file) {
        return super.createTemporaryRawContentItem(file);
    }

    /*
     * specific endpoint for IE9 flash upload component
     */
    @RequestMapping(value = "/rest/content/raw/text", method = RequestMethod.POST)
    public String createTemporaryRawContentItemText(@RequestParam("file") MultipartFile file) {
        ContentItemRepresentation contentItem = super.createTemporaryRawContentItem(file);
        String contentItemJson = null;
        try {
            contentItemJson = objectMapper.writeValueAsString(contentItem);
        } catch (Exception e) {
            LOGGER.error("Error while processing ContentItem representation json", e);
            throw new InternalServerErrorException("ContentItem could not be saved");
        }

        return contentItemJson;
    }

    @RequestMapping(value = "/rest/content", method = RequestMethod.POST)
    public ContentItemRepresentation createTemporaryRelatedContent(@RequestBody ContentItemRepresentation contentItem) {
        return addContentItem(contentItem, null, null, false);
    }

    @RequestMapping(value = "/rest/content/{contentId}", method = RequestMethod.DELETE)
    public void deleteContent(@PathVariable("contentId") String contentId, HttpServletResponse response) {
        super.deleteContent(contentId, response);
    }

    @RequestMapping(value = "/rest/content/{contentId}", method = RequestMethod.GET)
    public ContentItemRepresentation getContent(@PathVariable("contentId") String contentId) {
        return super.getContent(contentId);
    }

    @RequestMapping(value = "/rest/content/{contentId}/raw", method = RequestMethod.GET)
    public void getRawContent(@PathVariable("contentId") String contentId, HttpServletResponse response) {
        super.getRawContent(contentId, response);
    }
	
	
	@RequestMapping(value = "/rest/content/{reportKey}/{processInstanceId}/report", method = RequestMethod.GET)
    public void getRawReport(@PathVariable("reportKey") String reportKey, @PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		try {
			ArrayList<String> info = new ArrayList<String>();
			if(reportKey != null && !reportKey.isEmpty()){
				List<ModelReport> modelReports = new ModelReportQuery(processEngine.getManagementService()).modelKey(reportKey).modelType("5").list();
				if(modelReports.size()>0){
					ModelReport modelReport = modelReports.get(0);
					ObjectMapper mapper = new ObjectMapper();
					FormModelReport formModel = mapper.readValue(modelReport.getModelEditorJson(), FormModelReport.class);
					List<ExpressionFormField> fields = formModel.getFields();
					List<HistoricVariableInstance> historicVariables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
					String fieldVar = "", expressionConent = "";
					for (HistoricVariableInstance historicVariableInstance : historicVariables) {
						for (ExpressionFormField field : fields) {
							fieldVar = "${"+historicVariableInstance.getVariableName()+"}";
							if(field.getExpression().contains(fieldVar)){
								expressionConent = field.getExpression().replace(fieldVar, historicVariableInstance.getValue().toString());
								info.add(expressionConent);
							}
						}
					}
				}
			}
			String url = "data/report/"+processInstanceId+"/report.pdf";
			File file = new File(url);
			file.getParentFile().mkdirs();
			if(file.getParentFile().exists()){
				try {
					this.createPdf(url, info);
					response.setContentType("application/octet-stream");
					response.setContentLength((int) file.length());
					response.setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
						
					PrintWriter out = response.getWriter();
					FileInputStream fileInputStream = new FileInputStream(url);  
					int i;   
					while ((i=fileInputStream.read()) != -1) {  
						out.write(i);   
					}   
					fileInputStream.close();   
					out.close();   
				} catch (IOException e) {
					logger.info("IOException in report");
				} catch (DocumentException e) {
					logger.info("DocumentException in report");
				}
			}
		} catch (JsonProcessingException e) {
			logger.info("JsonProcessingException in report");
		} catch (IOException e) {
			logger.info("IOException  in report");
		}
    }
	
	/*
	try {
		String k = "<html><body> This is my Project </body></html>";
		OutputStream file = new FileOutputStream(new File("C:\\Test.pdf"));
		Document document = new Document();
		PdfWriter.getInstance(document, file);
		document.open();
		HTMLWorker htmlWorker = new HTMLWorker(document);
		htmlWorker.parse(new StringReader(k));
		document.close();
		file.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	*/

	void createPdf(String dest, ArrayList<String> info) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC);
        Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        Chunk chunk = new Chunk("Relatório", chapterFont);
        Chapter chapter = new Chapter(new Paragraph(chunk), 1);
        chapter.setNumberDepth(0);
        
        int i = 0;
        for(;i < info.size();i++){
        	chapter.add(new Paragraph(info.get(i), paragraphFont));
        }
        document.add(chapter);
        document.close();
    }
	
	@RequestMapping(value = "/rest/content/{processKey}/{task}/{type}/hasreport", method = RequestMethod.GET)
    public String getIsReportAvalible(@PathVariable("processKey") String processKey, @PathVariable("task") String task, @PathVariable("type") String type, HttpServletResponse response) {
		String reportKey = "";
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		List<ModelReport>  modelReports = new ModelReportQuery(processEngine.getManagementService()).modelKey(processKey).modelType("0").list();
		
		if(modelReports.size() > 0){
			ModelReport modelReport = modelReports.get(0);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode jsonEditorProcess = mapper.readTree(modelReport.getModelEditorJson());
				if(type.equals("process")){
					reportKey = jsonEditorProcess.get("properties").get("process_report").asText();
				} else if(type.equals("task")){
					JsonNode childShapes = jsonEditorProcess.get("childShapes");
					if (childShapes.isArray()) {
						for (final JsonNode childShape : childShapes) {
							if(childShape.get("properties").get("name").asText().equals(task)){
								reportKey = childShape.get("properties").get("process_report").asText();
								break;
							}
						}
					}
				}
			} catch (JsonProcessingException e) {
				logger.info("JsonProcessingException in report");
			} catch (IOException e) {
				logger.info("IOException  in report");
			}
		}
		return "{\"reportKey\":\""+reportKey+"\"}";
	}
}
