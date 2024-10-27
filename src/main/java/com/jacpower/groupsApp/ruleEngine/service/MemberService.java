package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.GroupDao;
import com.jacpower.groupsApp.dao.MemberDao;
import com.jacpower.groupsApp.dao.RoleDao;
import com.jacpower.groupsApp.model.Member;
import com.jacpower.groupsApp.model.MemberDto;
import com.jacpower.groupsApp.records.ServiceResponder;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberDao memberDao;
    private final RoleDao roleDao;
    private final GroupDao groupDao;
    private final EmailSenderService senderService;


    @Autowired
    public MemberService(MemberDao memberDao, RoleDao roleDao, GroupDao groupDao, EmailSenderService senderService) {
        this.memberDao = memberDao;
        this.roleDao = roleDao;
        this.groupDao = groupDao;
        this.senderService = senderService;
    }

    @Transactional
    public ServiceResponder createMember(Member member) {
        // check if member exists
        boolean isMemberExist = memberDao.isMemberExist(member.idNumber(), member.email());
        if (!isMemberExist) {
            boolean isUsernameTaken = memberDao.isUsernameTaken(member.username()); // check username
            if (!isUsernameTaken) {
                int memberId = memberDao.createMember(member);
                int memberUserId = memberDao.addMemberToUsers(member.username(), member.password());
                int memberRoleId = roleDao.addUserRole(memberUserId, 3);
                String groupName = groupDao.getGroupName(member.groupId());

                if (memberId > 0 && memberUserId > 0 && memberRoleId > 0 && !groupName.isEmpty()) {
                    senderService.sendWelcomeEmail(member.email(), member.firstname(), groupName, member.username(), member.password());
                    return new ServiceResponder(HttpStatus.ACCEPTED, true, "Member created successfully");
                } else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot create member");
            } else {
                return new ServiceResponder(HttpStatus.CONFLICT, false, "Username already taken");
            }
        } else {
            return new ServiceResponder(HttpStatus.CONFLICT, false, "Member already exists with the idNumber or email");
        }
    }


    public ServiceResponder getGroupMembers(int groupId) {
        List<JsonObject> groupMembers = memberDao.getGroupMembers(groupId);
        JsonArray members = Util.convertListToJsonArray(groupMembers);
        return (!members.isEmpty())
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, members)
                : new ServiceResponder(HttpStatus.NO_CONTENT, false, Json.createArrayBuilder().build());
    }

    public ServiceResponder getMemberById(int memberId) {
        Optional<JsonObject> memberDetails = memberDao.getMemberByMemberId(memberId);
        JsonObject member = memberDetails.orElseThrow(() -> new UsernameNotFoundException("member not found"));
        return new ServiceResponder(HttpStatus.ACCEPTED, true, member);
    }

    public ServiceResponder updateMemberDetails(JsonObject object) {
        MemberDto memberDto = MemberDto.fromJsonObject(object);
        int memberId = object.getInt("memberId");
        boolean isUpdated = memberDao.updateMemberDetails(memberDto, memberId);
        return (isUpdated)
                ? new ServiceResponder(HttpStatus.OK, true, "details updated successfully")
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot update details");
    }

    public ServiceResponder deactivateMember(int memberId) {
        boolean isUpdated = memberDao.deactivateMember(memberId);
        return (isUpdated)
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, "member deactivated successfully")
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot deactivate member");
    }

    public ServiceResponder activateMember(int memberId) {
        boolean isUpdated = memberDao.activateMember(memberId);
        return (isUpdated)
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, "member reActivated successfully")
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot reactivate member");
    }
}
