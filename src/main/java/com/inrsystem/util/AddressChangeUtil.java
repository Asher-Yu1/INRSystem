package com.inrsystem.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aiden
 * @date 2023/7/28 23:51
 */
@Component
public class AddressChangeUtil {
    /**
     * 地址从String类型转化为List类型
     * @param inputString String
     * @return List<String>
     */
    public List<String> parseStringToList(String inputString) {
        List<String> urlsList = new ArrayList<>();

        // 使用正则表达式提取URL
        Pattern pattern = Pattern.compile("\"(https?://[^\"]+)\"");
        Matcher matcher = pattern.matcher(inputString);

        // 逐个匹配并添加URL到列表
        while (matcher.find()) {
            String url = matcher.group(1);
            urlsList.add(url);
        }

        return urlsList;
    }

    /**
     * 地址从List类型转化为String类型
     * @param urlsList List<String>
     * @return String
     */
    public String listToString(List<String> urlsList) {
        StringBuilder sb = new StringBuilder();

        // Step 1: 添加方括号 "["
        sb.append("[");

        // Step 2 & 3: 遍历List中的每个URL，并添加到StringBuilder
        for (int i = 0; i < urlsList.size(); i++) {
            String url = urlsList.get(i);
            sb.append("\"").append(url).append("\""); // 添加双引号和URL
            if (i < urlsList.size() - 1) {
                sb.append(","); // 添加逗号，除了最后一个URL
            }
        }

        // Step 4: 添加方括号 "]"
        sb.append("]");

        return sb.toString();
    }


}
