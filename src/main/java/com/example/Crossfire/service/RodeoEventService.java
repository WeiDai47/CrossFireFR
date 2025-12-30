package com.example.Crossfire.service;

import com.example.Crossfire.ContestSetup;
import com.example.Crossfire.Contestant;
import com.example.Crossfire.FantasyContest;
import com.example.Crossfire.repository.ContestSetupRepo;
import com.example.Crossfire.repository.FantasyContestRepository;
import com.example.Crossfire.repository.RodeoEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RodeoEventService {

    @Autowired private RodeoEventRepository eventRepo;
    @Autowired private FantasyContestRepository contestRepo;
    @Autowired private ContestSetupRepo setupRepo;

    private static final String[] DEFAULT_CATEGORIES = {
            "Barrels", "Bareback", "Saddle Bronc", "Steer Wrestling", "Calf Roping", "Team Roping", "Bull Riding"
    };

    /**
     * This method initializes a Fantasy Contest with a full roster.
     * It ensures the hierarchy: RodeoEvent -> FantasyContest -> ContestSetup
     */
    @Transactional
    public void initializeContestRoster(FantasyContest contest, List<Contestant> globalRoster) {
        // 1. Ensure the RodeoEvent is saved first
        if (contest.getRodeoEvent().getId() == null) {
            eventRepo.save(contest.getRodeoEvent());
        }

        // 2. Save the FantasyContest
        FantasyContest savedContest = contestRepo.save(contest);

        // 3. Create the Category Slots (Setups) for this specific game
        for (String category : DEFAULT_CATEGORIES) {
            List<Contestant> eligibleForThisCategory = globalRoster.stream()
                    .filter(c -> c.getEventType().equalsIgnoreCase(category))
                    .collect(Collectors.toList());

            if (!eligibleForThisCategory.isEmpty()) {
                ContestSetup setup = new ContestSetup();
                setup.setCategoryName(category);

                // Link the slot to the saved FantasyContest
                setup.setFantasyContest(savedContest);

                // Link the athletes to this specific slot
                setup.setEligibleContestants(eligibleForThisCategory);
                setup.setSlotsAvailable(eligibleForThisCategory.size());
                setup.setMaxPickPerUser(1);

                setupRepo.save(setup);
            }
        }
    }
}