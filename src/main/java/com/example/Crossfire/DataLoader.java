package com.example.Crossfire;

import com.example.Crossfire.repository.ContestSetupRepo;
import com.example.Crossfire.repository.ContestantRepository;
import com.example.Crossfire.repository.PayoutRepository;
import com.example.Crossfire.repository.RodeoEventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ContestantRepository contestantRepo;
    private final RodeoEventRepository eventRepo;
    private final PayoutRepository payoutRepo;
    private final ContestSetupRepo configRepo;

    public DataLoader(ContestantRepository contestantRepo, RodeoEventRepository eventRepo,
                      PayoutRepository payoutRepo, ContestSetupRepo configRepo) {
        this.contestantRepo = contestantRepo;
        this.eventRepo = eventRepo;
        this.payoutRepo = payoutRepo;
        this.configRepo = configRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (contestantRepo.count() == 0) {
            // Seeding diverse contestants for each discipline
            seedCategory("Barrels", List.of("Brittany Pozzi Tonozzi", "Jordon Briggs", "Hailey Kinsel", "Emily Beisel"));
            seedCategory("Bareback", List.of("Keenan Hayes", "Rockie Patterson", "Tim O'Connell", "Clayton Biglow"));
            seedCategory("Team Roping", List.of("Tyler Wade/Wesley Thorp", "Nelson Wyatt/Chase Tryan", "Dustin Egusquiza/Levi Lord"));
            seedCategory("Steer Wrestling", List.of("Dalton Massey", "Jesse Brown", "Tyler Waguespack", "Will Lummus"));
            seedCategory("Saddle Bronc", List.of("Stetson Wright", "Sage Newman", "Zeke Thurston", "Kade Bruno"));
            seedCategory("Calf Roping", List.of("Riley Webb", "Shad Mayfield", "Ty Harris", "Cory Solomon"));
            seedCategory("Bull Riding", List.of("Ky Hamilton", "Josh Frost", "Sage Kimzey", "Tristen Hutchings"));

            System.out.println("Diverse rodeo contestant roster loaded!");
        }

        if (eventRepo.count() == 0) {
            seedFullRodeoEvent();
        }
    }

    private void seedCategory(String category, List<String> names) {
        for (String name : names) {
            Contestant c = new Contestant();
            c.setName(name);
            c.setEventType(category);
            // Randomize salaries slightly for a more realistic draft experience
            c.setSalary(8000.0 + (Math.random() * 5000.0));
            c.setBio("Top tier competitor in " + category);
            contestantRepo.save(c);
        }
    }

    private void seedFullRodeoEvent() {
        RodeoEvent nfr = new RodeoEvent();
        nfr.setEventName("National Finals Rodeo - Las Vegas");
        nfr.setEventDate(LocalDate.of(2026, 12, 5));
        nfr.setSalaryCap(50000.00);
        nfr.setEntryFee(new BigDecimal("50.00"));
        nfr.setPrizePool(new BigDecimal("25000.00"));
        nfr.setEventStatus("UPCOMING");

        RodeoEvent saved = eventRepo.save(nfr);

        // Add specific discipline limits
        saveConfig(saved, "Team Roping", 20);
        saveConfig(saved, "Bull Riding", 10);
        saveConfig(saved, "Barrels", 15);

        System.out.println("Complex Rodeo Event initialized.");
    }

    private void saveConfig(RodeoEvent event, String category, int slots) {
        ContestSetup config = new ContestSetup();
        config.setCategoryName(category);
        config.setSlotsAvailable(slots);
        config.setRodeoEvent(event);
        configRepo.save(config);
    }
}