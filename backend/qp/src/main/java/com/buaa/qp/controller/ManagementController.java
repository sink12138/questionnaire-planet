package com.buaa.qp.controller;

import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.exception.RepetitiveOperationException;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ManagementController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateService templateService;

    @PostMapping("/all")
    public Map<String, Object> all() {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();

            ArrayList<Template> results = templateService.getMyTemplates(accountId);
            ArrayList<Map<String, Object>> templates = new ArrayList<>();
            for (Template result : results) {
                if (!result.getDeleted()) {
                    Map<String, Object> templateMap = new HashMap<>();
                    templateMap.put("templateId", result.getTemplateId());
                    templateMap.put("type", result.getType());
                    templateMap.put("title", result.getTitle());
                    templateMap.put("creationTime", result.getCreationTime().toString());
                    boolean released = result.getReleased();
                    templateMap.put("released", released);
                    Time duration = result.getDuration();
                    if (released) {
                        Date releaseTime = result.getReleaseTime();
                        templateMap.put("releaseTime", releaseTime.toString());
                        duration = new Time(duration.getTime() + System.currentTimeMillis() - releaseTime.getTime());
                    }
                    templateMap.put("duration", duration.toString());
                    templates.add(templateMap);
                }
            }
            map.put("success", true);
            map.put("templates", templates);
        }
        catch (LoginVerificationException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/release")
    public Map<String, Object> release(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();

            Integer templateId;

            // Parameter checks
            try {
                templateId = (Integer) requestMap.get("templateId");
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId <= 0)
                throw new ParameterFormatException();

            Template template = templateService.getTemplate(templateId);
            if (!template.getOwner().equals(accountId))
                throw new LoginVerificationException();

            // Repetition checks
            if (template.getReleased())
                throw new RepetitiveOperationException();

            // Other checks
            if (template.getDeleted())
                throw new ExtraMessageException("无法操作已删除的问卷");

            templateService.releaseTemplate(templateId, true);
            map.put("success", true);
        }
        catch (LoginVerificationException | ParameterFormatException |
                RepetitiveOperationException | ExtraMessageException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/close")
    public Map<String, Object> close(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();

            Integer templateId;

            // Parameter checks
            try {
                templateId = (Integer) requestMap.get("templateId");
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId <= 0)
                throw new ParameterFormatException();

            Template template = templateService.getTemplate(templateId);
            if (!template.getOwner().equals(accountId))
                throw new LoginVerificationException();

            // Other checks
            if (!template.getReleased())
                throw new ExtraMessageException("问卷已处于关闭状态");
            if (template.getDeleted())
                throw new ExtraMessageException("无法操作已删除的问卷");

            templateService.releaseTemplate(templateId, false);
            map.put("success", true);
        }
        catch (LoginVerificationException | ParameterFormatException | ExtraMessageException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

}
