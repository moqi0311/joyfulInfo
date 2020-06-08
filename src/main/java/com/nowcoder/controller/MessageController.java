package com.nowcoder.controller;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/9.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private int limit = 10;
    private int pageShow = 7;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @RequestParam(value = "index", defaultValue = "0") int index) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId, index*limit, limit);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);

            //分页
            List<Integer> pageList = messageService.getPagesList(localUserId, index, limit, pageShow);

            model.addAttribute("doman", "/msg/list?index=");
            model.addAttribute("pages", pageList);
            model.addAttribute("curpage", index);
            model.addAttribute("lastpages", pageList.get(pageList.size()-1));
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @Param("conversationId") String conversationId ,
                                     @RequestParam(value = "index", defaultValue = "0") int index) {
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, index*limit, limit);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);

            //异步处理已读信息
            eventProducer.fireEvent(new EventModel(EventType.MESSAGE)
                    .setActorId(hostHolder.getUser().getId()).setExt("conversationId",conversationId));


            //分页
            List<Integer> pageList = messageService.getPagesList(conversationId, index, limit, pageShow);

            model.addAttribute("doman", "/msg/detail?conversationId="+conversationId+"&index=");
            model.addAttribute("pages", pageList);
            model.addAttribute("curpage", index);
            model.addAttribute("lastpages", pageList.get(pageList.size()-1));
        } catch (Exception e) {
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }


    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            Map<String, Object> map = new HashMap<>();
            if (hostHolder.getUser() == null) {
                map.put("msgname", "未登录");
                return ToutiaoUtil.getJSONString(999, map);
            }

            User user = userService.selectByName(toName);
            if (user == null) {
                map.put("msgname", "用户不存在");
                return ToutiaoUtil.getJSONString(1, map);
            }

            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);
            return ToutiaoUtil.getJSONString(0);

        } catch (Exception e) {
            logger.error("发送消息失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发信失败");
        }
    }

//    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
//    @ResponseBody
//    public String addMessage(@RequestParam("fromId") int fromId,
//                             @RequestParam("toId") int toId,
//                             @RequestParam("content") String content) {
//        try {
//            Message msg = new Message();
//            msg.setContent(content);
//            msg.setFromId(fromId);
//            msg.setToId(toId);
//            msg.setCreatedDate(new Date());
//            //msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
//            messageService.addMessage(msg);
//            return ToutiaoUtil.getJSONString(msg.getId());
//        } catch (Exception e) {
//            logger.error("增加评论失败" + e.getMessage());
//            return ToutiaoUtil.getJSONString(1, "插入评论失败");
//        }
//    }
}
