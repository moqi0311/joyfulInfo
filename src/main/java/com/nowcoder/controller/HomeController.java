package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Controller
public class HomeController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MailSender mailSender;

    private int limit = 3;
    private int pageShow = 5;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }

    public List<Integer> getPagesList(int userId, int curPage, int limit, int pageShow){
        List<Integer> pagesList = newsService.getPagesList(userId, curPage, limit, pageShow);

        return  pagesList;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "index", defaultValue = "0") int index,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) {

        model.addAttribute("vos", getNews(0, index*limit, limit));

        if (hostHolder.getUser() != null) {
            pop = 0;
        }
        model.addAttribute("pop", pop);

        //分页
        List<Integer> pageList = getPagesList(0, index, limit, pageShow);

        model.addAttribute("doman", "/?index=");
        model.addAttribute("pages", pageList);
        model.addAttribute("curpage", index);
        model.addAttribute("lastpages", pageList.get(pageList.size()-1));


        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model,
                            @RequestParam(value = "index", defaultValue = "0") int index,
                            @PathVariable("userId") int userId) {
        model.addAttribute("vos", getNews(userId, 0, limit));

        //分页
        List<Integer> pageList = getPagesList(userId, index, limit, pageShow);

        model.addAttribute("doman", "/user/" + userId + "?index=");
        model.addAttribute("pages", pageList);
        model.addAttribute("curpage", index);
        model.addAttribute("lastpages", pageList.get(pageList.size()-1));

        return "home";
    }


}
