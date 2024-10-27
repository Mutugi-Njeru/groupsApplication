package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.MeetingDao;
import com.jacpower.groupsApp.dao.MemberDao;
import com.jacpower.groupsApp.model.Meeting;
import com.jacpower.groupsApp.records.ServiceResponder;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService {
    private final MeetingDao meetingDao;
    private final MemberDao memberDao;
    private final EmailSenderService senderService;

    @Autowired
    public MeetingService(MeetingDao meetingDao, MemberDao memberDao, EmailSenderService senderService) {
        this.meetingDao = meetingDao;
        this.memberDao = memberDao;
        this.senderService = senderService;
    }

    public ServiceResponder addMeeting(Meeting meeting){
        //check if meeting date is in the future
        //check if other meetings are closed
        //send users email notifying them of the new added meeting
        boolean isOtherMeetingOpen= meetingDao.isOtherMeetingsClosed(meeting.groupId());
        if (!isOtherMeetingOpen){
            boolean isMeetingInFuture= Util.isMeetingDateInFuture(meeting.meetingDate());
            if (isMeetingInFuture){
                int meetingId=meetingDao.addMeeting(meeting);
                if (meetingId>0){
                    List<JsonObject> memberEmails = memberDao.getMemberEmails(meeting.groupId());
                    senderService.sendMeetingDetails(memberEmails, meeting); //send members meeting details
                    return new ServiceResponder(HttpStatus.OK, true, "Meeting added successfully");
                }
                else return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "cannot add meeting");
            }
            else return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "Meeting cannot be set in the past");
        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "You have another meeting open. Close to add meeting");
    }

    public ServiceResponder getGroupMeetings(int userId){
        List<JsonObject> groupMeetings = meetingDao.getGroupMeetings(userId);
        JsonArray meetings = Util.convertListToJsonArray(groupMeetings);
        return (!meetings.isEmpty())
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, meetings)
                : new ServiceResponder(HttpStatus.NO_CONTENT, false, Json.createArrayBuilder().build());
    }

    public ServiceResponder closeMeeting(int meetingId){
        boolean isClosed= meetingDao.closeMeeting(meetingId);
        return (isClosed)
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, "Meeting closed successfully")
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot close meeting");
    }
}
