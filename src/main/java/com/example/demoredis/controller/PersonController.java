package com.example.demoredis.controller;

import com.example.demoredis.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PersonController {

    public static final String PERSON_KEY_PREFIX = "person::";
    public static final String PERSON_LIST_KEY = "person_list";
    public static final String PERSON_HASH_KEY_PREFIX = "person_hash::";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    //--------------------------------------------String Operations---------------------------------------------//

    @PostMapping("/set_value")
    public void setValue(@RequestBody Person person) {

        String key = PERSON_KEY_PREFIX + person.getId();
        redisTemplate.opsForValue().set(key, person);
    }

    @GetMapping("get_value")
    public Person getValue(@RequestParam("id") long id) {

        String key = PERSON_KEY_PREFIX + id;
        return (Person)redisTemplate.opsForValue().get(key);
    }

    //--------------------------------------------List Operations---------------------------------------------//

    @PostMapping("/lpush")
    public void lPush(@RequestBody Person person) {
        redisTemplate.opsForList().leftPush(PERSON_LIST_KEY, person);
    }

    @PostMapping("/rpush")
    public void rPush(@RequestBody Person person) {
        redisTemplate.opsForList().rightPush(PERSON_LIST_KEY, person);
    }

    @GetMapping("/lrange")
    public List<Person> lRange(
            @RequestParam("start") int start,
            @RequestParam("end") int end) {

        List<Object> personList = redisTemplate.opsForList().range(PERSON_LIST_KEY, start, end);

//        return personList.stream().map(x -> (Person)x).collect(Collectors.toList());

        List<Person> personList2 = new ArrayList<>();
        for(int i = 0; i < personList.size(); i++) {
            personList2.add((Person)personList.get(i));
        }
        return personList2;
    }

    //--------------------------------------------Hash Operations---------------------------------------------//

    @PostMapping("/hmset")
    public void hmSet(@RequestBody Person person){
        Map map = objectMapper.convertValue(person, Map.class);
        redisTemplate.opsForHash().putAll(PERSON_HASH_KEY_PREFIX + person.getId(), map);
    }

    @GetMapping("/hgetall")
    public Person hGetAll(@RequestParam("id") int id){
        Map map = redisTemplate.opsForHash().entries(PERSON_HASH_KEY_PREFIX + id);
        if(map == null || map.isEmpty()){
            return null;
        }

        return objectMapper.convertValue(map, Person.class);
    }
}
