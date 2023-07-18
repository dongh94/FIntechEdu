package bank.card.controller;

import bank.card.dto.NewsEntity;
import bank.card.repository.INewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class NewsController {

    @Autowired
    private INewsRepository iNewsRepository;

    @RequestMapping("/news")
    public @ResponseBody Map<String, Object> news() throws Exception {
        Map<String, Object> rtnObj = new HashMap<>();

        List<NewsEntity> newsEntities = iNewsRepository.newsEntities();
        log.info("news" , newsEntities);
        rtnObj.put("news_list", newsEntities);
        return rtnObj;
    }
}
