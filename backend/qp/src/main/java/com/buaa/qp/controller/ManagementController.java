package com.buaa.qp.controller;

import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.*;
import com.buaa.qp.service.AccountService;
import com.buaa.qp.service.AnswerService;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.util.*;
import java.text.SimpleDateFormat;

@RestController
public class ManagementController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AnswerService answerService;

    @GetMapping("/all")
    public Map<String, Object> all(@RequestParam(value = "removed", required = false) String removedStr) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();

            boolean removed;
            if (removedStr == null) removed = false;
            else removed = Boolean.parseBoolean(removedStr);

            ArrayList<Template> results = templateService.getMyTemplates(accountId);
            ArrayList<Map<String, Object>> templates = new ArrayList<>();
            for (Template result : results) {
                if (result.getDeleted() == removed) {
                    Map<String, Object> templateMap = new HashMap<>();
                    Integer templateId = result.getTemplateId();
                    templateMap.put("templateId", templateId);
                    templateMap.put("type", result.getType());
                    templateMap.put("title", result.getTitle());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                    templateMap.put("creationTime", sdf.format(result.getCreationTime()));
                    boolean released = result.getReleased();
                    templateMap.put("released", released);
                    Time duration = result.getDuration();
                    if (released) {
                        Date releaseTime = result.getReleaseTime();
                        templateMap.put("releaseTime", sdf.format(releaseTime));
                        duration = new Time(duration.getTime() + System.currentTimeMillis() - releaseTime.getTime());
                    }
                    templateMap.put("duration", duration.toString());
                    templateMap.put("answerCount", answerService.countAnswers(templateId));
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
            if (template == null) {
                throw new ObjectNotFoundException();
            } else if (!Objects.equals(template.getOwner(), accountId)) {
                throw new ParameterFormatException();
            }

            // Repetition checks
            if (template.getReleased())
                throw new RepetitiveOperationException();

            // Other checks
            if (template.getDeleted())
                throw new ExtraMessageException("无法操作已删除的问卷");

            templateService.releaseTemplate(templateId, true);
            map.put("success", true);
        }
        catch (LoginVerificationException | ParameterFormatException | ObjectNotFoundException |
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
            if (template == null) {
                throw new ObjectNotFoundException();
            } else if (!Objects.equals(template.getOwner(), accountId)) {
                throw new ParameterFormatException();
            }

            // Other checks
            if (!template.getReleased())
                throw new ExtraMessageException("问卷已处于关闭状态");
            if (template.getDeleted())
                throw new ExtraMessageException("无法操作已删除的问卷");

            templateService.releaseTemplate(templateId, false);
            map.put("success", true);
        }
        catch (LoginVerificationException | ParameterFormatException |
                ExtraMessageException | ObjectNotFoundException exc) {
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

    @PostMapping("/clone")
    public Map<String, Object> clone(@RequestBody Map<String, Object> requestMap) {
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
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId < 0) {
                throw new ParameterFormatException();
            }
            Template template = templateService.getTemplate(templateId);
            if (template == null) {
                throw new ObjectNotFoundException();
            } else if (!Objects.equals(template.getOwner(), accountId)) {
                    throw new ParameterFormatException();
            } else if (template.getDeleted())
                throw new ExtraMessageException("无法操作已删除的问卷");

            // clone
            Template newTemplate = new Template(template.getType(), template.getOwner(),
                    template.getTitle(), template.getDescription(), template.getPassword(), template.getConclusion(), template.getQuota());
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            ArrayList<Question> newQuestions = new ArrayList<>();
            for (Question question : questions) {
                Question newQuestion = new Question(question.getType(), question.getStem(), question.getDescription(), question.getRequired(), question.getArgs());
                newQuestions.add(newQuestion);
            }
            Integer newTemplateId = templateService.submitTemplate(newTemplate, newQuestions);
            map.put("success", true);
            map.put("newTemplateId", newTemplateId);
        } catch (LoginVerificationException | ParameterFormatException | ObjectNotFoundException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/remove")
    public Map<String, Object> remove(@RequestBody Map<String, Object> requestMap) {
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
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId < 0) {
                throw new ParameterFormatException();
            }
            Template template = templateService.getTemplate(templateId);
            if (template == null) {
                throw new ObjectNotFoundException();
            } else if (!Objects.equals(template.getOwner(), accountId)) {
                throw new ParameterFormatException();
            } else if (template.getDeleted()) {
                throw new RepetitiveOperationException();
            }

            templateService.removeTemplate(templateId, true);
            map.put("success", true);

        } catch (LoginVerificationException | ParameterFormatException | ObjectNotFoundException | RepetitiveOperationException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = accountService.getAccountBySession(request.getSession()).getAccountId();
            Integer templateId;

            // Parameter checks
            try {
                templateId = (Integer) requestMap.get("templateId");
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId < 0) {
                throw new ParameterFormatException();
            }
            Template template = templateService.getTemplate(templateId);
            if (template == null) {
                throw new ObjectNotFoundException();
            } else if (!Objects.equals(template.getOwner(), accountId)) {
                throw new ParameterFormatException();
            } else if (!template.getDeleted()) {
                throw new ExtraMessageException("无法删除不在回收站的问卷");
            }

            templateService.deleteTemplate(templateId);
            map.put("success", true);

        } catch (LoginVerificationException | ParameterFormatException |
                ObjectNotFoundException | ExtraMessageException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/recover")
    public Map<String, Object> recover(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = accountService.getAccountBySession(request.getSession()).getAccountId();
            Integer templateId;

            // Parameter checks
            try {
                templateId = (Integer) requestMap.get("templateId");
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId < 0) {
                throw new ParameterFormatException();
            }
            Template template = templateService.getTemplate(templateId);
            if (template == null) {
                throw new ObjectNotFoundException();
            } else if (!Objects.equals(template.getOwner(), accountId)) {
                throw new ParameterFormatException();
            } else if (!template.getDeleted()) {
                throw new ExtraMessageException("无法回收未删除的问卷");
            }

            templateService.removeTemplate(templateId, false);
            map.put("success", true);

        } catch (LoginVerificationException | ParameterFormatException |
                ObjectNotFoundException | ExtraMessageException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }
}
