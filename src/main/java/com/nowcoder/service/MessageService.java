package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nowcoder on 2016/7/9.
 */
@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;
    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public int getConvesationUnreadCount(int userId, String conversationId) {
        return messageDAO.getConvesationUnreadCount(userId, conversationId);
    }

    public void updateHasRead(int userId, String conversationId){
        messageDAO.updateHasRead(userId, conversationId);
    }


    /**
     * 用来加载更多的页面
     * @param userId
     * @param curPage 当前页，从0开始，但最后显示到界面的是从1-n
     * @param limit
     * @param showPage 标签一次显示有几页
     * @return
     */
    public List<Integer> getPagesList(int userId, int curPage, int limit, int showPage){

        int count = messageDAO.selectCountByUserId(userId);
        return getList(count,curPage,limit,showPage);

    }

    /**
     * 用来加载更多的页面
     * @param conversationId
     * @param curPage 当前页，从0开始，但最后显示到界面的是从1-n
     * @param limit
     * @param showPage 标签一次显示有几页
     * @return
     */
    public List<Integer> getPagesList(String conversationId, int curPage, int limit, int showPage){

        int count = messageDAO.selectCountByConversationId(conversationId);
        return getList(count,curPage,limit,showPage);

    }

    /**
     * 用来加载更多的页面
     * @param curPage 当前页，从0开始，但最后显示到界面的是从1-n
     * @param limit
     * @param showPage 标签一次显示有几页
     * @return
     */
    public List<Integer> getList(int count, int curPage, int limit, int showPage){
        List<Integer> pageList = new ArrayList<>();
        int lastPage = (int)Math.ceil(count/(double)limit)-1;

        if(curPage <= showPage/2){
            for (int i = 0; i < showPage && i <= lastPage; i++) {
                pageList.add(i);
            }
        }else if(curPage >= lastPage - showPage/2){
            for (int i = lastPage; i > lastPage-showPage; i--) {
                pageList.add(i);
            }
            Collections.reverse(pageList);
        }else{
            for (int i = curPage-showPage/2; i <= curPage+showPage/2 && i <= lastPage; i++) {
                pageList.add(i);
            }
        }

        return pageList;
    }
}
