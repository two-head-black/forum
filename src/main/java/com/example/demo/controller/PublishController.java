package com.example.demo.controller;

import com.example.demo.Model.*;
import com.example.demo.cache.*;
import com.example.demo.dto.PaginationDTO;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private CampusService campusService;

    @Autowired
    private ShareService shareService;

    @GetMapping("/publish")
    public String publish(Model model, @RequestParam(name = "area", defaultValue = "discuss") String area) {
        if ("discuss".equals(area)) {
            model.addAttribute("tags", TagCache.get());
            model.addAttribute("area", "discuss");
            model.addAttribute("sessionName", "讨论区提问");
        }
        if ("recommendation".equals(area)) {
            model.addAttribute("tags", RecommendationTagCache.get());
            model.addAttribute("area", "recommendation");
            model.addAttribute("sessionName", "课程推荐");
        }
        if ("solution".equals(area)) {
            model.addAttribute("tags", SolutionTagCache.get());
            model.addAttribute("area", "solution");
            model.addAttribute("sessionName", "发表题解");
        }
        if ("record".equals(area)) {
            model.addAttribute("tags", RecordTagCache.get());
            model.addAttribute("area", "record");
            model.addAttribute("sessionName", "校园周边分享");
        }
        if ("sharing".equals(area)){
            model.addAttribute("tags", SharingTagCache.get());
            model.addAttribute("area", "sharing");
            model.addAttribute("sessionName", "资源分享");
        }
        Integer limit = 0;
        model.addAttribute("limit", limit);
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam(value = "title",required = false) String title,
                            @RequestParam(value = "description",required = false) String description,
                            @RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "id", required = false) Long id,
                            @RequestParam(value = "area",required = false) String area,
                            @RequestParam(value = "limit", required = false) Integer limit,
                            HttpServletRequest request,
                            Model model) {
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());
        model.addAttribute("area",area);
        model.addAttribute("limit", limit);
        model.addAttribute("sessionName", "发起");
        if (title == null || title == "") {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (description == null || description == "") {
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }
        /*String isValid = TagCache.filterIsValid(tag);
        if (StringUtils.isNotBlank(isValid)) {
            model.addAttribute("error", "输入非法标签"+isValid);
            return "publish";
        }*/
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        if (limit != null && limit > user.getGrade()) {
            model.addAttribute("error","权限设置超过您当前等级");
            return "publish";
        }
        userService.addGrade(1, user.getId());
        if("discuss".equals(area)) {
            Question question = new Question();
            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setCreator(user.getId());
            question.setId(id);
            question.setLimitation(limit);
            questionService.createOrUpdate(question);
        }
        else if("recommendation".equals(area)){
            Recommend recommend = new Recommend();
            recommend.setTitle(title);
            recommend.setDescription(description);
            recommend.setTag(tag);
            recommend.setCreator(user.getId());
            recommend.setId(id);
            recommend.setLimitation(limit);
            recommendationService.createOrUpdate(recommend);
            return "redirect:/recommend";
        }
        else if("solution".equals(area)) {
            CodeSolve codeSolve = new CodeSolve();
            codeSolve.setTitle(title);
            codeSolve.setDescription(description);
            codeSolve.setTag(tag);
            codeSolve.setCreator(user.getId());
            codeSolve.setId(id);
            codeSolve.setLimitation(limit);
            codeService.createOrUpdate(codeSolve);
            return "redirect:/code";
        }
        else if("record".equals(area)) {
            Campus campus = new Campus();
            campus.setTitle(title);
            campus.setDescription(description);
            campus.setTag(tag);
            campus.setCreator(user.getId());
            campus.setId(id);
            campus.setLimitation(limit);
            campusService.createOrUpdate(campus);
            return "redirect:/campus";
        }
        else if("sharing".equals(area)) {
            Share share = new Share();
            share.setTitle(title);
            share.setDescription(description);
            share.setTag(tag);
            share.setCreator(user.getId());
            share.setId(id);
            share.setLimitation(limit);
            shareService.createOrUpdate(share);
            return "redirect:/share";
        }
        return "redirect:/";
    }

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id, Model model) {
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags",TagCache.get());
        model.addAttribute("limit", question.getLimitation());
        model.addAttribute("area","discuss");
        model.addAttribute("sessionName", "讨论区");
        return "publish";
    }
}
