package com.example.Crossfire;

import com.example.Crossfire.repository.*;
import com.example.Crossfire.service.RodeoEventService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {

    private final ContestantRepository contestantRepo;
    private final RodeoEventRepository eventRepo;
    private final FantasyContestRepository contestRepo;
    private final RodeoEventService eventService;
    private final UserRepository userRepo;
    private final LiveScoreRepository liveScoreRepo; // Added for testing
    private final UserEntryRepository userEntryRepo; // Added for testing

    public DataLoader(ContestantRepository contestantRepo,
                      RodeoEventRepository eventRepo,
                      FantasyContestRepository contestRepo,
                      RodeoEventService eventService,
                      UserRepository userRepo,
                      LiveScoreRepository liveScoreRepo,
                      UserEntryRepository userEntryRepo) {
        this.contestantRepo = contestantRepo;
        this.eventRepo = eventRepo;
        this.contestRepo = contestRepo;
        this.eventService = eventService;
        this.userRepo = userRepo;
        this.liveScoreRepo = liveScoreRepo;
        this.userEntryRepo = userEntryRepo;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- MASTER SWITCH: Comment out the line below to have a BLANK database ---
        generateTestData();
    }

    private void generateTestData() {
        // 1. MASTER CONTESTANTS
        if (contestantRepo.count() == 0) {
            seedAllContestants();
        }

        if (eventRepo.count() == 0) {
            List<Contestant> allPool = contestantRepo.findAll();

            // 2. CREATE THE NFR RODEO
            RodeoEvent nfr = createRodeoWithContests("NFR 2025 - Round 1", "Las Vegas, NV",
                    LocalDate.of(2025, 12, 4), allPool, 50000.0);

            // 3. CREATE OTHER RODEOS
            createRodeoWithContests("California Finals Rodeo", "Red Bluff, CA",
                    LocalDate.of(2025, 10, 15), getRandomRoster(allPool, 10), 45000.0);

            // 4. TEST LEADERBOARD DATA (Creating Users and Scores for the NFR)
            seedLeaderboardTestData(nfr, allPool);

            System.out.println("Test Data Loaded Successfully.");
        }
    }

    private void seedLeaderboardTestData(RodeoEvent event, List<Contestant> pool) {
        // Find the NFR High Stakes contest we just made
        FantasyContest contest = contestRepo.findAll().stream()
                .filter(c -> c.getContestName().contains("NFR") && c.getContestName().contains("High Stakes"))
                .findFirst().orElse(null);

        if (contest == null) return;

        // Create 2 Test Users
        // Create 2 Test Users with required Email field
        User user1 = new User();
        user1.setUsername("RodeoKing");
        user1.setDisplayName("Rodeo King");
        user1.setEmail("king@crossfire.com"); // ADD THIS LINE
        userRepo.save(user1);

        User user2 = new User();
        user2.setUsername("BuckleBunny");
        user2.setDisplayName("Buckle Bunny");
        user2.setEmail("bunny@crossfire.com"); // ADD THIS LINE
        userRepo.save(user2);

        // Create Live Scores for 3 random athletes in this event
        Contestant c1 = pool.get(0); // e.g., Stetson Wright
        Contestant c2 = pool.get(15); // e.g., Keenan Hayes
        Contestant c3 = pool.get(30); // e.g., Wade/Thorp

        saveLiveScore(event, c1, 88.5);
        saveLiveScore(event, c2, 91.0);
        saveLiveScore(event, c3, 4.2); // Team Roping time

        // Create a User Entry for RodeoKing
        UserEntry entry1 = new UserEntry();
        entry1.setUsername("RodeoKing");
        entry1.setFantasyContest(contest);
        entry1.setUser(user1);

        // Manually adding one selection for testing
        DraftSelection selection = new DraftSelection();
        selection.setContestant(c1);
        selection.setUserEntry(entry1);
        entry1.getSelections().add(selection);

        userEntryRepo.save(entry1);
    }

    private void saveLiveScore(RodeoEvent event, Contestant c, double val) {
        LiveScore ls = new LiveScore();
        ls.setRodeoEvent(event);
        ls.setContestant(c);
        ls.setScore(val);
        ls.setOfficial(true);
        liveScoreRepo.save(ls);
    }

    // --- YOUR EXISTING METHODS BELOW ---

    private RodeoEvent createRodeoWithContests(String name, String loc, LocalDate date, List<Contestant> roster, double cap) {
        RodeoEvent event = new RodeoEvent();
        event.setEventName(name);
        event.setLocation(loc);
        event.setEventDate(date);
        event.setEventStatus("UPCOMING");
        eventRepo.save(event);

        FantasyContest highStakes = new FantasyContest();
        highStakes.setContestName(name + " - High Stakes");
        highStakes.setRodeoEvent(event);
        highStakes.setSalaryCap(cap);
        highStakes.setEntryFee(new BigDecimal("100.00"));
        highStakes.setPrizePool(new BigDecimal("10000.00"));
        eventService.initializeContestRoster(highStakes, roster);

        FantasyContest casual = new FantasyContest();
        casual.setContestName(name + " - Casual Play");
        casual.setRodeoEvent(event);
        casual.setSalaryCap(cap + 5000);
        casual.setEntryFee(new BigDecimal("5.00"));
        casual.setPrizePool(new BigDecimal("250.00"));
        eventService.initializeContestRoster(casual, roster);

        return event; // Return the event so we can link scores to it
    }

    private List<Contestant> getRandomRoster(List<Contestant> pool, int sizePerCategory) {
        Collections.shuffle(pool);
        return pool.stream().limit(100).collect(Collectors.toList());
    }

    private void seedAllContestants() {
        seedCategory("Barrels", List.of("Brittany Tonozzi", "Jordon Briggs", "Hailey Kinsel", "Emily Beisel", "Sissy Winn", "Taycie Matthews", "Lisa Lockhart", "Kassie Mowry", "Wenda Johnson", "Jessica Routier", "Summer Kosel", "Ilyssa Riley", "Stevi Hillman", "Presley Smith", "Jackie Ganter"));
        seedCategory("Bareback", List.of("Keenan Hayes", "Rockie Patterson", "Tim O'Connell", "Clayton Biglow", "Jess Pope", "Kaycee Feild", "Cole Reiner", "Tilden Hooper", "Rocker Steiner", "Leighton Berry", "Garrett Shadbolt", "Caleb Bennett", "Orin Larsen", "Tanner Aus", "Mason Clements"));
        seedCategory("Team Roping", List.of("Wade/Thorp", "Egusquiza/Lord", "Wyatt/Tryan", "Driggers/Nogueira", "Crawford/Medlin", "Proctor/Long", "Summers/Collier", "Smith/Eaves", "Lovell/Eiguren", "Tsinigine/Cull", "Snow/Thorp", "Begay/Petska", "Sartain/Rogers", "Thornton/Yates", "Hall/Tryan"));
        seedCategory("Steer Wrestling", List.of("Dalton Massey", "Jesse Brown", "Tyler Waguespack", "Will Lummus", "Stan Branco", "JD Struxness", "Dakota Eldridge", "Dirk Tavenner", "Bridger Anderson", "Cody Devers", "Stephen Culling", "Tanner Brunner", "Nick Guy", "Trell Etbauer", "Rowdy Parrott"));
        seedCategory("Saddle Bronc", List.of("Stetson Wright", "Sage Newman", "Zeke Thurston", "Kade Bruno", "Wyatt Casper", "Brody Cress", "Lefty Holman", "Tanner Butner", "Ryder Wright", "Chase Brooks", "Layton Green", "Shorty Garrett", "Logan Hay", "Ben Andersen", "Dawson Hay"));
        seedCategory("Calf Roping", List.of("Riley Webb", "Shad Mayfield", "Ty Harris", "Cory Solomon", "Westyn Hughes", "Caleb Smidt", "Haven Meged", "Hunter Herrin", "Shane Hanchey", "Tuf Cooper", "Blane Cox", "John Douch", "Jake Pratt", "Beau Cooper", "Zack Jongbloed"));
        seedCategory("Bull Riding", List.of("Ky Hamilton", "Josh Frost", "Sage Kimzey", "Tristen Hutchings", "Stetson Wright", "Trey Holston", "Jeff Askey", "Creek Young", "Trey Benton III", "Jared Parsonage", "Cullen Telfer", "Jordan Hansen", "Cody Teel", "Toby Collins", "Hayes Weight"));
    }

    private void seedCategory(String category, List<String> names) {
        for (String name : names) {
            Contestant c = new Contestant();
            c.setName(name);
            c.setEventType(category);
            c.setSalary(Math.round(4000.0 + (Math.random() * 5500.0)));
            contestantRepo.save(c);
        }
    }
}