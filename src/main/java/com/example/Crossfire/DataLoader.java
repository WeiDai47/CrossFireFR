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

    public DataLoader(ContestantRepository contestantRepo,
                      RodeoEventRepository eventRepo,
                      FantasyContestRepository contestRepo,
                      RodeoEventService eventService,
                      UserRepository userRepo) {
        this.contestantRepo = contestantRepo;
        this.eventRepo = eventRepo;
        this.contestRepo = contestRepo;
        this.eventService = eventService;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- 1. MASTER CONTESTANTS (The Global Pool) ---
        if (contestantRepo.count() == 0) {
            seedAllContestants();
        }

        if (eventRepo.count() == 0) {
            List<Contestant> allPool = contestantRepo.findAll();

            // --- 2. THE NFR (Las Vegas) ---
            createRodeoWithContests("NFR 2025 - Round 1", "Las Vegas, NV",
                    LocalDate.of(2025, 12, 4), allPool, 50000.0);

            // --- 3. CALIFORNIA FINALS RODEO ---
            createRodeoWithContests("California Finals Rodeo", "Red Bluff, CA",
                    LocalDate.of(2025, 10, 15), getRandomRoster(allPool, 10), 45000.0);

            // --- 4. PENDLETON ROUND-UP ---
            createRodeoWithContests("Pendleton Round-Up", "Pendleton, OR",
                    LocalDate.of(2025, 9, 13), getRandomRoster(allPool, 12), 60000.0);

            // --- 5. CHEYENNE FRONTIER DAYS ---
            createRodeoWithContests("Cheyenne Frontier Days", "Cheyenne, WY",
                    LocalDate.of(2025, 7, 20), getRandomRoster(allPool, 15), 55000.0);

            System.out.println("All Major Rodeos and Fantasy Contests Loaded.");
        }
    }

    /**
     * Helper to create a Rodeo Event and 2 distinct Fantasy Contests for it.
     */
    private void createRodeoWithContests(String name, String loc, LocalDate date, List<Contestant> roster, double cap) {
        RodeoEvent event = new RodeoEvent();
        event.setEventName(name);
        event.setLocation(loc);
        event.setEventDate(date);
        event.setEventStatus("UPCOMING");
        eventRepo.save(event);

        // Contest A: High Stakes
        FantasyContest highStakes = new FantasyContest();
        highStakes.setContestName(name + " - High Stakes");
        highStakes.setRodeoEvent(event);
        highStakes.setSalaryCap(cap);
        highStakes.setEntryFee(new BigDecimal("100.00"));
        highStakes.setPrizePool(new BigDecimal("10000.00"));
        eventService.initializeContestRoster(highStakes, roster);

        // Contest B: Casual / Free Play
        FantasyContest casual = new FantasyContest();
        casual.setContestName(name + " - Casual Play");
        casual.setRodeoEvent(event);
        casual.setSalaryCap(cap + 5000); // Slightly easier cap for casuals
        casual.setEntryFee(new BigDecimal("5.00"));
        casual.setPrizePool(new BigDecimal("250.00"));
        eventService.initializeContestRoster(casual, roster);
    }

    /**
     * Shuffles the global pool and returns a subset to simulate "Unique" rosters for different rodeos.
     */
    private List<Contestant> getRandomRoster(List<Contestant> pool, int sizePerCategory) {
        Collections.shuffle(pool);
        // We still need at least one per category, so we just return the shuffled pool
        // In a real app, you'd filter more strictly by performance ranking.
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