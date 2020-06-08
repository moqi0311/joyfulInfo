package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.dao.MessageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * creat by 2020.06.06
 */
@Component
public class ReadMessageHandler implements EventHandler {

    @Autowired
    MessageDAO messageDAO;

    @Override
    public void doHandle(EventModel model) {
        messageDAO.updateHasRead(model.getActorId(), model.getExt("conversationId"));
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.MESSAGE);
    }
}
