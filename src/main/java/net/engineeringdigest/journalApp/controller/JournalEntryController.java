package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entry.JournalEntry;
import net.engineeringdigest.journalApp.entry.User;
import net.engineeringdigest.journalApp.services.JournalEntryService;
import net.engineeringdigest.journalApp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerv2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("{username}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String username){                                 //localhost:8080/journal GET
        User user = userService.getUserByUsername(username);
        List<JournalEntry> all=user.getJournalEntries();
        if(all!=null || !all.isEmpty()) return new ResponseEntity<>(all, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("{username}")
    public ResponseEntity<JournalEntry> createJournalEntry(@RequestBody JournalEntry journalEntry,@PathVariable String username){                     //localhost:8080/journal POST
        try {
            journalEntryService.saveEntry(journalEntry,username);
            return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryId(@PathVariable ObjectId myId){
        Optional<JournalEntry> journalEntry=journalEntryService.getJournalEntryById(myId);
        if(journalEntry.isPresent()) return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("id/{username}/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId,@PathVariable String username){
        journalEntryService.deleteEntryById(myId,username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{username}/{myId}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable ObjectId myId, @RequestBody JournalEntry journalEntry, @PathVariable String username){
        JournalEntry currEntry=journalEntryService.getJournalEntryById(myId).orElse(null);
        if(currEntry!=null){
            currEntry.setTitle(journalEntry.getTitle()!=null && !journalEntry.getTitle().equals("")?
                    journalEntry.getTitle() : currEntry.getTitle());
            currEntry.setContent(journalEntry.getContent()!=null && !journalEntry.getContent().equals("")?
                    journalEntry.getContent() : currEntry.getContent());
            journalEntryService.saveEntry(currEntry,username);
            return new ResponseEntity<>(currEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
