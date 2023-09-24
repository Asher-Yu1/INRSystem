package com.inrsystem.quart;

import com.inrsystem.service.CrowdOsAlgorithmService;
import com.inrsystem.util.SpringUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SelectWinner extends QuartzJob{


    @Override
    public String getTypeName() {
        return "winner_select:";
    }

    @Override
    public void nextStep(Integer value)  {
        // 获胜者选择
        // 注意，这里无法使用 @Resource 注解或 @Autowired 注解自动注入
        CrowdOsAlgorithmService crowdOsAlgorithmService = (CrowdOsAlgorithmService) SpringUtils.getBean(CrowdOsAlgorithmService.class);
        try {
            crowdOsAlgorithmService.getAllowedTeam();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}