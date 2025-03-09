package com.example.myFirstProject.controller;

import com.example.myFirstProject.entity.JournalEntry;
import com.example.myFirstProject.entity.User;
import com.example.myFirstProject.service.JournalEntryService;
import com.example.myFirstProject.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {


    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("{userName}")
    public ResponseEntity<?> getAllJournalEntiresOfUser(@PathVariable String userName){
        User user=userService.findByUserName(userName);
        List<JournalEntry> all=user.getJournalEntries();
        if(all!=null && !all.isEmpty()){
            return new ResponseEntity<>(all,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> CreateEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName){
        try {

            journalEntryService.saveEntry(myEntry,userName);
            return new ResponseEntity<>(myEntry,HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getjournalEntryById(@PathVariable ObjectId myId){
        Optional<JournalEntry> journalEntry=journalEntryService.findBYId(myId);
        if(journalEntry.isPresent()){
            return new ResponseEntity<>(journalEntry.get(), HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(HttpStatusCode.valueOf(404));
    }

    @DeleteMapping("id/{userName}/{myId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId myId,@PathVariable String username){
          journalEntryService.deleteById(myId,username);
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PutMapping("id/{userName}/{myId}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId id, @RequestBody JournalEntry myEntry,
                                               @PathVariable String userName){

        JournalEntry old=journalEntryService.findBYId(id).orElse(null);
        if(old!=null){
           old.setTitle(myEntry.getTitle()!=null && !myEntry.getTitle().equals("")?myEntry.getTitle():old.getTitle());
           old.setContent(myEntry.getContent()!=null && !myEntry.equals("")?myEntry.getContent(): old.getContent());
            journalEntryService.saveEntry(old);
            return new ResponseEntity<>(old,HttpStatus.OK);
        }

       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
