package com.example.java26.week3.rest.demo2.controller;

import com.example.java26.week3.rest.demo2.domain.UserInfo;
import com.example.java26.week3.rest.demo2.domain.UserRequestDTO;
import com.example.java26.week3.rest.demo2.serice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserRequestDTO> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping(params = "id")
    public ResponseEntity<Map<String, List<UserInfo>>> getUsersIdLargerThan(String id) {
        return new ResponseEntity<>(userService.filterUsersById(id), HttpStatus.OK);
    }

}

/**
 *  TODO
 *      1. exception
 *      2. log
 *      3. documentation (swagger)
 *      4. rest template timeout
 *      5. 3rd party api no response
 *          a. retry(timeout + times)
 *          b. cache / database + scheduler
 *              user -read-> cache / db  -if cannot find data-> 3rd party api
 *          c. circuit breaker
 *              user -> circuit breaker(on) -> return default message
 *                             | (backend thread)
 *                             --> try to visit 3rd party api -> cannot connect -> do nothing
 *                                          |
 *                                          --> can visit -> turn it off
 *
 *
 * * * * * * * * * * * * * * * * * * * * *
 * TDD(test driven development)
 *      1. requirement
 *      2. corner cases
 *      3. design flow / OOD / interface / abstract class / algorithm / api
 *      4. write unit test cases + integration test cases
 *      5. impl TODO
 *      6. run test cases
 *      7. git push -> pull request code review -> merge -> trigger jenkins pipeline -> development env / qa env / dev env
 *
 *
 * * * * * * * * * * * * * * * * * * * *
 * /user/{id @PathVariable}/courses?x=x @RequestParam
 *
 * /user/{id}/courses/{c_id}
 * * * * * * * * * * * * * * * * * * * *
 * 1. microservice
 *         user
 *          |
 *      serverA      <->      serverB(3rd party company)
 *
 *
 *         user
 *          |
 *         serverA
 *          /     \
 *      serverB   serverC
 * * * * * * * * * * * * * * * * * * * *
 *
 *   /teacher/{t_id}/student/{s_id}
 *   /junction-table/{id}
 *   /junction-table?xxx=
 *
 */