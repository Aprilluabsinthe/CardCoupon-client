package com.cardcoupon.passbook.controller;

/**
 * PassTemplate Token Upload Controller
 */

import com.cardcoupon.passbook.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
public class TokenUploadController {
    /** redis client side */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public TokenUploadController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/upload")
    public String upload(){
        // Controller, not RestController, will return upload.html
        return "upload";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus(){
        // Controller, not RestController, will return upload.html
        return "uploadStatus";
    }

    @PostMapping("/token")
    public String tokenFileUpload(@RequestParam("merchantsId") String merchantsId,
                                  @RequestParam("passTemplateId") String passTemplateId,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes){
        // empty handing
        if(passTemplateId==null || passTemplateId==null || file.isEmpty()){
            redirectAttributes.addFlashAttribute(
                    "message","Empty: merchantsId/passTemplateId is null or file is empty");
            return "redirect:/uploadStatus";
        }

        // write redis
        try{

            File cur = new File(Constants.TOKEN_DIR + merchantsId);
            // get merchants dir or make dir
            if(!cur.exists()){
                log.info("Create File : {}", cur.mkdir());
            }

            // get from /TOKEN_DIR/merchantsId/passTemplateId
            Path path = Paths.get(Constants.TOKEN_DIR, merchantsId, passTemplateId);
            Files.write(path , file.getBytes());

            if( !writeTokeToRedis(path,passTemplateId)){
                redirectAttributes.addFlashAttribute(
                        "message","Error: Write Toke To Redis Error");
            }
            else{
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Success: you have successfully uploaded '" + file.getOriginalFilename() + "' !");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/uploadStatus";

    }


    /**
     * Write token to redis
     * @param path {@link Path}
     * @param key redis key
     * @return true/false
     */
    private boolean writeTokeToRedis(Path path, String key) {
        Set<String> tokens;
        // each line is a token
        try(Stream<String> stream = Files.lines(path)){
            tokens = stream.collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // store tokens to redis
        // not empty && size > 0
        if(!CollectionUtils.isEmpty(tokens)){
            // single pipeline
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for(String token : tokens){
                    connection.sAdd(key.getBytes(),token.getBytes());
                }
                return null;
            });
            return true;
        }

        return false;
    }
}
