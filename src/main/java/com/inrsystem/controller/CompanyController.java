package com.inrsystem.controller;

import com.inrsystem.annotation.Authorized;
import com.inrsystem.dao.Company;
import com.inrsystem.dao.Event;
import com.inrsystem.mapper.EventMapper;
import com.inrsystem.service.CompanyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/companies")
@Authorized(roles = {1})
public class CompanyController {
   @Resource
   private CompanyService companyService;
   @Resource
   private EventMapper eventMapper;
    @GetMapping("/test")
    public Map<String, Object> getInfo(@RequestAttribute("info") Map<String, Object> info) {
        Map<String, Object> map = new HashMap<>();
        map.put("role", info.get("role"));
        map.put("id", info.get("id"));
        return map;
    }
    @PostMapping("/postEvents")
    public void postEvents(@RequestAttribute("info") Map<String, Object> info,@RequestBody()Map<String,Object> map){
        String account = info.get("account").toString();
        Company company = companyService.getCompany(account);
        Event event =new Event();
        event.setCompanyId(company.getId());
        event.setName(map.get("event_name").toString());
        event.setDescription(map.get("description").toString());
        event.setPrice((Integer) map.get("price"));
        event.setRemark(0);
        event.setState(0);
        eventMapper.insert(event);
    }
}
