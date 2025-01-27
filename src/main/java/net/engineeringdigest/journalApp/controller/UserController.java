package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entry.JournalEntry;
import net.engineeringdigest.journalApp.services.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping
    public ResponseEntity<?> getAll(){                                                                                  //localhost:8080/journal GET
        List<JournalEntry> all=journalEntryService.getAllEntries();
        if(all!=null || !all.isEmpty()) return new ResponseEntity<>(all, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createJournalEntry(@RequestBody JournalEntry journalEntry){                     //localhost:8080/journal POST
        try {
            journalEntryService.saveEntry(journalEntry);
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

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable ObjectId myId){
        journalEntryService.deleteEntryById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{myId}")
    public ResponseEntity<?> updateJournalEntry(@PathVariable ObjectId myId, @RequestBody JournalEntry journalEntry){
        JournalEntry currEntry=journalEntryService.getJournalEntryById(myId).orElse(null);
        if(currEntry!=null){
            currEntry.setTitle(journalEntry.getTitle()!=null && !journalEntry.getTitle().equals("")?
                    journalEntry.getTitle() : currEntry.getTitle());
            currEntry.setContent(journalEntry.getContent()!=null && !journalEntry.getContent().equals("")?
                    journalEntry.getContent() : currEntry.getContent());
            journalEntryService.saveEntry(currEntry);
            return new ResponseEntity<>(currEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
