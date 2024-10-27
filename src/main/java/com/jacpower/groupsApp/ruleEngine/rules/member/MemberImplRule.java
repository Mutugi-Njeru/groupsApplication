package com.jacpower.groupsApp.ruleEngine.rules.member;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Member;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.MemberService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class MemberImplRule implements ServiceRule {
    private final MemberService memberService;

    @Autowired
    public MemberImplRule(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.MEMBER.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case CREATE_MEMBER:
                return Util.buildResponse(memberService.createMember(Member.fromJsonObject(requestBody)));
            case GET_GROUP_MEMBERS:
                return Util.buildResponse(memberService.getGroupMembers(requestBody.getInt("groupId")));
            case GET_MEMBER_BY_ID:
                return Util.buildResponse(memberService.getMemberById(requestBody.getInt("memberId")));
            case UPDATE_MEMBER_DETAILS:
                return Util.buildResponse(memberService.updateMemberDetails(requestBody));
            case DEACTIVATE_MEMBER:
                return Util.buildResponse(memberService.deactivateMember(requestBody.getInt("memberId")));
            case ACTIVATE_MEMBER:
                return Util.buildResponse(memberService.activateMember(requestBody.getInt("memberId")));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
