package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.AccountDao;
import com.jacpower.groupsApp.dao.AttendanceDao;
import com.jacpower.groupsApp.dao.InventoryDao;
import com.jacpower.groupsApp.dao.MemberDao;
import com.jacpower.groupsApp.model.Attendance;
import com.jacpower.groupsApp.model.AttendanceUpdateDto;
import com.jacpower.groupsApp.records.ServiceResponder;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceDao attendanceDao;
    private final MemberDao memberDao;
    private final InventoryDao inventoryDao;
    private final AccountDao accountDao;

    @Autowired
    public AttendanceService(AttendanceDao attendanceDao, MemberDao memberDao, InventoryDao inventoryDao, AccountDao accountDao) {
        this.attendanceDao = attendanceDao;
        this.memberDao = memberDao;
        this.inventoryDao = inventoryDao;
        this.accountDao = accountDao;
    }

    public ServiceResponder addAttendance(Attendance attendance) {
        boolean isMemberActive = memberDao.isMemberActive(attendance.memberId());
        if (isMemberActive) {
            boolean isMeetingOpen = attendanceDao.isMeetingOpen(attendance.meetingId());
            if (isMeetingOpen) {
                boolean isAttended = attendanceDao.hasMemberAttendedMeeting(attendance.meetingId(), attendance.memberId());
                if (!isAttended) {
                    int attendanceId = attendanceDao.addMeetingAttendance(attendance);
                    return (attendanceId > 0)
                            ? new ServiceResponder(HttpStatus.ACCEPTED, true, "attendance added successfully")
                            : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot add attendance");
                } else
                    return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "you have already attended this meeting");

            } else
                return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "The meeting is currently closed for attendance");
        } else
            return new ServiceResponder(HttpStatus.FORBIDDEN, false, "Your account is inactive. Contact system administrator");
    }

    public ServiceResponder getAttendanceDetails(int meetingId){
        List<JsonObject> attendanceDetails = attendanceDao.getAttendanceDetails(meetingId);
        JsonArray attendanceArray = Util.convertListToJsonArray(attendanceDetails);
        return (!attendanceArray.isEmpty())
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, attendanceArray)
                : new ServiceResponder(HttpStatus.NO_CONTENT, false, Json.createArrayBuilder().build());
    }
    @Transactional
    public ServiceResponder updateAttendanceDetails(AttendanceUpdateDto updateDto){
        boolean isUpdated= attendanceDao.updateAttendanceDetails(updateDto.amount(), updateDto.presence(), updateDto.attendanceId());
        String fullName= memberDao.getMemberFullName(updateDto.memberId());
        if (isUpdated){
            int inventoryId= inventoryDao.updateContributionToInventory(updateDto.groupId(), fullName, updateDto.amount());
            if (inventoryId>0){
                int accountBalance= inventoryDao.getBalanceFromInventory(updateDto.groupId());
                boolean balanceUpdated= accountDao.updateAccountBalance(accountBalance, updateDto.groupId());
                return (balanceUpdated)
                        ? new ServiceResponder(HttpStatus.ACCEPTED, true, "operation successful")
                        : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "operation failed");
            }
            else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot add record to inventory");
        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot update attendance details");






        // insert into inventory- group_id, fullName, amount
        //we get groupId by userId on frontend
        // get full name by memberId

    }
}
