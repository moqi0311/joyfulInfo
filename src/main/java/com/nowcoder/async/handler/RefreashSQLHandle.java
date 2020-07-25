package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class RefreashSQLHandle implements EventHandler {

    @Autowired
    NewsService newsService;

    @Override
    public void doHandle(EventModel model) {
        int newsId = model.getEntityId();
        int likeCount = Integer.valueOf(model.getExt("likeCount"));

        newsService.updateLikeCount(newsId, likeCount);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKESQL);
    }
}
