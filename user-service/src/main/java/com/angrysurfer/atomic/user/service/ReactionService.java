package com.angrysurfer.atomic.user.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.user.model.Reaction;
import com.angrysurfer.atomic.user.repository.ReactionRepository;

@Service
public class ReactionService {

    private static final Logger log = LoggerFactory.getLogger(ReactionService.class);
    private final ReactionRepository reactionRepository;

    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
        log.info("ReactionService initialized");
    }

    public String delete(Long reactionId) {
        log.info("Delete reaction id {}", reactionId);
        reactionRepository.deleteById(reactionId);
        return "redirect:/Reaction/all";
    }

    public Optional<Reaction> findById(Long reactionId) {
        log.info("Find reaction by id {}", reactionId);
        return reactionRepository.findById(reactionId);
    }

    public Set<Reaction> findAll() {
        log.info("Find all reactions");
        HashSet<Reaction> result = new HashSet<>();
        reactionRepository.findAll().forEach(result::add);
        return result;
    }

    public Reaction save(Reaction n) {
        log.info("Save reaction {}", n.getId());
        return reactionRepository.save(n);
    }

    public void update(Long id) {
        log.info("Update reaction id {}", id);
        reactionRepository.deleteById(id);
    }

}