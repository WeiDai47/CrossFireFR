package com.example.Crossfire;

import com.example.Crossfire.repository.*;
import com.example.Crossfire.service.RodeoEventService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final ContestantRepository contestantRepo;
    private final RodeoEventRepository eventRepo;
    private final FantasyContestRepository contestRepo;
    private final RodeoEventService eventService;
    private final UserRepository userRepo;
    private final LiveScoreRepository liveScoreRepo;
    private final UserEntryRepository userEntryRepo;
    private final ContestSetupRepo setupRepo;

    public DataLoader(ContestantRepository contestantRepo,
                      RodeoEventRepository eventRepo,
                      FantasyContestRepository contestRepo,
                      RodeoEventService eventService,
                      UserRepository userRepo,
                      LiveScoreRepository liveScoreRepo,
                      UserEntryRepository userEntryRepo,
                      ContestSetupRepo setupRepo) {
        this.contestantRepo = contestantRepo;
        this.eventRepo = eventRepo;
        this.contestRepo = contestRepo;
        this.eventService = eventService;
        this.userRepo = userRepo;
        this.liveScoreRepo = liveScoreRepo;
        this.userEntryRepo = userEntryRepo;
        this.setupRepo = setupRepo;
    }

    @Override
    @Transactional // Fixes the LazyInitializationException by keeping the Hibernate session open
    public void run(String... args) throws Exception {
        generateTestData();
    }

    private void generateTestData() {
        if (contestantRepo.count() == 0) {
            seedAllContestants();
        }
        seedAdminUser();

        if (eventRepo.count() == 0) {
            List<Contestant> allPool = contestantRepo.findAll();

            // 1. Create the NFR Event
            RodeoEvent nfr = new RodeoEvent();
            nfr.setEventName("NFR 2025 - Round 1");
            nfr.setLocation("Las Vegas, NV");
            nfr.setEventDate(LocalDate.of(2025, 12, 4));
            nfr.setEventStatus("LIVE");
            eventRepo.save(nfr);

            // 2. Create the High Stakes Contest
            FantasyContest contest = new FantasyContest();
            contest.setContestName("NFR $10k High Stakes");
            contest.setRodeoEvent(nfr);
            contest.setMaxEntriesPerUser(3);
            contest.setSalaryCap(50000.0);
            contest.setEntryFee(new BigDecimal("100.00"));
            contest.setPrizePool(new BigDecimal("10000.00"));

            // This service method initializes categories (ContestSetup) and links athletes
            eventService.initializeContestRoster(contest, allPool);

            // 3. Seed 10 Users with unique teams and high scores
            seedCompetitiveLeaderboard(nfr, contest);

            System.out.println("Demo Data Successfully Loaded: 10 Users added to " + contest.getContestName());
        }
    }
    private void seedAdminUser() {
        if (userRepo.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("givemeajob");
            admin.setDisplayName("System Admin");
            admin.setEmail("admin@crossfire.com");
            admin.setRole("ADMIN");
            admin.setBalance(new BigDecimal("0.00"));
            userRepo.save(admin);
            System.out.println("Admin User Created: admin / admin123");
        }
    }

    private void seedCompetitiveLeaderboard(RodeoEvent event, FantasyContest contest) {
        Random rand = new Random();
        String[] usernames = {"RodeoKing", "BuckleBunny", "TrailBoss", "DustyBoots", "CactusJack",
                "BarrelRacer99", "GritAndGlory", "LassoPro", "SteerStomper", "Outlaw2025"};

        // Fetch the setups created by the eventService (Barrels, Bareback, etc.)
        List<ContestSetup> setups = setupRepo.findAll();

        // Give every athlete in the database a high score (1200.0 - 1850.0)
        List<Contestant> allAthletes = contestantRepo.findAll();
        for (Contestant c : allAthletes) {
            int highScaleScore = 1200 + rand.nextInt(651);
            saveLiveScore(event, c, (double) highScaleScore);
        }

        for (String name : usernames) {
            // Create User Account
            User user = new User();
            user.setUsername(name);
            user.setPassword("password");
            user.setDisplayName(name);
            user.setEmail(name.toLowerCase() + "@demo.com");
            user.setBalance(new BigDecimal("500.00"));
            userRepo.save(user);

            // Create Contest Entry
            UserEntry entry = new UserEntry();
            entry.setUsername(name);
            entry.setUser(user);
            entry.setFantasyContest(contest);

            double totalSalary = 0;

            // Pick 1 unique random athlete from each required category
            for (ContestSetup setup : setups) {
                List<Contestant> eligible = setup.getEligibleContestants();
                if (!eligible.isEmpty()) {
                    Contestant pick = eligible.get(rand.nextInt(eligible.size()));

                    DraftSelection selection = new DraftSelection();
                    selection.setContestant(pick);
                    selection.setContestSetup(setup);
                    selection.setUserEntry(entry);

                    entry.getSelections().add(selection);
                    totalSalary += pick.getSalary();
                }
            }

            entry.setTotalTeamSalary(totalSalary);
            userEntryRepo.save(entry);
        }
    }

    private void saveLiveScore(RodeoEvent event, Contestant c, double val) {
        LiveScore ls = new LiveScore();
        ls.setRodeoEvent(event);
        ls.setContestant(c);
        ls.setScore(val);
        ls.setOfficial(true);
        liveScoreRepo.save(ls);
    }

    private void seedAllContestants() {
        seedCategory("Barrels", List.of("Brittany Tonozzi", "Jordon Briggs", "Hailey Kinsel", "Emily Beisel", "Sissy Winn"));
        seedCategory("Bareback", List.of("Keenan Hayes", "Rockie Patterson", "Tim O'Connell", "Clayton Biglow", "Jess Pope"));
        seedCategory("Team Roping", List.of("Wade/Thorp", "Egusquiza/Lord", "Wyatt/Tryan", "Driggers/Nogueira", "Crawford/Medlin"));
        seedCategory("Steer Wrestling", List.of("Dalton Massey", "Jesse Brown", "Tyler Waguespack", "Will Lummus", "Stan Branco"));
        seedCategory("Saddle Bronc", List.of("Stetson Wright", "Sage Newman", "Zeke Thurston", "Kade Bruno", "Wyatt Casper"));
        seedCategory("Calf Roping", List.of("Riley Webb", "Shad Mayfield", "Ty Harris", "Cory Solomon", "Westyn Hughes"));
        seedCategory("Bull Riding", List.of("Ky Hamilton", "Josh Frost", "Sage Kimzey", "Tristen Hutchings", "Stetson Wright"));
    }

    private void seedCategory(String category, List<String> names) {
        Random rand = new Random();
        for (String name : names) {
            Contestant c = new Contestant();
            c.setName(name);
            c.setEventType(category);
            // Salaries between 6,000 and 9,000
            c.setSalary(6000.0 + (rand.nextDouble() * 3000.0));
            contestantRepo.save(c);
        }
    }
}